package com.github.datlaipro.shop.domain.product.service;

import com.github.datlaipro.shop.domain.product.dto.ProductRes;
import com.github.datlaipro.shop.domain.product.dto.SearchProductReq;
import com.github.datlaipro.shop.domain.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

public interface ProductQueryService {

  // Trả về DTO (khuyên dùng cho FE)
  Page<ProductRes> search(SearchProductReq req);

  // Tuỳ chọn: trả về thẳng entity nếu BE nội bộ cần
  Page<ProductEntity> searchEntities(SearchProductReq req);
}
