package com.mycomp.b2borderservice.controller.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckoutBasketRequest {
    @NotBlank
    private String basketId;
    @NotBlank
    private String vatNumber;
}
