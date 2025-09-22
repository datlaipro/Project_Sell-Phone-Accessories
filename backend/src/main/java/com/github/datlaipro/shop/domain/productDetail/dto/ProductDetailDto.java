package com.github.datlaipro.shop.domain.productDetail.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDetailDto {
    private Long id;
    private String name;            
    private String description;     
    private BigDecimal price;       
    private BigDecimal discount;    
    private BigDecimal finalPrice;  
    private String colorName;       
    private String availability;    
    private String sku;             
    private String brand;
    private String categoryName;

    private String coverImage;      
    private List<ProductDetailImageDto> mediaImages;

    private Double rate;            
    private Integer quantity;       

    // ====== THÊM MỚI: specs ======
    @Builder.Default
    private List<SpecItem> specs = new ArrayList<>();

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SpecItem {
        private String attr;
        private String val;
    }
}
