package com.github.datlaipro.shop.domain.productDetail.controller;

import com.github.datlaipro.shop.domain.productDetail.dto.ProductDetailDto;
import com.github.datlaipro.shop.domain.productDetail.service.ProductDetailQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductDetailQueryController {

    private final ProductDetailQueryService productDetailQueryService; // <-- camelCase

    // Constructor injection (Spring sẽ tự inject bean này)
    public ProductDetailQueryController(ProductDetailQueryService productDetailQueryService) {
        this.productDetailQueryService = productDetailQueryService;
    }

    // GET /api/products/{id}/detail?limitImages=4
    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDetailDto> getDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "limitImages", required = false) Integer limitImages
    ) {
        ProductDetailDto dto = productDetailQueryService.getProductDetail(id, limitImages);
        return ResponseEntity.ok(dto);
    }
}
