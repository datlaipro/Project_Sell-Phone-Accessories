package com.github.datlaipro.shop.domain.addNewProduct.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public class AddNewProductReq {

  @NotBlank
  @Size(max = 255)
  private String name;

  @NotBlank
  @Size(max = 100)
  private String brand;

  @NotNull
  @Min(0)
  private Integer quantity;

  // tuỳ nghiệp vụ: nếu muốn bắt buộc, đổi thành @NotBlank
  @Size(max = 100)
  private String color;

  @NotBlank
  private String description;
  private Map<String, String> specs; // ví dụ: {"Material":"TPU","Compat":"iPhone 15"}
  @DecimalMin("0.00")
  @DecimalMax("100.00") // nếu discount là %
  private BigDecimal discount;
  @Min(0)
  private Integer coverIndex;
  private List<String> modelSlugs;
  @NotNull
  @DecimalMin(value = "0.00")
  private BigDecimal price;


  // 👇 Cho phép null; nếu có giá trị thì phải ≥ 0.00
  @DecimalMin(value = "0.00")
  private BigDecimal rate;

  @NotBlank
  @Size(max = 255)
  private String category;

  // ===== Getters / Setters =====
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Integer getCoverIndex() {
    return coverIndex;
  }

  public void setCoverIndex(Integer coverIndex) {
    this.coverIndex = coverIndex;
  }

  public List<String> getModelSlugs() {
    return modelSlugs;
  }


  public void setModelSlugs(List<String> modelSlugs) {
    this.modelSlugs = modelSlugs;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(BigDecimal discount) {
    this.discount = discount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public java.util.Map<String, String> getSpecs() {
    return specs;
  }

  public void setSpecs(java.util.Map<String, String> specs) {
    this.specs = specs;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public void setRate(BigDecimal rate) {
    this.rate = rate;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
