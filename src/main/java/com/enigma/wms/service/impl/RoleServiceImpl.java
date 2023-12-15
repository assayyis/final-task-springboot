package com.enigma.wms.service.impl;

import com.enigma.wms.entity.Role;
import com.enigma.wms.repository.RoleRepository;
import com.enigma.wms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public Role getOrSave(Role role) {
        Optional<Role> optionalRole = roleRepository.findByName(role.getName());
        return optionalRole.orElseGet(() -> roleRepository.save(role));
    }
}
