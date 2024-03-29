package com.enigma.wms.service.impl;

import com.enigma.wms.entity.AppUser;
import com.enigma.wms.entity.UserCredential;
import com.enigma.wms.repository.UserCredentialRepository;
import com.enigma.wms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCredentialRepository userCredentialRepository;

    @Override
    public AppUser loadUserByUserId(String id) {
        UserCredential userCredential = userCredentialRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credential"));
        return new AppUser(
                userCredential.getId(),
                userCredential.getUsername(),
                userCredential.getPassword(),
                userCredential.getRole().getName());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential userCredential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credential"));
        return new AppUser(
                userCredential.getId(),
                userCredential.getUsername(),
                userCredential.getPassword(),
                userCredential.getRole().getName());
    }
}
