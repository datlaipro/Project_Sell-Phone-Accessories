package com.github.datlaipro.shop.domain.productspec.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductSpecId implements Serializable {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "attr", nullable = false, length = 100)
    private String attr;

    public ProductSpecId() {}

    public ProductSpecId(Long productId, String attr) {
        this.productId = productId;
        this.attr = attr;
    }

    // ===== getters/setters =====
    public Long getProductId() { 
        return productId; 
    }

    public void setProductId(Long productId) { 
        this.productId = productId; 
    }

    public String getAttr() { 
        return attr; 
    }

    public void setAttr(String attr) { 
        this.attr = attr; 
    }

    // equals & hashCode bắt buộc cho khóa tổng hợp
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSpecId)) return false;
        ProductSpecId that = (ProductSpecId) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(attr, that.attr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, attr);
    }
}
