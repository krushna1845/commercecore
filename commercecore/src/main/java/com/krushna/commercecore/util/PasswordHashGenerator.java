package com.krushna.commercecore.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hash for user123
        String userHash = encoder.encode("user123");
        System.out.println("Hash for 'user123': " + userHash);
        
        // Generate hash for Admin@123
        String adminHash = encoder.encode("Admin@123");
        System.out.println("Hash for 'Admin@123': " + adminHash);
        
        // Generate hash for seller123
        String sellerHash = encoder.encode("seller123");
        System.out.println("Hash for 'seller123': " + sellerHash);
    }
}
