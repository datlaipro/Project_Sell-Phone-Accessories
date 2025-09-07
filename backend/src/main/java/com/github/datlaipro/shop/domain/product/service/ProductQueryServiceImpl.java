package com.github.datlaipro.shop.domain.product.service;


import com.github.datlaipro.shop.exception.NotFoundException;
import com.github.datlaipro.shop.domain.util.SlugUtil;
import com.github.datlaipro.shop.domain.addNewProduct.entity.ProductImageEntity;
import com.github.datlaipro.shop.domain.addNewProduct.repo.ProductImageRepository;
import com.github.datlaipro.shop.domain.addNewProduct.entity.CategoryEntity;
import com.github.datlaipro.shop.domain.product.repo.CategoryProductRepository;
import com.github.datlaipro.shop.domain.product.entity.DeviceModelEntity;
import com.github.datlaipro.shop.domain.product.repo.DeviceModelRepository;
import com.github.datlaipro.shop.domain.product.dto.ProductRes;
import com.github.datlaipro.shop.domain.product.dto.SearchProductReq;
import com.github.datlaipro.shop.domain.product.entity.ProductEntity;
import com.github.datlaipro.shop.domain.product.repo.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductQueryServiceImpl implements ProductQueryService {

  private final ProductRepository productRepository;
  private final CategoryProductRepository categoryRepository;
  private final DeviceModelRepository deviceModelRepository;
  private final ProductImageRepository productImageRepository;

  public ProductQueryServiceImpl(ProductRepository productRepository,
                                 CategoryProductRepository categoryRepository,
                                 DeviceModelRepository deviceModelRepository,
                                 ProductImageRepository productImageRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.deviceModelRepository = deviceModelRepository;
    this.productImageRepository = productImageRepository;
  }

  // -------------------------------------------------------------------------
  // Public API
  // -------------------------------------------------------------------------

  @Override
  @Transactional(readOnly = true)
  public Page<ProductRes> search(SearchProductReq req) {
    Page<ProductEntity> page = searchEntities(req);

    // ---- LẤY COVER THEO LÔ (TRÁNH N+1) ----
    List<Long> ids = page.getContent().stream()
        .map(ProductEntity::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    Map<Long, ProductImageEntity> coverMap = ids.isEmpty()
        ? Map.of()
        : productImageRepository.findCoversByProductIds(ids).stream()
            .collect(Collectors.toMap(
                pi -> pi.getProduct().getId(),            // nếu bạn đổi sang ProductEntity thì vẫn là getProduct().getId()
                Function.identity(),
                // nếu lỡ có nhiều cover (không nên), giữ tấm có id mới hơn
                (a, b) -> a.getId() > b.getId() ? a : b
            ));

    // ---- MAP DTO + GẮN COVER ----
    return page.map(p -> {
      ProductRes r = toRes(p);
      ProductImageEntity cover = coverMap.get(p.getId());
      if (cover != null) {
        r.setCoverImageId(cover.getId());
        r.setCoverImageUrl(cover.getImageUrl()); // field trong entity của bạn
      }
      return r;
    });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductEntity> searchEntities(SearchProductReq req) {
    if (req == null) throw new IllegalArgumentException("Request must not be null");
    if (req.getCategory() == null || req.getCategory().isBlank())
      throw new IllegalArgumentException("Category is required");
    if (req.getModel() == null || req.getModel().isBlank())
      throw new IllegalArgumentException("Model is required");

    String catSlug = SlugUtil.toSlug(req.getCategory());
    String modelSlug = SlugUtil.toSlug(req.getModel());
    String name = (req.getName() == null || req.getName().isBlank()) ? null : req.getName().trim();

    // Resolve category (ưu tiên slug, fallback name)
    CategoryEntity cat = categoryRepository.findBySlug(catSlug)
        .orElseGet(() ->
            categoryRepository.findByNameIgnoreCase(req.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found: " + req.getCategory()))
        );

    // Resolve device model (bắt buộc có slug)
    DeviceModelEntity model = deviceModelRepository.findBySlug(modelSlug)
        .orElseThrow(() -> new NotFoundException("Device model not found: " + req.getModel()));

    Pageable pageable = buildPageable(req.getPage(), req.getSize(), req.getSort());

    // JPQL linh hoạt (đã định nghĩa trong ProductRepository)
    return productRepository.searchByCategoryAndModel(
        cat.getSlug(), model.getSlug(), name, pageable
    );
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private Pageable buildPageable(Integer page, Integer size, String sort) {
    int p = (page == null || page < 0) ? 0 : page;
    int s = (size == null || size <= 0 || size > 100) ? 20 : size;

    // default sort
    Sort sortObj = Sort.by(Sort.Order.desc("createdAt"));

    if (sort != null && !sort.isBlank()) {
      String[] parts = sort.split(",");
      String field = parts[0].trim();
      String dir = parts.length > 1 ? parts[1].trim().toLowerCase() : "desc";
      if (!field.isEmpty()) {
        sortObj = "asc".equals(dir) ? Sort.by(field).ascending() : Sort.by(field).descending();
      }
    }
    return PageRequest.of(p, s, sortObj);
  }

  private ProductRes toRes(ProductEntity e) {
    ProductRes r = new ProductRes();
    r.setId(e.getId());
    r.setName(e.getName());
    r.setPrice(e.getPrice());
    r.setRate(e.getRate());
    r.setColor(e.getColor());
    r.setQuantity(e.getQuantity());
    r.setBrand(e.getBrand());
    r.setActive(e.isActive());
    r.setDiscount(e.getDiscount());
    r.setSubSlug(e.getSubSlug());
    r.setCreatedAt(e.getCreatedAt());
    r.setUpdatedAt(e.getUpdatedAt());

    if (e.getCategory() != null) {
      r.setCategoryName(e.getCategory().getName());
      r.setCategorySlug(e.getCategory().getSlug());
    }

    if (e.getDeviceFits() != null) {
      r.setDeviceModelNames(
          e.getDeviceFits().stream()
              .map(DeviceModelEntity::getName)
              .collect(Collectors.toList())
      );
      r.setDeviceModelSlugs(
          e.getDeviceFits().stream()
              .map(DeviceModelEntity::getSlug)
              .collect(Collectors.toList())
      );
    }
    return r;
  }
}
