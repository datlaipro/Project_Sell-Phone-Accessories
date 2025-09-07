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

  // GET /api/products?category=cases&model=iphone-16-pro-max&name=case&page=0&size=20&sort=createdAt,desc
  @GetMapping("/products")
  public Page<ProductRes> searchProducts(
      @RequestParam String category,
      @RequestParam String model,
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "createdAt,desc") String sort
  ) {
    SearchProductReq req = new SearchProductReq();
    req.setCategory(category);
    req.setModel(model);
    req.setName(name);
    req.setPage(page);
    req.setSize(size);
    req.setSort(sort);
    log.debug("Search products GET: {}", req);
    return productQueryService.search(req);
  }

  // POST /api/products/search  (body: SearchProductReq)
  @PostMapping("/products/search")
  public Page<ProductRes> searchProductsPost(@RequestBody SearchProductReq req) {
    log.debug("Search products POST: {}", req);
    return productQueryService.search(req);
  }
}
