package com.enigma.wms.mapper;

import com.enigma.wms.dto.BranchDTO;
import com.enigma.wms.entity.Branch;

public class BranchMapper {
    public static BranchDTO mapToDTO(Branch branch) {
        return BranchDTO.builder()
                .branchId(branch.getId())
                .branchCode(branch.getCode())
                .branchName(branch.getName())
                .address(branch.getAddress())
                .phoneNumber(branch.getPhoneNumber())
                .build();
    }

    public static Branch mapToBranch(BranchDTO branchDTO) {
        return Branch.builder()
                .id(branchDTO.getBranchId())
                .code(branchDTO.getBranchCode())
                .name(branchDTO.getBranchName())
                .address(branchDTO.getAddress())
                .phoneNumber(branchDTO.getPhoneNumber())
                .build();
    }
}
