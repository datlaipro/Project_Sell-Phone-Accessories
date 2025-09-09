package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.AddNewProductEntity;
import com.github.datlaipro.shop.domain.product.entity.DeviceModelEntity; // 👈 thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AddNewProductRepository extends JpaRepository<AddNewProductEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = """
        INSERT INTO product_device_fit(product_id, device_model_id)
        VALUES (:#{#p.id}, :#{#m.id})
        """, nativeQuery = true)
    int linkDevice(@Param("p") AddNewProductEntity product,
                   @Param("m") DeviceModelEntity model);
}
