package com.enigma.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionResponse {
    private String billId;
    private String receiptNumber;
    private String transDate;
    private String transactionType;
    private List<BillDetailResponse> billDetails;
}
