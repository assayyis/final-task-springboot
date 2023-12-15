package com.enigma.wms.controller;

import com.enigma.wms.constant.AppPath;
import com.enigma.wms.dto.request.ProductRequest;
import com.enigma.wms.dto.response.CommonResponse;
import com.enigma.wms.dto.response.PagingResponse;
import com.enigma.wms.dto.response.ProductResponse;
import com.enigma.wms.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.PRODUCT)
public class ProductController {
    private final ProductService productService;

    @GetMapping(params = {"size", "page", "productCode", "productName", "minPrice", "maxPrice"})
    public ResponseEntity<?> getProductList(
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "productCode", required = false) String productCode,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice
    ) {
        Page<ProductResponse> productResponses = productService.getProductList(
                size,
                page,
                productCode,
                productName,
                minPrice,
                maxPrice,
                null);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(productResponses.getTotalPages())
                .size(size)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<List<ProductResponse>>builder()
                        .message("Successfully get product list.")
                        .data(productResponses.getContent())
                        .paging(pagingResponse)
                        .build());
    }

    @GetMapping(AppPath.PATHVAR_ID_BRANCH)
    public ResponseEntity<?> getProductList(
            @PathVariable String id_branch,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page
    ) {
        Page<ProductResponse> productResponses = productService.getProductList(
                size,
                page,
                null,
                null,
                null,
                null,
                id_branch);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(productResponses.getTotalPages())
                .size(size)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<List<ProductResponse>>builder()
                        .message("Successfully get product list.")
                        .data(productResponses.getContent())
                        .paging(pagingResponse)
                        .build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.<ProductResponse>builder()
                        .message("Successfully register new product.")
                        .data(productResponse)
                        .build());
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.update(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<ProductResponse>builder()
                        .message("Successfully update product.")
                        .data(productResponse)
                        .build());
    }

    @DeleteMapping(AppPath.PATHVAR_ID_PRODUCT)
    public ResponseEntity<?> delete(@PathVariable String id_product) {
        productService.delete(id_product);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .message("Successfully delete product.")
                        .data("OK")
                        .build());
    }
}
