package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// 👇 THÊM CÁC IMPORT NÀY
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

  // Lấy danh sách ảnh của 1 product theo thứ tự sort_order tăng dần
  List<ProductImageEntity> findByProduct_IdOrderBySortOrderAsc(Long productId);

  // Đếm số ảnh của 1 product
  long countByProduct_Id(Long productId);

  // Xoá toàn bộ ảnh của 1 product (trả về số hàng bị xoá)
  long deleteByProduct_Id(Long productId);

  // Unset cover hiện tại (nếu có)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional // hoặc để @Transactional ở Service gọi method này cũng được
  @Query("update ProductImageEntity pi " +
         "set pi.isCover = false " +
         "where pi.product.id = :productId and pi.isCover = true")
  int unsetCoverForProduct(@Param("productId") Long productId);
}
