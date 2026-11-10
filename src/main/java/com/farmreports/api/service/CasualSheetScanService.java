package com.farmreports.api.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.StructuredMessage;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.farmreports.api.dto.CasualSheetDtos.NameMatch;
import com.farmreports.api.dto.CasualSheetDtos.ScanPageResult;
import com.farmreports.api.dto.CasualSheetDtos.ScannedHeading;
import com.farmreports.api.dto.CasualSheetDtos.ScannedRow;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Reads one photographed page of a handwritten weekly casual pay sheet with Claude (vision +
 * structured output), then suggests which existing employee each handwritten name refers to.
 * Nothing is written here — the admin reviews the result and saves via {@link CasualSheetImportService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CasualSheetScanService {

    @Value("${anthropic.api-key:}")
    private String apiKey;

    @Value("${anthropic.sheet-model:claude-opus-5-5}")
    private String model;

    private final EmployeeRepository employeeRepo;
    private final FarmRepository farmRepo;

    private volatile AnthropicClient client;

    private static final Set<String> SUPPORTED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    static final double AUTO_MATCH_SCORE = 0.86;
    static final double AUTO_MATCH_MARGIN = 0.08;
    static final double MIN_CANDIDATE_SCORE = 0.5;

    // ---- shape Claude must return (structured output) ----
    // Strings rather than numbers/dates so an unreadable or missing value is "" instead of a schema violation.

    public record SheetHeading(
            @JsonPropertyDescription("true if this page has a heading naming the work/activity and/or the week; false for a continuation page that starts straight with numbered rows")
            boolean present,
            @JsonPropertyDescription("Activity / job as written, e.g. 'Detasselling', 'Collecting of manure'. Empty if none.")
            String activity,
            @JsonPropertyDescription("Period exactly as written, e.g. '1st week June 2026 8th-13th'. Empty if none.")
            String periodText,
            @JsonPropertyDescription("First day of the period as YYYY-MM-DD if it can be worked out from the heading, else empty")
            String startDate,
            @JsonPropertyDescription("Last day of the period as YYYY-MM-DD if it can be worked out from the heading, else empty")
            String endDate,
            @JsonPropertyDescription("Daily rate written in the heading or column header (digits only), else empty")
            String ratePerDay) {}

    public record SheetRow(
            @JsonPropertyDescription("Row number written at the start of the line (digits only), empty if none")
            String lineNo,
            @JsonPropertyDescription("The person's name exactly as written (both words, in the written order), with your best reading of unclear letters")
            String name,
            @JsonPropertyDescription("Days worked as written (e.g. '6', '7 1/2', '3+2'), empty if blank")
            String daysWorked,
            @JsonPropertyDescription("Deduction for maize/food issued as written (digits/fraction), empty if blank")
            String deduction,
            @JsonPropertyDescription("Net amount for the row, digits only without '/-' or commas (e.g. '1200'). Empty if blank or not a number (e.g. 'Cleared').")
            String netAmount,
            @JsonPropertyDescription("The net-amount cell exactly as written, including words like 'Cleared'")
            String rawAmount,
            @JsonPropertyDescription("What is in the signature column: 'thumbprint', 'signature', 'tick', 'x', 'paid', other short words as written, or empty if blank")
            String signMark,
            @JsonPropertyDescription("true if the row has any thumbprint, signature, tick, x mark or the word paid/cleared in the sign column")
            boolean signed,
            @JsonPropertyDescription("true if the name or amount is hard to read, crossed out, overwritten or ambiguous")
            boolean uncertain,
            @JsonPropertyDescription("Short reason when uncertain, else empty")
            String uncertainReason) {}

    public record SheetPage(
            @JsonPropertyDescription("false if the photo is not a casual labour pay list at all")
            boolean isCasualPaySheet,
            SheetHeading heading,
            @JsonPropertyDescription("Every person row on the page, top to bottom, including rows with no amount")
            List<SheetRow> rows,
            @JsonPropertyDescription("A page total written at the bottom (digits only), else empty")
            String writtenTotal,
            @JsonPropertyDescription("Anything the reviewer should know, e.g. a second list/activity starting part-way down, very blurry areas. Empty if nothing.")
            String notes) {}

    private static final String PROMPT = """
            This photo is a page from a farm's handwritten casual labour pay book (Kenya, amounts in KSh).
            A sheet usually starts with a heading giving the activity/job (e.g. "Detasselling",
            "Planting of live fence and watering") and the week (e.g. "1st week June 2026 8th-13th"),
            then numbered rows: name, sometimes rate per day, days worked, maize tins issued (a
            deduction), net amount (often written like "1200/-"), and a sign column holding a
            signature, thumbprint, tick, x, or the word "paid". Continuation pages have no heading and
            just continue the numbering.

            Transcribe every person row on this page into the requested JSON. Rules:
            - Copy names as written; do not reorder or "correct" them to a different person.
            - netAmount is digits only; leave it empty when the cell is blank or a word like "Cleared"
              (put that word in rawAmount).
            - Mark a row uncertain whenever a name or amount is hard to read, crossed out or overwritten.
            - If a heading gives a date range, fill startDate/endDate (YYYY-MM-DD). Use the year written;
              if no year is written use %d.
            - If a second activity heading starts part-way down the page, still list all rows and
              explain it in notes.
            """;

    @Transactional(readOnly = true)
    public ScanPageResult scanPage(MultipartFile image, Integer farmId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Sheet scanning is not configured (ANTHROPIC_API_KEY missing)");
        }
        farmRepo.findById(farmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        String mediaType = image.getContentType() == null ? "" : image.getContentType().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_TYPES.contains(mediaType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported image type '" + mediaType + "' — upload JPEG, PNG or WebP photos");
        }

        SheetPage page = callClaude(image, mediaType);
        return toResult(image.getOriginalFilename(), page, employeeRepo.findByFarmId(farmId));
    }

    private SheetPage callClaude(MultipartFile image, String mediaType) {
        String base64;
        try {
            base64 = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the uploaded photo");
        }

        StructuredMessageCreateParams<SheetPage> params = MessageCreateParams.builder()
                .model(model)
                .maxTokens(16000L)
                .outputConfig(SheetPage.class)
                .addUserMessageOfContentBlockParams(List.of(
                        ContentBlockParam.ofImage(ImageBlockParam.builder()
                                .source(Base64ImageSource.builder()
                                        .mediaType(Base64ImageSource.MediaType.of(mediaType))
                                        .data(base64)
                                        .build())
                                .build()),
                        ContentBlockParam.ofText(TextBlockParam.builder()
                                .text(PROMPT.formatted(LocalDate.now().getYear()))
                                .build())))
                .build();

        StructuredMessage<SheetPage> response;
        try {
            response = client().messages().create(params);
        } catch (RuntimeException e) {
            log.warn("Casual sheet scan failed: {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The sheet reader is unavailable right now — try again shortly");
        }

        // Logged so the real per-photo cost can be read from the Railway logs.
        log.info("Casual sheet scan usage: model={} inputTokens={} outputTokens={}",
                model, response.usage().inputTokens(), response.usage().outputTokens());

        String stop = response.stopReason().map(Object::toString).orElse("");
        if (stop.equalsIgnoreCase("refusal")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The sheet reader declined this photo");
        }
        if (stop.equalsIgnoreCase("max_tokens")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "This page has too much on it to read in one go — crop the photo into two halves");
        }
        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(typed -> typed.text())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The sheet reader returned nothing for this photo"));
    }

    private AnthropicClient client() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = AnthropicOkHttpClient.builder().apiKey(apiKey).build();
                }
            }
        }
        return client;
    }

    // ---- mapping + name matching (package-visible for tests) ----

    ScanPageResult toResult(String fileName, SheetPage page, List<Employee> farmEmployees) {
        SheetHeading h = page.heading();
        ScannedHeading heading = h == null
                ? new ScannedHeading(false, "", "", null, null, null)
                : new ScannedHeading(h.present(), blankToEmpty(h.activity()), blankToEmpty(h.periodText()),
                        parseDate(h.startDate()), parseDate(h.endDate()), parseAmount(h.ratePerDay()));

        List<ScannedRow> rows = new ArrayList<>();
        for (SheetRow r : page.rows() == null ? List.<SheetRow>of() : page.rows()) {
            String written = blankToEmpty(r.name()).trim();
            if (written.isEmpty() && parseAmount(r.netAmount()) == null) continue;

            List<NameMatch> candidates = rankCandidates(written, farmEmployees);
            Integer matched = pickAutoMatch(candidates);
            String[] split = splitName(written);
            rows.add(new ScannedRow(
                    parseInt(r.lineNo()), written, blankToEmpty(r.daysWorked()),
                    parseAmount(r.deduction()), parseAmount(r.netAmount()), blankToEmpty(r.rawAmount()),
                    blankToEmpty(r.signMark()), r.signed(), r.uncertain(), blankToEmpty(r.uncertainReason()),
                    matched, candidates, split[0], split[1]));
        }
        return new ScanPageResult(fileName, page.isCasualPaySheet(), heading, rows,
                parseAmount(page.writtenTotal()), blankToEmpty(page.notes()));
    }

    static List<NameMatch> rankCandidates(String written, List<Employee> employees) {
        List<String> w = tokens(written);
        if (w.isEmpty()) return List.of();
        return employees.stream()
                .map(e -> new NameMatch(e.getId(), fullName(e), e.getLsNumber(),
                        "ACTIVE".equalsIgnoreCase(e.getStatus()),
                        nameScore(w, tokens(fullName(e)))))
                .filter(m -> m.score() >= MIN_CANDIDATE_SCORE)
                .sorted(Comparator.comparingDouble(NameMatch::score).reversed()
                        .thenComparing(m -> !m.active()))
                .limit(4)
                .toList();
    }

    static Integer pickAutoMatch(List<NameMatch> candidates) {
        if (candidates.isEmpty()) return null;
        NameMatch best = candidates.get(0);
        if (best.score() < AUTO_MATCH_SCORE) return null;
        if (candidates.size() > 1 && best.score() - candidates.get(1).score() < AUTO_MATCH_MARGIN) return null;
        return best.employeeId();
    }

    /**
     * Order-insensitive similarity (handles "Misoi Ronald" vs "Ronald Misoi"): each written token is
     * paired with its most similar unused candidate token; extra/missing tokens pull the score down.
     */
    static double nameScore(List<String> written, List<String> candidate) {
        if (written.isEmpty() || candidate.isEmpty()) return 0;
        List<String> pool = new ArrayList<>(candidate);
        double sum = 0;
        for (String t : written) {
            int bestIdx = -1;
            double best = 0;
            for (int i = 0; i < pool.size(); i++) {
                double s = similarity(t, pool.get(i));
                if (s > best) { best = s; bestIdx = i; }
            }
            sum += best;
            if (bestIdx >= 0) pool.remove(bestIdx);
        }
        int n = Math.max(written.size(), candidate.size());
        return sum / n;
    }

    static double similarity(String a, String b) {
        if (a.equals(b)) return 1;
        int max = Math.max(a.length(), b.length());
        return max == 0 ? 0 : 1.0 - (double) levenshtein(a, b) / max;
    }

    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] t = prev; prev = cur; cur = t;
        }
        return prev[b.length()];
    }

    static List<String> tokens(String name) {
        if (name == null) return List.of();
        return List.of(name.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").trim().split("\\s+"))
                .stream().filter(t -> !t.isBlank()).toList();
    }

    static String fullName(Employee e) {
        String last = e.getLastName() == null ? "" : " " + e.getLastName();
        return (e.getFirstName() + last).trim();
    }

    /** "LONAH CHEPLETING" -> ["Lonah", "Chepleting"]; extra words go to the last name. */
    static String[] splitName(String written) {
        List<String> parts = List.of(written.trim().split("\\s+")).stream().filter(s -> !s.isBlank())
                .map(CasualSheetScanService::capitalise).toList();
        if (parts.isEmpty()) return new String[]{"", ""};
        if (parts.size() == 1) return new String[]{parts.get(0), ""};
        return new String[]{parts.get(0), String.join(" ", parts.subList(1, parts.size()))};
    }

    private static String capitalise(String s) {
        String lower = s.toLowerCase(Locale.ROOT);
        return lower.isEmpty() ? lower : Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    static BigDecimal parseAmount(String s) {
        if (s == null) return null;
        String digits = s.replaceAll("[^0-9.]", "");
        if (digits.isEmpty() || digits.equals(".")) return null;
        try {
            return new BigDecimal(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInt(String s) {
        BigDecimal n = parseAmount(s);
        return n == null ? null : n.intValue();
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String blankToEmpty(String s) {
        return s == null ? "" : s;
    }
}
