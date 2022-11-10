package com.mycomp.b2borderservice.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddAnItemToBasketRequest {
    @NotBlank
    private String basketId;
    @NotBlank
    private String productId;
    private int quantity;
}
