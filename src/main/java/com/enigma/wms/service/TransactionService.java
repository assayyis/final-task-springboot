package com.enigma.wms.service;

import com.enigma.wms.dto.request.TransactionRequest;
import com.enigma.wms.dto.response.TotalSalesResponse;
import com.enigma.wms.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    TransactionResponse getById(String id);
    Page<TransactionResponse> getTransactions(
            Integer page,
            Integer size,
            String receiptNumber,
            String startDate,
            String endDate,
            String transType,
            String productName);

    TotalSalesResponse getTotalSales(String startDate, String endDate);
}
