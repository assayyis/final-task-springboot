package com.enigma.wms.service;

import com.enigma.wms.dto.request.ProductRequest;
import com.enigma.wms.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> getProductList(
            Integer size,
            Integer page,
            String productCode,
            String productName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String branchId);

    List<ProductResponse> getProductListByBranchId(String branchId);

    ProductResponse create(ProductRequest request);

    ProductResponse update(ProductRequest request);

    void delete(String id);
}
