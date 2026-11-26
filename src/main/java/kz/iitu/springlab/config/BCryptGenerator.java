
package kz.iitu.springlab.config;

import org.springframework.security.crypto.bcrypt

        .BCryptPasswordEncoder;

public class BCryptGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(10);

        String userHash = encoder.encode("user123");
        String adminHash = encoder.encode("admin123");
        String secondUserHash = encoder.encode("user123");

        System.out.println("=== LAB 10: BCRYPT ===");

        System.out.println("USER HASH:");
        System.out.println(userHash);

        System.out.println("ADMIN HASH:");
        System.out.println(adminHash);

        System.out.println("SECOND USER HASH:");
        System.out.println(secondUserHash);

        System.out.println("HASH LENGTH:");
        System.out.println(userHash.length());

        System.out.println("HASHES ARE DIFFERENT:");
        System.out.println(!userHash.equals(secondUserHash));

        System.out.println("PASSWORD VERIFICATION:");
        System.out.println(encoder.matches("user123", userHash));

        System.out.println("=== FINISHED ===");
    }
}
