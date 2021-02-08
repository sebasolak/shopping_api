package com.example.transfer.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("cartRepo")
@Transactional(readOnly = true)
public interface CartRepository extends JpaRepository<Cart, Integer> {
}
