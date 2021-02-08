package com.example.transfer.product;

import com.example.transfer.service.GsonService;
import com.example.transfer.service.UrlAddressProviderService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ProductService {

    private final Gson gson;
    private final GsonService gsonService;
    private final UrlAddressProviderService addressProviderService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(Gson gson,
                          GsonService gsonService,
                          UrlAddressProviderService addressProviderService,
                          @Qualifier("productRepo") ProductRepository productRepository) {
        this.gson = gson;
        this.gsonService = gsonService;
        this.addressProviderService = addressProviderService;
        this.productRepository = productRepository;
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Product[] selectAvailableProducts(Optional<Integer> max) {
        String deserializedJSON = gsonService.deserializeJSON(addressProviderService.getProductListUrl());
        Product[] products = gson.fromJson(deserializedJSON, Product[].class);
        return max.map(integer -> Arrays.stream(products).filter(product -> product.getPrice() <= integer)
                .toArray(Product[]::new)).orElse(products);
    }

    public Product selectProduct(int productId) {
        String deserializedJSON = gsonService.deserializeJSON(addressProviderService.getProductListUrl() + productId);
        return gson.fromJson(deserializedJSON, Product.class);
    }

}
