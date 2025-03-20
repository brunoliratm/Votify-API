package com.votify.services;

import com.votify.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
