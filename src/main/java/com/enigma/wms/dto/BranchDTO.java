package com.enigma.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BranchDTO {
    private String branchId;
    private String branchCode;
    private String branchName;
    private String address;
    private String phoneNumber;
}
