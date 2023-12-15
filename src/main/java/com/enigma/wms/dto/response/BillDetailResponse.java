package com.enigma.wms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BillDetailResponse {
    private String billDetailId;
    private String billId;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal totalSales;
}
