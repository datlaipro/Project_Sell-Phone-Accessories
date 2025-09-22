package com.github.datlaipro.shop.domain.productDetail.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailImageDto {
    private String url;
    private Integer sortOrder;
    private boolean cover;
}
