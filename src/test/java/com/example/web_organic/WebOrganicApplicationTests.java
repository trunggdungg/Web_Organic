package com.example.web_organic;

import com.example.web_organic.entity.Role;
import com.example.web_organic.entity.User;
import com.example.web_organic.repository.RoleRepository;
import com.example.web_organic.repository.UserRepository;
import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@SpringBootTest
class WebOrganicApplicationTests {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createRoles() {
        String[][] roles = {
            {"ROLE_ADMIN", "Administrator role"},
            {"ROLE_USER", "User role"},
            {"ROLE_STAFF", "Staff role"}
        };
        for (String[] roleData : roles) {
            Role role = new Role();
            role.setRoleName(roleData[0]);
            role.setDescription(roleData[1]);
            roleRepository.save(role);
        }
    }

    @Test
    void createUser() {
        Faker faker = new Faker();
        Random random = new Random();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<Role> roles = roleRepository.findAll();

        for (int i = 0; i < 10; i++) {
            String name = faker.name().username();
            User user = User.builder()
                .username(name)
                .password(passwordEncoder.encode("123"))
                .fullname(name)
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .avatar(faker.avatar().image())
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
//				if i % 2==0 then user has role ROLE_ADMIN else user has role ROLE_USER, ROLE_STAFF
                .roles(random.nextInt() % 2 == 0 ? List.of(roles.get(0)) : List.of(roles.get(1), roles.get(2)))
                .build();
            userRepository.save(user);
        }
    }












    @Test
    void contextLoads() {
        // Set user with ID 11 to have the role ROLE_USER
        User user = userRepository.findById(11).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByRoleName("ROLE_USER");
        user.setRoles(List.of(role));
        userRepository.save(user);
    }


}
