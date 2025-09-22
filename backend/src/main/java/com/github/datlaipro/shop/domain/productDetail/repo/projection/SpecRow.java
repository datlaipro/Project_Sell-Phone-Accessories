package com.github.datlaipro.shop.domain.productDetail.repo.projection;

/** Projection đọc 1 dòng spec: attr và val từ bảng product_specs */
public interface SpecRow {
    String getAttr();
    String getVal();
}
