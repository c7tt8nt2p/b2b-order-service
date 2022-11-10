package com.mycomp.b2borderservice.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutBasketResponse {
    private String basketId;
    private boolean validated;
}
