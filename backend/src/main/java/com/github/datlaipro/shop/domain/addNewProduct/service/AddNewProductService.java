package com.github.datlaipro.shop.domain.addNewProduct.service;

import com.github.datlaipro.shop.domain.addNewProduct.dto.AddNewProductReq;
import com.github.datlaipro.shop.domain.addNewProduct.dto.AddNewProductRes;
import com.github.datlaipro.shop.domain.addNewProduct.entity.AddNewProductEntity;
import com.github.datlaipro.shop.domain.addNewProduct.entity.ProductImageEntity;
import com.github.datlaipro.shop.domain.addNewProduct.repo.AddNewProductRepository;
import com.github.datlaipro.shop.domain.addNewProduct.repo.CategoryRepository;
import com.github.datlaipro.shop.domain.addNewProduct.repo.ProductImageRepository;
import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminRes;
import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecEntity;
import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecId;
import com.github.datlaipro.shop.domain.productspec.repo.ProductSpecRepository;
import com.github.datlaipro.shop.storage.CloudflareR2Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Service
public class AddNewProductService {

  private final AddNewProductRepository productRepo;
  private final CategoryRepository categoryRepo;
  private final ProductImageRepository imageRepo;
  private final ProductSpecRepository productSpecRepo; // 👈 NEW
  private final CloudflareR2Service r2;

  public AddNewProductService(AddNewProductRepository productRepo,
      CategoryRepository categoryRepo,
      ProductImageRepository imageRepo,
      ProductSpecRepository productSpecRepo, // 👈 NEW
      CloudflareR2Service r2) {
    this.productRepo = productRepo;
    this.categoryRepo = categoryRepo;
    this.imageRepo = imageRepo;
    this.productSpecRepo = productSpecRepo; // 👈 NEW
    this.r2 = r2;
  }

  // ===== API cũ (không có file) vẫn chạy bình thường =====
  @Transactional
  public AddNewProductRes createProduct(AddNewProductReq req, LoginAdminRes admin) {
    return createProduct(req, admin, null); // gọi overload mới
  }

  // ===== API mới: có thể nhận nhiều file ảnh =====
  @Transactional
  public AddNewProductRes createProduct(AddNewProductReq req,
      LoginAdminRes admin,
      MultipartFile[] files) {
    // 1) Chuẩn hoá & kiểm tra category theo tên
    String catName = Optional.ofNullable(req.getCategory())
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .orElseThrow(() -> new IllegalArgumentException("Thiếu category"));

    var category = categoryRepo.findByNameIgnoreCase(catName)
        .orElseThrow(() -> new IllegalArgumentException("Category không tồn tại: " + catName));

    // 1.1) (NEW) Chuẩn hoá product type (nếu FE gửi)
   

    

    // 2) Lưu product
    var p = new AddNewProductEntity();
    p.setName(req.getName());
    p.setDescription(req.getDescription());
    p.setPrice(req.getPrice());
    p.setDiscount(Optional.ofNullable(req.getDiscount()).orElse(BigDecimal.ZERO));
    p.setRate(req.getRate() == null ? new BigDecimal("0.00") : req.getRate()); // tránh NULL cho cột NOT NULL
    p.setColor(req.getColor());
    p.setQuantity(req.getQuantity());
    p.setBrand(req.getBrand());
    p.setCategoryId(category.getId());
    p.setIsActive(true);

    // (NEW) gán productTypeId nếu có
 

    if (admin != null && admin.getId() != null) {
      p.setCreatedByAdminId(admin.getId());
      p.setUpdatedByAdminId(admin.getId()); // lần tạo đầu tiên: updated = created
    }

    var saved = productRepo.save(p);

    // 3) Upload ảnh lên R2 (nếu có), rồi lưu vào product_images
    List<String> urls = new ArrayList<>();
    try {
      if (files != null && files.length > 0) {
        urls = r2.uploadMany(files, "product");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Upload ảnh thất bại: " + e.getMessage(), e);
    }

    if (!urls.isEmpty()) {
      int coverIdx = Optional.ofNullable(req.getCoverIndex()).orElse(0);
      if (coverIdx < 0 || coverIdx >= urls.size())
        coverIdx = 0;

      int order = 1; // cover = 0, còn lại 1,2,3...
      for (int i = 0; i < urls.size(); i++) {
        String url = urls.get(i);
        if (url == null || url.isBlank())
          continue;

        var img = new ProductImageEntity();
        img.setProduct(saved);
        img.setImageUrl(url.trim());

        boolean isCover = (i == coverIdx);
        img.setIsCover(isCover);
        img.setSortOrder(isCover ? 0 : order++);

        imageRepo.save(img);
      }
    }

    // 4) (NEW) Lưu specs mềm dẻo vào product_specs
    Map<String, String> specs = Optional.ofNullable(req.getSpecs()).orElse(Collections.emptyMap());
    if (!specs.isEmpty()) {
      for (Entry<String, String> e : specs.entrySet()) {
        String attr = sanitizeAttr(e.getKey());
        String val = sanitizeVal(e.getValue());
        if (attr == null || val == null)
          continue;

        var pk = new ProductSpecId(saved.getId(), attr);
        var specEntity = new ProductSpecEntity(pk, val);
        productSpecRepo.save(specEntity);
      }
      // nếu muốn đảm bảo không trùng key (PK kép đã đảm bảo); không cần xoá trước
    }

    // 5) Trả DTO
    return toRes(saved, category.getName());
  }

  private AddNewProductRes toRes(AddNewProductEntity e, String categoryName) {
    var res = new AddNewProductRes();
    res.setId(e.getId());
    res.setName(e.getName());
    res.setDescription(e.getDescription());
    res.setPrice(e.getPrice());
    res.setRate(e.getRate());
    res.setColor(e.getColor());
    res.setQuantity(e.getQuantity());
    res.setBrand(e.getBrand());

    res.setCategoryId(e.getCategoryId());
    res.setCategoryName(categoryName);
    res.setIsActive(e.getIsActive());
    res.setCreatedAt(e.getCreatedAt());
    res.setUpdatedAt(e.getUpdatedAt());

    // nếu entity có các field này:
    res.setCreatedByAdminId(e.getCreatedByAdminId());
    res.setUpdatedByAdminId(e.getUpdatedByAdminId());
    return res;
  }

  // --- helpers nhỏ để an toàn độ dài/blank ---
  private String sanitizeAttr(String raw) {
    if (raw == null)
      return null;
    String s = raw.trim();
    if (s.isEmpty())
      return null;
    // khớp @Column(length=100) bạn đã định nghĩa cho attr
    return s.length() > 100 ? s.substring(0, 100) : s;
  }

  private String sanitizeVal(String raw) {
    if (raw == null)
      return null;
    String s = raw.trim();
    if (s.isEmpty())
      return null;
    // khớp @Column(length=500) bạn đã định nghĩa cho val
    return s.length() > 500 ? s.substring(0, 500) : s;
  }
}
