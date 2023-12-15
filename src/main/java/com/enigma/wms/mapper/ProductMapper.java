package com.enigma.wms.mapper;

import com.enigma.wms.dto.response.ProductResponse;
import com.enigma.wms.entity.Branch;
import com.enigma.wms.entity.Product;
import com.enigma.wms.entity.ProductPrice;

public class ProductMapper {
    public static ProductResponse mapToResponse(Product product, ProductPrice productPrice, Branch branch) {
        return ProductResponse.builder()
                .productId(product.getId())
                .productPriceId(productPrice.getId())
                .productCode(product.getCode())
                .productName(product.getName())
                .price(productPrice.getPrice())
                .branch(BranchMapper.mapToDTO(product.getBranch()))
                .build();
    }
}
