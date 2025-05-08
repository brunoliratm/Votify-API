package com.votify.seeders;

import com.votify.enums.UserRole;
import com.votify.infra.config.AdminProperties;
import com.votify.models.UserModel;
import com.votify.repositories.UserRepository;
import com.votify.services.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private final AdminProperties adminProperties;
    private final UserRepository userRepository;

    public AdminSeeder(UserService userService, AdminProperties adminProperties, UserRepository userRepository) {
        this.userService = userService;
        this.adminProperties = adminProperties;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userService.getUserByEmail(adminProperties.getEmail()) == null) {
            UserModel admin = new UserModel();

            admin.setName("Admin");
            admin.setSurname("Default");
            admin.setEmail(adminProperties.getEmail());
            admin.setPassword(new BCryptPasswordEncoder().encode(adminProperties.getPassword()));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            log.info("ADMIN user create successfully!");
        }
    }
}
