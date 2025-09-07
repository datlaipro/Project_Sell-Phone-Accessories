package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

  // Lấy cover của 1 product (khi view chi tiết)
  Optional<ProductImageEntity>
  findFirstByProduct_IdAndIsCoverTrueOrderByIdDesc(Long productId);

  // Lấy cover cho N product id (khi list/search)
  @Query("""
    select pi from ProductImageEntity pi
    where pi.isCover = true and pi.product.id in :ids
  """)
  List<ProductImageEntity> findCoversByProductIds(@Param("ids") Collection<Long> productIds);

  // (Tuỳ chọn) fallback: nếu chưa set is_cover=true, lấy tấm đầu tiên theo sort_order rồi id
  @Query("""
    select pi from ProductImageEntity pi
    where pi.product.id = :productId
    order by pi.isCover desc, pi.sortOrder asc, pi.id desc
  """)
  List<ProductImageEntity> findBestFirstForProduct(@Param("productId") Long productId);
}
