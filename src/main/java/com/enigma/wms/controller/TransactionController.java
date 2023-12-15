package com.enigma.wms.controller;

import com.enigma.wms.constant.AppPath;
import com.enigma.wms.dto.request.TransactionRequest;
import com.enigma.wms.dto.response.*;
import com.enigma.wms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.TRANSACTION)
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping(params = {"size", "page", "receiptNumber", "productName", "startDate", "endDate", "transType"})
    public ResponseEntity<?> getAll(
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "receiptNumber", required = false) String receiptNumber,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "transType", required = false) String transType
    ) {
        Page<TransactionResponse> transactionResponses = transactionService.getTransactions(
                page,
                size,
                receiptNumber,
                startDate,
                endDate,
                transType,
                productName);

        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(transactionResponses.getTotalPages())
                .size(size)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<List<TransactionResponse>>builder()
                        .message("Successfully get product list.")
                        .data(transactionResponses.getContent())
                        .paging(pagingResponse)
                        .build());
    }

    @GetMapping(AppPath.PATHVAR_ID_BILL)
    public ResponseEntity<?> getById(@PathVariable String id_bill) {
        TransactionResponse transactionResponse = transactionService.getById(id_bill);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<TransactionResponse>builder()
                        .message("Successfully get transaction by id.")
                        .data(transactionResponse)
                        .build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.create(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<TransactionResponse>builder()
                        .message("Successfully create transaction.")
                        .data(transactionResponse)
                        .build());
    }

    @GetMapping(value = AppPath.TOTAL_SALES, params = {"startDate", "endDate"})
    public ResponseEntity<?> getTotalSales(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate
    ) {
        TotalSalesResponse totalSalesResponse = transactionService.getTotalSales(startDate, endDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<TotalSalesResponse>builder()
                        .message("Successfully get total sales.")
                        .data(totalSalesResponse)
                        .build());
    }
}
