package com.devstack.POS.helper;

import com.devstack.POS.entity.ROLE_TYPES;
import com.devstack.POS.entity.SystemUser;
import com.devstack.POS.repo.SystemUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final SystemUserRepo systemUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUser("admin@gmail.com", "Admin User", "Admin@1234!", ROLE_TYPES.ADMIN);
        seedUser("manager@gmail.com", "Manager User", "Manager@1234!", ROLE_TYPES.MANAGER);
    }

    private void seedUser(String email, String name, String password, ROLE_TYPES role) {
        if (!systemUserRepo.existsByEmail(email)) {
            SystemUser user = SystemUser.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .isActive(true)
                    .build();
            systemUserRepo.save(user);
        }
    }
}