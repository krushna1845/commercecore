package com.krushna.commercecore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.krushna.commercecore.model.Role;
import com.krushna.commercecore.model.User;
import com.krushna.commercecore.repository.RoleRepository;
import com.krushna.commercecore.repository.UserRepository;

@Service
public class RoleService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public RoleService(RoleRepository roleRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${admin.username:admin}") String adminUsername,
                       @Value("${admin.password:Admin@123}") String adminPassword) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize default roles if they don't exist
        if (!roleRepository.findByName(Role.PredefinedRole.ROLE_USER.getRoleName()).isPresent()) {
            Role userRole = new Role(Role.PredefinedRole.ROLE_USER.getRoleName());
            roleRepository.save(userRole);
            System.out.println("Created ROLE_USER");
        }

        Role adminRole = roleRepository.findByName(Role.PredefinedRole.ROLE_ADMIN.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(Role.PredefinedRole.ROLE_ADMIN.getRoleName())));

        if (!roleRepository.findByName(Role.PredefinedRole.ROLE_SELLER.getRoleName()).isPresent()) {
            roleRepository.save(new Role(Role.PredefinedRole.ROLE_SELLER.getRoleName()));
            System.out.println("Created ROLE_SELLER");
        }

        if (!userRepository.findByUsername(adminUsername).isPresent()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.addRole(adminRole);
            userRepository.save(adminUser);
            System.out.println("Created ADMIN user: " + adminUsername);
        } else {
            User adminUser = userRepository.findByUsername(adminUsername).get();
            if (!adminUser.hasRole(Role.PredefinedRole.ROLE_ADMIN.getRoleName())) {
                adminUser.addRole(adminRole);
                userRepository.save(adminUser);
                System.out.println("Added ROLE_ADMIN to existing admin user: " + adminUsername);
            }
        }
    }
}
