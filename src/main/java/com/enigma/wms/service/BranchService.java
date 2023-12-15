package com.enigma.wms.service;

import com.enigma.wms.entity.Branch;

public interface BranchService {
    Branch getById(String id);
    Branch create(Branch branch);
    Branch update(Branch branch);
    void delete(String id);
}
