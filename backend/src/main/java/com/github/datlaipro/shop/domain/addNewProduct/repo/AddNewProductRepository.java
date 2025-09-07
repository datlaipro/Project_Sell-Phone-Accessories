package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.AddNewProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface AddNewProductRepository extends JpaRepository<AddNewProductEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
              INSERT IGNORE INTO product_device_fit(product_id, device_model_id)
              VALUES (:productId, :modelId)
            """, nativeQuery = true)
    int linkDevice(@Param("productId") Long productId,
            @Param("modelId") Long modelId);
}
