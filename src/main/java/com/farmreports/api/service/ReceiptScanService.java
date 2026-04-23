package com.farmreports.api.service;

import com.farmreports.api.dto.ReceiptScanResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReceiptScanService {

    @Value("${anthropic.api-key:}")
    private String apiKey;

    private static final String API_URL     = "https://api.anthropic.com/v1/messages";
    private static final String MODEL       = "claude-haiku-4-5-20251001";
    private static final String PROMPT      = """
            Extract data from this receipt image and return ONLY a valid JSON object with these exact fields:
            - day: integer (day of month from the receipt date, 1-31, or null if not visible)
            - supplier_contractor: string (vendor or supplier name, or null)
            - receipt_no: string (receipt, invoice, or reference number, or null)
            - cost: number (total amount paid — the final total, not subtotal, or null)
            - description: string (brief summary of items or services purchased, or null)

            Rules:
            - Return ONLY the raw JSON object — no markdown, no code blocks, no explanation.
            - If a field cannot be determined, use null.
            - For cost, extract the numeric value only (no currency symbols).
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient   httpClient   = HttpClient.newHttpClient();

    public ReceiptScanResult scanReceipt(MultipartFile image) throws IOException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Receipt scanning is not configured");
        }

        String base64Data = Base64.getEncoder().encodeToString(image.getBytes());
        String mediaType  = image.getContentType() != null ? image.getContentType() : "image/jpeg";

        Map<String, Object> body = Map.of(
                "model",      MODEL,
                "max_tokens", 512,
                "messages",   List.of(Map.of(
                        "role",    "user",
                        "content", List.of(
                                Map.of("type", "image",
                                        "source", Map.of(
                                                "type",       "base64",
                                                "media_type", mediaType,
                                                "data",       base64Data)),
                                Map.of("type", "text", "text", PROMPT)
                        )
                ))
        );

        String requestJson = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type",     "application/json")
                .header("x-api-key",        apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Receipt scan service returned: " + response.statusCode());
            }

            JsonNode root    = objectMapper.readTree(response.body());
            String   content = root.path("content").get(0).path("text").asText("{}").trim();

            // Strip markdown code fences if Claude wrapped the JSON
            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)```[a-zA-Z]*\\n?", "").replace("```", "").trim();
            }

            JsonNode result = objectMapper.readTree(content);

            Integer day        = result.hasNonNull("day")                ? result.get("day").asInt()                        : null;
            String  supplier   = result.hasNonNull("supplier_contractor") ? result.get("supplier_contractor").asText(null)   : null;
            String  receiptNo  = result.hasNonNull("receipt_no")          ? result.get("receipt_no").asText(null)            : null;
            Double  cost       = result.hasNonNull("cost")                ? result.get("cost").asDouble()                    : null;
            String  description = result.hasNonNull("description")        ? result.get("description").asText(null)           : null;

            return new ReceiptScanResult(day, supplier, receiptNo, cost, description);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt scan interrupted");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // JSON parse failed — return empty result so the form still opens
            return new ReceiptScanResult(null, null, null, null, null);
        }
    }
}
