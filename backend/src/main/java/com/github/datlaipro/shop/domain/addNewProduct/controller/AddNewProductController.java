package com.github.datlaipro.shop.domain.addNewProduct.controller;

import com.github.datlaipro.shop.domain.addNewProduct.dto.AddNewProductReq;
import com.github.datlaipro.shop.domain.addNewProduct.dto.AddNewProductRes;
import com.github.datlaipro.shop.domain.addNewProduct.service.AddNewProductService;
import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminRes;
import com.github.datlaipro.shop.storage.CloudflareR2Service;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AddNewProductController {

  private final AddNewProductService productService;
  private final CloudflareR2Service r2;

  public AddNewProductController(AddNewProductService productService, CloudflareR2Service r2) {
    this.productService = productService;
    this.r2 = r2;
  }

 @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<AddNewProductRes> addNewProduct(
    @AuthenticationPrincipal LoginAdminRes admin,
    @RequestPart("data") @Valid AddNewProductReq req,
    @RequestPart(name = "files", required = false) MultipartFile[] files
) {
  AddNewProductRes res = productService.createProduct(req, admin, files);
  return ResponseEntity.created(URI.create("/admin/product/" + res.getId())).body(res);
}

}
