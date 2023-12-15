package com.enigma.wms.service.impl;

import com.enigma.wms.constant.ERole;
import com.enigma.wms.dto.request.AuthRequest;
import com.enigma.wms.dto.response.LoginResponse;
import com.enigma.wms.dto.response.RegisterResponse;
import com.enigma.wms.entity.Admin;
import com.enigma.wms.entity.AppUser;
import com.enigma.wms.entity.Role;
import com.enigma.wms.entity.UserCredential;
import com.enigma.wms.repository.AdminRepository;
import com.enigma.wms.repository.UserCredentialRepository;
import com.enigma.wms.security.JwtUtil;
import com.enigma.wms.service.AdminService;
import com.enigma.wms.service.AuthService;
import com.enigma.wms.service.RoleService;
import com.enigma.wms.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final AdminService adminService;
    private final RoleService roleService;

    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest authRequest) {
        try {
            validationUtil.validate(authRequest);
            Role role = Role.builder()
                    .name(ERole.ROLE_ADMIN)
                    .build();
            Role savedRole = roleService.getOrSave(role);

            UserCredential userCredential = UserCredential.builder()
                    .username(authRequest.getUsername().toLowerCase())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(savedRole)
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);

            Admin.builder()
                    .name(authRequest.getName())
                    .phoneNumber(authRequest.getPhoneNumber())
                    .userCredential(userCredential)
                    .build();

            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .role(userCredential.getRole().getName().toString())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exist");
        }
    }

    @Override
    public LoginResponse login(AuthRequest authRequest) {
        validationUtil.validate(authRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername().toLowerCase(),
                authRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);

        return LoginResponse.builder()
                .token(token)
                .role(appUser.getRole().name())
                .build();
    }
}
