package com.github.datlaipro.shop.domain.product.dto;

public class SearchProductReq {
  private String category; // nhận từ FE: "Cases" (hoặc "cases")
  private String model;    // nhận từ FE: "iPhone 16 Pro Max"
  private String name;     // tuỳ chọn: tìm gần đúng theo tên

  private Integer page = 0;     // mặc định 0
  private Integer size = 20;    // mặc định 20
  private String sort = "createdAt,desc"; // "field,asc|desc"

  public SearchProductReq() {}

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }

  public String getModel() { return model; }
  public void setModel(String model) { this.model = model; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Integer getPage() { return page; }
  public void setPage(Integer page) { this.page = page; }

  public Integer getSize() { return size; }
  public void setSize(Integer size) { this.size = size; }

  public String getSort() { return sort; }
  public void setSort(String sort) { this.sort = sort; }
}
