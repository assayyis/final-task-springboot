package com.enigma.wms.service.impl;

import com.enigma.wms.dto.request.ProductRequest;
import com.enigma.wms.dto.response.ProductResponse;
import com.enigma.wms.entity.Branch;
import com.enigma.wms.entity.Product;
import com.enigma.wms.entity.ProductPrice;
import com.enigma.wms.mapper.BranchMapper;
import com.enigma.wms.mapper.ProductMapper;
import com.enigma.wms.repository.ProductPriceRepository;
import com.enigma.wms.repository.ProductRepository;
import com.enigma.wms.service.BranchService;
import com.enigma.wms.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final BranchService branchService;

    @Override
    public Page<ProductResponse> getProductList(
            Integer size,
            Integer page,
            String productCode,
            String productName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String branchId) {

        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            Join<Product, ProductPrice> productPrices = root.join("productPrices");
            List<Predicate> predicates = new ArrayList<>();
            if (productCode != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + productCode.toLowerCase() + "%"));
            }
            if (productName != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + productName.toLowerCase() + "%"));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productPrices.get("price"), maxPrice));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(productPrices.get("price"), minPrice));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(specification, pageable);

        List<ProductResponse> productResponses = new ArrayList<>();
        products.getContent().forEach(product -> {
            Branch branch = product.getBranch();
            if (branch.getId().equals(branchId)){
                productResponses.add(ProductMapper.mapToResponse(product, product.getProductPrice(), branch));
            }
        });
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Override
    public List<ProductResponse> getProductListByBranchId(String branchId) {
        List<Product> products = productRepository.findAllByBranchId(branchId);
        return products.stream()
                .map(product -> ProductMapper.mapToResponse(product, product.getProductPrice(), product.getBranch()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse create(ProductRequest request) {
        Branch branch = branchService.getById(request.getBranchId());

        Product product = productRepository.save(Product.builder()
                .name(request.getProductName())
                .code(request.getProductCode())
                .branch(branch)
                .build());

        ProductPrice productPrice = productPriceRepository.save(ProductPrice.builder()
                .price(request.getPrice())
                .product(product)
                .build());

        return ProductMapper.mapToResponse(product, productPrice, product.getBranch());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse update(ProductRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("productId doesn't exist."));
        Branch branch = branchService.getById(request.getBranchId());

        product.setCode(request.getProductCode());
        product.setName(request.getProductName());
        product.setBranch(branch);

        product = productRepository.save(product);
        ProductPrice productPrice = product.getProductPrice();

        if (request.getPrice().compareTo(product.getProductPrice().getPrice()) == 0) {
            productPrice = productPriceRepository.save(ProductPrice.builder()
                    .price(request.getPrice())
                    .product(product)
                    .build());
        }

        return ProductMapper.mapToResponse(product, productPrice, product.getBranch());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void delete(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product doesn't exist."));
        productPriceRepository.deleteById(product.getProductPrice().getId());
        productRepository.deleteById(id);
    }
}
