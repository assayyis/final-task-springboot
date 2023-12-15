package com.enigma.wms.service.impl;

import com.enigma.wms.entity.Branch;
import com.enigma.wms.repository.BranchRepository;
import com.enigma.wms.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository repository;

    @Override
    public Branch getById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Branch doesn't exist."));
    }

    @Override
    public Branch create(Branch branch) {
        return repository.save(branch);
    }

    @Override
    public Branch update(Branch branch) {
        repository.findById(branch.getId()).orElseThrow(() -> new RuntimeException("Branch doesn't exist."));

        return repository.save(branch);
    }

    @Override
    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new RuntimeException("Branch doesn't exist."));
        repository.deleteById(id);
    }
}
