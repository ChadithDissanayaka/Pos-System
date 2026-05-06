package com.devstack.POS.api;

import com.devstack.POS.dto.request.LoginRequestDTO;
import com.devstack.POS.dto.request.RegisterRequestDTO;
import com.devstack.POS.dto.response.AuthResponseDTO;
import com.devstack.POS.service.AuthService;
import com.devstack.POS.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponseDTO> register
            (@Valid @RequestBody RegisterRequestDTO dto){
        authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("User created successfully")
                        .data(null)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponseDTO> login
            (@Valid @RequestBody LoginRequestDTO dto){
        AuthResponseDTO authResponse = authService.login(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Login successful")
                        .data(authResponse)
                        .build());
    }
}