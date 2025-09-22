package com.github.datlaipro.shop.domain.productDetail.repo;

import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecEntity;
import com.github.datlaipro.shop.domain.productspec.entity.ProductSpecId; // <-- THÊM IMPORT NÀY

import com.github.datlaipro.shop.domain.productDetail.repo.projection.SpecRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repo đọc specs bằng native query + interface projection, KHÔNG cần entity
 * product_specs.
 */
public interface ProductSpecDetailRepository extends Repository<ProductSpecEntity, ProductSpecId> {

    @Query(value = """
            SELECT ps.attr AS attr, ps.val AS val
            FROM product_specs ps
            WHERE ps.product_id = :id
            """, nativeQuery = true)
    List<SpecRow> findAllByProductId(@Param("id") Long productId);
}
