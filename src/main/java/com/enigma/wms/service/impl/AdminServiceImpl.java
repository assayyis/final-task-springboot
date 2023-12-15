package com.enigma.wms.service.impl;

import com.enigma.wms.entity.Admin;
import com.enigma.wms.repository.AdminRepository;
import com.enigma.wms.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository repository;
    @Override
    public Admin create(Admin admin) {
        return repository.save(admin);
    }
}
