package com.enigma.wms.controller;

import com.enigma.wms.constant.ERole;
import com.enigma.wms.dto.request.AuthRequest;
import com.enigma.wms.dto.response.LoginResponse;
import com.enigma.wms.dto.response.RegisterResponse;
import com.enigma.wms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse registerCustomer(@RequestBody AuthRequest authRequest) {
        return authService.register(authRequest);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(CommonResponse.<RegisterResponse>builder()
//                        .statusCode(HttpStatus.CREATED.value())
//                        .message("Success register")
//                        .data(registerResponse)
//                        .build());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

}
