package com.mycomp.b2borderservice.entity;

import lombok.Data;

@Data
public class BasketProduct {
    private Product product;
    private int quantity;
}
