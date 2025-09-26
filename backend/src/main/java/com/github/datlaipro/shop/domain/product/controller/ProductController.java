package com.github.datlaipro.shop.domain.product.controller;

import com.github.datlaipro.shop.domain.product.dto.ProductRes;
import com.github.datlaipro.shop.domain.product.dto.SearchProductReq;
import com.github.datlaipro.shop.domain.product.service.ProductQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

  private static final Logger log = LoggerFactory.getLogger(ProductController.class);
  private final ProductQueryService productQueryService;

  public ProductController(ProductQueryService productQueryService) {
    this.productQueryService = productQueryService;
  }

  // GET /api/products?category=cases&model=iphone-16-pro-max&name=case&page=0&sort=createdAt,desc
  // LƯU Ý: page-size luôn 15 (được ép trong service), tham số 'size' bị loại bỏ để tránh nhầm lẫn.
  @GetMapping("/products")
  public Page<ProductRes> searchProducts(
      @RequestParam String category,
      @RequestParam String model,
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "createdAt,desc") String sort // có thể set "createdAt,desc;id,desc" ở service
  ) {
    SearchProductReq req = new SearchProductReq();
    req.setCategory(category);
    req.setModel(model);
    req.setName(name);
    req.setPage(page);
    // KHÔNG set size -> service sẽ dùng PAGE_SIZE = 15
    req.setSort(sort);

    log.debug("Search products GET (size fixed at 15 in service): {}", req);
    return productQueryService.search(req);
  }

  // POST /api/products/search  (body: SearchProductReq)
  // Nếu body có 'size', service của bạn vẫn không dùng và luôn cố định 15.
  @PostMapping("/products/search")
  public Page<ProductRes> searchProductsPost(@RequestBody SearchProductReq req) {
    // Đảm bảo page-size bị ép 15 ở service; ở đây chỉ log cho rõ.
    log.debug("Search products POST (size fixed at 15 in service): {}", req);
    return productQueryService.search(req);
  }
}
