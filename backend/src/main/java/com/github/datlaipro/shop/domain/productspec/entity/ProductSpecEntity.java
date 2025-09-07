package com.github.datlaipro.shop.domain.productspec.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_specs")
public class ProductSpecEntity {

    @EmbeddedId
    private ProductSpecId id;

    @Column(name = "val", nullable = false, columnDefinition = "TEXT")
    private String val;

   

    public ProductSpecEntity() {
    }

    public ProductSpecEntity(ProductSpecId id, String val) {
        this.id = id;
        this.val = val;
    }

    // ===== getters/setters =====
    public ProductSpecId getId() {
        return id;
    }

    public void setId(ProductSpecId id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    // --- tiện ích: thao tác nhanh với khóa tổng hợp ---
    @Transient
    public Long getProductId() {
        return (id != null) ? id.getProductId() : null;
    }

    public void setProductId(Long productId) {
        if (this.id == null)
            this.id = new ProductSpecId();
        this.id.setProductId(productId);
    }

    @Transient
    public String getAttr() {
        return (id != null) ? id.getAttr() : null;
    }

    public void setAttr(String attr) {
        if (this.id == null)
            this.id = new ProductSpecId();
        this.id.setAttr(attr);
    }
}
