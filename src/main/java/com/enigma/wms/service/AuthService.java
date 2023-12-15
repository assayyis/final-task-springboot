package com.enigma.wms.service;

import com.enigma.wms.constant.ERole;
import com.enigma.wms.dto.request.AuthRequest;
import com.enigma.wms.dto.response.LoginResponse;
import com.enigma.wms.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest authRequest);
    LoginResponse login(AuthRequest authRequest);
}
