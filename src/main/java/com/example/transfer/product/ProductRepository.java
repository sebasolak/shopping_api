package com.example.transfer.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("productRepo")
@Transactional(readOnly = true)
public interface ProductRepository extends JpaRepository<Product,Integer> {
}
