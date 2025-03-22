package com.votify.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.votify.dto.UserDTO;
import com.votify.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDTO userDto, BindingResult bindingResult) {
        userService.createUser(userDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    

    
}
