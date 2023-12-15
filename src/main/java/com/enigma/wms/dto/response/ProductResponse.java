package com.enigma.wms.dto.response;

import com.enigma.wms.dto.BranchDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProductResponse {
    private String productId;
    private String productCode;
    private String productName;
    private BigDecimal price;
    private String productPriceId;
    private BranchDTO branch;
}
