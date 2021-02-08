package com.example.transfer.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;

    @Autowired
    public CartService(@Qualifier("cartRepo") CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
