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
public class TotalSalesResponse {
    private BigDecimal eatIn;
    private BigDecimal takeAway;
    private BigDecimal online;
}
