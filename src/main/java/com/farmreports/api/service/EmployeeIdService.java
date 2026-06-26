package com.farmreports.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeIdService {

    private final JdbcTemplate jdbc;

    public String generateFor(String farmName) {
        String prefix = prefixFor(farmName);
        Integer seq = jdbc.queryForObject(
                "UPDATE employee_id_sequences SET last_number = last_number + 1 WHERE prefix = ? RETURNING last_number",
                Integer.class, prefix);
        return String.format("%s%04d", prefix, seq != null ? seq : 1);
    }

    public String generateLsNumber() {
        Integer seq = jdbc.queryForObject(
                "UPDATE employee_id_sequences SET last_number = last_number + 1 WHERE prefix = 'LS' RETURNING last_number",
                Integer.class);
        return "LS" + (seq != null ? seq : 2001);
    }

    private static String prefixFor(String farmName) {
        return switch (farmName) {
            case "Les A", "Les B" -> "LES";
            case "Kenlet"         -> "KEN";
            case "Matunda"        -> "MAT";
            case "Siyoi"          -> "SIY";
            default               -> "EMP";
        };
    }
}
