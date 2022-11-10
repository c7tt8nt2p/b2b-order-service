package com.mycomp.b2borderservice.controller.dto.response;

import com.mycomp.b2borderservice.entity.BasketEntity;
import com.mycomp.b2borderservice.entity.BasketProduct;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ListItemsInBasketResponse {
    private String basketId;
    private List<Product> productList;

    public static ListItemsInBasketResponse fromBasketEntity(BasketEntity basketEntity) {
        String basketId = basketEntity.getId();
        List<Product> productsList = toProducts(basketEntity.getBasketProductList());
        return ListItemsInBasketResponse.builder()
                .basketId(basketId)
                .productList(productsList)
                .build();
    }

    private static List<Product> toProducts(List<BasketProduct> basketProductList) {
        return basketProductList.stream()
                .map(ListItemsInBasketResponse::toProduct)
                .collect(Collectors.toList());
    }

    private static Product toProduct(BasketProduct basketProduct) {
        String productId = basketProduct.getProduct().getId();
        int quantity = basketProduct.getQuantity();
        return Product.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}

@Data
@Builder
class Product {
    private String productId;
    private int quantity;
}
