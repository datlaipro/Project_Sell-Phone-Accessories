package com.github.datlaipro.shop.domain.productspec.repo;

import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecEntity;
import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductSpecRepository extends JpaRepository<ProductSpecEntity, ProductSpecId> {

    // Lấy toàn bộ specs của một product
    List<ProductSpecEntity> findByIdProductId(Long productId);

    // Lấy specs của nhiều product
    List<ProductSpecEntity> findByIdProductIdIn(Collection<Long> productIds);

    // Lấy 1 spec cụ thể theo (product_id, attr)
    Optional<ProductSpecEntity> findByIdProductIdAndIdAttr(Long productId, String attr);

    // Kiểm tra đã có (product_id, attr) chưa
    boolean existsByIdProductIdAndIdAttr(Long productId, String attr);

    // Xoá toàn bộ specs một product
    void deleteByIdProductId(Long productId);

    // Cập nhật value nhanh cho 1 key cụ thể
    @Transactional
    @Modifying
    @Query("""
           UPDATE ProductSpecEntity s
           SET s.val = :val
           WHERE s.id.productId = :productId AND s.id.attr = :attr
           """)
    int updateVal(@Param("productId") Long productId,
                  @Param("attr") String attr,
                  @Param("val") String val);

    // Projection gọn: trả về (attr, val) cho 1 product
    @Query("""
           SELECT s.id.attr AS attr, s.val AS val
           FROM ProductSpecEntity s
           WHERE s.id.productId = :productId
           """)
    List<AttrVal> findAttrValByProductId(@Param("productId") Long productId);

    interface AttrVal {
        String getAttr();
        String getVal();
    }

    // (Tuỳ chọn) Lấy JSON specs (MySQL) — trả về chuỗi JSON hoặc null nếu không có rows
    @Query(value = "SELECT JSON_OBJECTAGG(attr, val) FROM product_specs WHERE product_id = :productId",
           nativeQuery = true)
    String specsAsJson(@Param("productId") Long productId);
}
