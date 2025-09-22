package com.github.datlaipro.shop.domain.productDetail.service;

import com.github.datlaipro.shop.domain.productDetail.dto.ProductDetailDto;

public interface ProductDetailQueryService {
    ProductDetailDto getProductDetail(Long productId, Integer limitImages);
}
