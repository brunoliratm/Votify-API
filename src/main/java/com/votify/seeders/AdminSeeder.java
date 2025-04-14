package com.votify.seeders;

import com.votify.config.AdminProperties;
import com.votify.dtos.requests.UserRequestDto;
import com.votify.services.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import org.slf4j.Logger;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private final AdminProperties adminProperties;

    public AdminSeeder(UserService userService, AdminProperties adminProperties) {
        this.userService = userService;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(String... args) {
        UserRequestDto adminDto = new UserRequestDto(
            "Admin",
            "Votify",
            adminProperties.getEmail(),
            adminProperties.getPassword(),
            "ADMIN"
        );

        if (userService.getUserByEmail(adminDto.email()) == null) {
            BindingResult bindingResult = new BeanPropertyBindingResult(adminDto, "userRequestDto");
            userService.createUser(adminDto, bindingResult);

            log.info("ADMIN user create successfully!");
        }
    }
}