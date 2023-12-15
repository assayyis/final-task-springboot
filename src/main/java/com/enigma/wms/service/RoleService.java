package com.enigma.wms.service;

import com.enigma.wms.entity.Role;

public interface RoleService {
    Role getOrSave(Role role);
}
