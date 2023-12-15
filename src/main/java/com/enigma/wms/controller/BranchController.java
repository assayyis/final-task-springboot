package com.enigma.wms.controller;

import com.enigma.wms.constant.AppPath;
import com.enigma.wms.dto.BranchDTO;
import com.enigma.wms.dto.response.CommonResponse;
import com.enigma.wms.entity.Branch;
import com.enigma.wms.mapper.BranchMapper;
import com.enigma.wms.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.BRANCH)
public class BranchController {
    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody BranchDTO branchDTO) {
        Branch branch = branchService.create(BranchMapper.mapToBranch(branchDTO));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.<BranchDTO>builder()
                        .message("Successfully register new branch.")
                        .data(BranchMapper.mapToDTO(branch))
                        .build());
    }

    @PutMapping
    public ResponseEntity<?> updateBranch(@RequestBody BranchDTO branchDTO) {
        Branch branch = branchService.update(BranchMapper.mapToBranch(branchDTO));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<BranchDTO>builder()
                        .message("Successfully update branch.")
                        .data(BranchMapper.mapToDTO(branch))
                        .build());
    }

    @DeleteMapping(AppPath.PATHVAR_ID_BRANCH)
    public ResponseEntity<?> deleteBranch(@PathVariable String id_branch) {
        branchService.delete(id_branch);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .message("Successfully delete branch.")
                        .data("OK")
                        .build());
    }

    @GetMapping(AppPath.PATHVAR_ID_BRANCH)
    public ResponseEntity<?> getBranchById(@PathVariable String id_branch) {
        Branch branch = branchService.getById(id_branch);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<BranchDTO>builder()
                        .message("Successfully get branch by id.")
                        .data(BranchMapper.mapToDTO(branch))
                        .build());
    }
}
