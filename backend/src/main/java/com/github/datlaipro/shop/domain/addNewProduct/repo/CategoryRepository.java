package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.CategoryEntity; // chỉnh package đúng với entity của bạn
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}
