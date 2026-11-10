package com.farmreports.api.service;

import com.farmreports.api.dto.CasualSheetDtos.NameMatch;
import com.farmreports.api.dto.CasualSheetDtos.ScanPageResult;
import com.farmreports.api.dto.CasualSheetDtos.ScannedRow;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.service.CasualSheetScanService.SheetHeading;
import com.farmreports.api.service.CasualSheetScanService.SheetPage;
import com.farmreports.api.service.CasualSheetScanService.SheetRow;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CasualSheetScanServiceTest {

    private static Employee emp(int id, String first, String last, String status) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(first);
        e.setLastName(last);
        e.setStatus(status);
        e.setLsNumber("LS" + (2000 + id) + "B");
        return e;
    }

    private final List<Employee> farm = List.of(
            emp(1, "Ronald", "Misoi", "ACTIVE"),
            emp(2, "Robert", "Misoi", "ACTIVE"),
            emp(3, "Sylvia", "Chepkosgei", "ACTIVE"),
            emp(4, "Joan", "Cherono", "ACTIVE"),
            emp(5, "Metrine", "Nafula", "INACTIVE"));

    private static SheetRow row(String no, String name, String amount, boolean signed) {
        return new SheetRow(no, name, "6", "", amount, amount + "/-", signed ? "thumbprint" : "", signed, false, "");
    }

    @Test
    void reversedNameOrderStillMatchesConfidently() {
        List<NameMatch> c = CasualSheetScanService.rankCandidates("MISOI RONALD", farm);
        assertThat(c.get(0).employeeId()).isEqualTo(1);
        assertThat(CasualSheetScanService.pickAutoMatch(c)).isEqualTo(1);
    }

    @Test
    void closeMisspellingIsSuggestedButAmbiguousOnesAreNotAutoPicked() {
        // "Jean Cherono" is one letter off "Joan Cherono" -> confident match.
        assertThat(CasualSheetScanService.pickAutoMatch(
                CasualSheetScanService.rankCandidates("Jean Cherono", farm))).isEqualTo(4);

        // Surname only: Ronald and Robert Misoi score the same -> must be picked by a person.
        List<NameMatch> c = CasualSheetScanService.rankCandidates("Misoi", farm);
        assertThat(c).extracting(NameMatch::employeeId).contains(1, 2);
        assertThat(CasualSheetScanService.pickAutoMatch(c)).isNull();
    }

    @Test
    void differentSurnameIsOnlyASuggestion() {
        List<NameMatch> c = CasualSheetScanService.rankCandidates("Sylvia Chepkurui", farm);
        assertThat(c.get(0).employeeId()).isEqualTo(3);
        assertThat(CasualSheetScanService.pickAutoMatch(c)).isNull();
    }

    @Test
    void unknownPersonHasNoMatchAndASuggestedNewName() {
        CasualSheetScanService svc = new CasualSheetScanService(null, null);
        SheetPage page = new SheetPage(true,
                new SheetHeading(true, "Detasselling", "1st week June 2026 8th-13th", "2026-06-08", "2026-06-13", "200"),
                List.of(row("1", "LONAH CHEPLETING", "1200", true),
                        row("2", "Misoi Ronald", "1000", false),
                        new SheetRow("3", "", "", "", "", "", "", false, false, "")),
                "27640", "");

        ScanPageResult r = svc.toResult("p1.jpeg", page, farm);

        assertThat(r.heading().present()).isTrue();
        assertThat(r.heading().endDate()).isEqualTo(LocalDate.of(2026, 6, 13));
        assertThat(r.heading().ratePerDay()).isEqualByComparingTo("200");
        assertThat(r.writtenTotal()).isEqualByComparingTo("27640");
        assertThat(r.rows()).hasSize(2); // blank row dropped

        ScannedRow lonah = r.rows().get(0);
        assertThat(lonah.matchedEmployeeId()).isNull();
        assertThat(lonah.suggestedFirstName()).isEqualTo("Lonah");
        assertThat(lonah.suggestedLastName()).isEqualTo("Chepleting");
        assertThat(lonah.netAmount()).isEqualByComparingTo("1200");
        assertThat(lonah.paid()).isTrue();

        ScannedRow ronald = r.rows().get(1);
        assertThat(ronald.matchedEmployeeId()).isEqualTo(1);
        assertThat(ronald.paid()).isFalse();
    }

    @Test
    void amountsAreReadFromMessyCells() {
        assertThat(CasualSheetScanService.parseAmount("1200/-")).isEqualByComparingTo("1200");
        assertThat(CasualSheetScanService.parseAmount("1,040")).isEqualByComparingTo(new BigDecimal("1040"));
        assertThat(CasualSheetScanService.parseAmount("Cleared")).isNull();
        assertThat(CasualSheetScanService.parseAmount("")).isNull();
    }
}
