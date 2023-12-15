package com.enigma.wms.service.impl;

import com.enigma.wms.constant.ETransType;
import com.enigma.wms.dto.request.BillDetailRequest;
import com.enigma.wms.dto.request.TransactionRequest;
import com.enigma.wms.dto.response.BillDetailResponse;
import com.enigma.wms.dto.response.ProductResponse;
import com.enigma.wms.dto.response.TotalSalesResponse;
import com.enigma.wms.dto.response.TransactionResponse;
import com.enigma.wms.entity.*;
import com.enigma.wms.mapper.ProductMapper;
import com.enigma.wms.repository.BillDetailRepository;
import com.enigma.wms.repository.BillRepository;
import com.enigma.wms.repository.ProductPriceRepository;
import com.enigma.wms.service.TransactionService;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final ProductPriceRepository productPriceRepository;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public TransactionResponse create(TransactionRequest request) {
        String transactionType = request.getTransactionType().trim();
        if (!(transactionType.equals("1") ||
                transactionType.equals("2") ||
                transactionType.equals("3"))) {
            throw new RuntimeException("Invalid transactionType");
        }
        if (request.getBillDetails().isEmpty()) {
            throw new RuntimeException("Please buy something");
        }
        ProductPrice initialProductPrice = productPriceRepository
                .findById(request.getBillDetails().get(0).getProductPriceId())
                .orElseThrow(() -> new RuntimeException("productPriceId doesn't exist"));

        String branchCode = initialProductPrice.getProduct().getBranch().getCode();
        String thisYear = Year.now().toString();

        String transType = null;
        switch (request.getTransactionType()) {
            case "1" -> transType = ETransType.EAT_IN.name();
            case "2" -> transType = ETransType.ONLINE.name();
            case "3" -> transType = ETransType.TAKE_AWAY.name();
        }

        Bill bill = Bill.builder()
                .transType(transType)
                .transDate(Date.valueOf(LocalDate.now()))
                .receiptNumber("%s-%s-%d".formatted(branchCode, thisYear, billRepository.count() + 1))
                .build();
        bill = billRepository.save(bill);

        List<BillDetailResponse> billDetailResponses = new ArrayList<>();
        for (BillDetailRequest billDetail : request.getBillDetails()) {
            ProductPrice productPrice = productPriceRepository.findById(billDetail.getProductPriceId())
                    .orElseThrow(() -> new RuntimeException("productPriceId doesn't exist"));

            BillDetail savedBillDetail = BillDetail.builder()
                    .bill(bill)
                    .quantity(billDetail.getQuantity())
                    .totalSales(productPrice.getPrice().multiply(BigDecimal.valueOf(billDetail.getQuantity())))
                    .product(productPrice.getProduct())
                    .build();
            savedBillDetail = billDetailRepository.save(savedBillDetail);

            billDetailResponses.add(BillDetailResponse.builder()
                    .billDetailId(savedBillDetail.getId())
                    .billId(savedBillDetail.getBill().getId())
                    .product(ProductMapper.mapToResponse(productPrice.getProduct(), productPrice.getProduct().getProductPrice(), productPrice.getProduct().getBranch()))
                    .quantity(billDetail.getQuantity())
                    .totalSales(savedBillDetail.getTotalSales())
                    .build());
        }

        return TransactionResponse.builder()
                .billId(bill.getId())
                .receiptNumber(bill.getReceiptNumber())
                .transDate(bill.getTransDate().toString())
                .transactionType(bill.getTransType())
                .billDetails(billDetailResponses)
                .build();
    }

    @Override
    public TransactionResponse getById(String id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("bill id doesn't exist"));

        List<BillDetailResponse> billDetailResponses = new ArrayList<>();
        for (BillDetail billDetail : bill.getBillDetail()) {
            billDetailResponses.add(BillDetailResponse.builder()
                            .billId(billDetail.getBill().getId())
                            .billDetailId(billDetail.getId())
                            .quantity(billDetail.getQuantity())
                            .totalSales(billDetail.getTotalSales())
                            .product(ProductMapper.mapToResponse(billDetail.getProduct(), billDetail.getProduct().getProductPrice(), billDetail.getProduct().getBranch()))
                    .build());
        }

        return TransactionResponse.builder()
                .billId(bill.getId())
                .receiptNumber(bill.getReceiptNumber())
                .transDate(bill.getTransDate().toString())
                .transactionType(bill.getTransType())
                .billDetails(billDetailResponses)
                .build();
    }

    @Override
    public Page<TransactionResponse> getTransactions(
            Integer page,
            Integer size,
            String receiptNumber,
            String startDate,
            String endDate,
            String transType,
            String productName) {

        Specification<Bill> specification = (root, query, criteriaBuilder) -> {
            Join<Bill, BillDetail> billDetails = root.join("billDetails");
            Join<BillDetail, Product> products = billDetails.join("product");
            List<Predicate> predicates = new ArrayList<>();
            if (receiptNumber != null) {
                predicates.add(criteriaBuilder.like(root.get("receiptNumber"), "%" + receiptNumber + "%"));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transDate"), endDate));
            }
            if (transType != null) {
                predicates.add(criteriaBuilder.like(root.get("transType"), "%" + transType + "%"));
            }
            if (productName != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(products.get("name")), "%" + productName.toLowerCase() + "%"));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<Bill> bills = billRepository.findAll(specification, pageable);

        List<TransactionResponse> transactionResponses = new ArrayList<>();
        bills.getContent().forEach(bill -> {
            List<BillDetailResponse> billDetailResponses = new ArrayList<>();
            for (BillDetail billDetail : bill.getBillDetail()) {
                billDetailResponses.add(BillDetailResponse.builder()
                        .billId(billDetail.getBill().getId())
                        .billDetailId(billDetail.getId())
                        .quantity(billDetail.getQuantity())
                        .totalSales(billDetail.getTotalSales())
                        .product(ProductMapper.mapToResponse(billDetail.getProduct(), billDetail.getProduct().getProductPrice(), billDetail.getProduct().getBranch()))
                        .build());
            }
            transactionResponses.add(TransactionResponse.builder()
                    .billId(bill.getId())
                    .receiptNumber(bill.getReceiptNumber())
                    .transDate(bill.getTransDate().toString())
                    .transactionType(bill.getTransType())
                    .billDetails(billDetailResponses)
                    .build());
        });
        return new PageImpl<>(transactionResponses, pageable, bills.getTotalElements());
    }

    @Override
    public TotalSalesResponse getTotalSales(String startDate, String endDate) {
        Specification<Bill> specification = (root, query, criteriaBuilder) -> {
            Join<Bill, BillDetail> billDetails = root.join("billDetails");
            Join<BillDetail, Product> products = billDetails.join("products");
            Join<Product, ProductPrice> productPrices = products.join("productPrices");
            List<Predicate> predicates = new ArrayList<>();
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transDate"), endDate));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), startDate));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        List<Bill> bills = billRepository.findAll(specification);
        BigDecimal eatIn = BigDecimal.valueOf(0);
        BigDecimal takeAway = BigDecimal.valueOf(0);
        BigDecimal online = BigDecimal.valueOf(0);

        for (Bill bill : bills) {
            if (ETransType.EAT_IN.name().equals(bill.getTransType())) {
                BigDecimal sum = BigDecimal.valueOf(0);
                for (BillDetail billDetail : bill.getBillDetail()) {
                    sum = sum.add(billDetail.getTotalSales());
                };
                eatIn = eatIn.add(sum);
            }
            if (ETransType.ONLINE.name().equals(bill.getTransType())) {
                BigDecimal sum = BigDecimal.valueOf(0);
                for (BillDetail billDetail : bill.getBillDetail()) {
                    sum = sum.add(billDetail.getTotalSales());
                };
                takeAway = takeAway.add(sum);

            }
            if (ETransType.TAKE_AWAY.name().equals(bill.getTransType())) {
                BigDecimal sum = BigDecimal.valueOf(0);
                for (BillDetail billDetail : bill.getBillDetail()) {
                    sum = sum.add(billDetail.getTotalSales());
                };
                online = online.add(sum);
            }
        }

        return TotalSalesResponse.builder()
                .eatIn(eatIn)
                .online(online)
                .takeAway(takeAway)
                .build();
    }
}
