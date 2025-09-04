package com.github.datlaipro.shop.domain.addNewProduct.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.AddNewProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddNewProductRepository extends JpaRepository<AddNewProductEntity, Long> {}
