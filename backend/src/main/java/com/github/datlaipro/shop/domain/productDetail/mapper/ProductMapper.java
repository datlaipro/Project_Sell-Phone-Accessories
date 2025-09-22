package com.github.datlaipro.shop.domain.productDetail.mapper;

//Nhận dữ liệu thô từ tầng repository
//Ánh xạ + chuẩn hóa dữ liệu về DTO cho FE

import com.github.datlaipro.shop.domain.productDetail.dto.ProductDetailDto;
import com.github.datlaipro.shop.domain.productDetail.dto.ProductDetailImageDto;
import com.github.datlaipro.shop.domain.productDetail.repo.projection.ProductDetailRow;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class ProductMapper {
    private ProductMapper() {}

    public static ProductDetailDto toDto(ProductDetailRow row,
                                         String coverUrl,
                                         List<String> nonCoverPacked) {
        BigDecimal price = row.getPrice();
        BigDecimal discount = row.getDiscount() == null ? BigDecimal.ZERO : row.getDiscount();

        // Nếu discount biểu diễn phần trăm (ví dụ 30.00 => 30%)
        BigDecimal finalPrice = (discount.compareTo(BigDecimal.ZERO) > 0)
                ? price.multiply(BigDecimal.ONE.subtract(discount.movePointLeft(2))) // price*(1 - %/100)
                : price;

        List<ProductDetailImageDto> media = nonCoverPacked.stream()
                .map(s -> {
                    // format "url||sort"
                    String[] parts = s.split("\\|\\|");
                    String url = parts[0];
                    Integer sort = parts.length > 1 ? Integer.valueOf(parts[1]) : 0;
                    return ProductDetailImageDto.builder()
                            .url(url)
                            .sortOrder(sort)
                            .cover(false)
                            .build();
                })
                .collect(Collectors.toList());

        String availability = (row.getQuantity() != null && row.getQuantity() > 0)
                ? "In Stock" : "Out of Stock";

        return ProductDetailDto.builder()
                .id(row.getId())
                .name(row.getName())
                .description(row.getDescription())
                .price(price)
                .discount(discount)
                .finalPrice(finalPrice)
                .colorName(row.getColor())
                .availability(availability)
                .sku("SKU " + row.getId())
                .brand(row.getBrand())
                .categoryName(row.getCategoryName())
                .coverImage(coverUrl)
                .mediaImages(media)
                .rate(row.getRate())
                .quantity(row.getQuantity())
                .build();
    }
}
