package com.medifind.backend.service;

import com.medifind.backend.entity.User;
import com.medifind.backend.enums.Role;
import com.medifind.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    public long getCustomerCount() {
        return userRepository.countByRole(Role.CUSTOMER);
    }
}