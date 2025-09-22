package com.github.datlaipro.shop.domain.productDetail.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="image_url", nullable=false, length=500)
    private String imageUrl;

    @Column(name="sort_order", nullable=false)
    private Integer sortOrder;

    @Column(name="is_cover", nullable=false)
    private boolean isCover;
}
