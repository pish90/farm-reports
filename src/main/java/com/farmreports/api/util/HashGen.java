package com.farmreports.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Run main() to print a BCrypt hash for a given password.
 * Delete this file after use.
 */
public class HashGen {
    public static void main(String[] args) {
        String password = "changeme";
        String hash = new BCryptPasswordEncoder().encode(password);
        System.out.println("Hash for \"" + password + "\":");
        System.out.println(hash);

        // Verify it round-trips
        boolean matches = new BCryptPasswordEncoder().matches(password, hash);
        System.out.println("Verification: " + matches);
    }
}
