package com.devstack.POS.service.impl;

import com.devstack.POS.dto.request.LoginRequestDTO;
import com.devstack.POS.dto.request.RegisterRequestDTO;
import com.devstack.POS.dto.response.AuthResponseDTO;
import com.devstack.POS.entity.ROLE_TYPES;
import com.devstack.POS.entity.SystemUser;
import com.devstack.POS.exception.DuplicateEntryException;
import com.devstack.POS.exception.EntryNotFoundException;
import com.devstack.POS.repo.SystemUserRepo;
import com.devstack.POS.service.AuthService;
import com.devstack.POS.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceIMPL implements AuthService {
    private final SystemUserRepo systemUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequestDTO dto) {
        if (systemUserRepo.existsByEmail(dto.getEmail())){
            throw new DuplicateEntryException("Email is Already exists");
        }

        SystemUser user = SystemUser.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(ROLE_TYPES.USER)
                .isActive(true)
                .build();

        systemUserRepo.save(user);

    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        SystemUser user = systemUserRepo.findByEmail(dto.getEmail());
        if (user == null) {
            throw new EntryNotFoundException("User not found with email: " + dto.getEmail());
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new EntryNotFoundException("Invalid credentials");
        }

        String token = jwtUtil.generateAccessToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}