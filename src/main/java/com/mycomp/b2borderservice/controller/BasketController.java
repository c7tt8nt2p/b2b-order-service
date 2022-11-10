package com.mycomp.b2borderservice.controller;

import com.mycomp.b2borderservice.controller.dto.request.AddAnItemToBasketRequest;
import com.mycomp.b2borderservice.controller.dto.request.CheckoutBasketRequest;
import com.mycomp.b2borderservice.controller.dto.response.*;
import com.mycomp.b2borderservice.entity.BasketEntity;
import com.mycomp.b2borderservice.service.BasketService;
import com.mycomp.b2borderservice.service.VatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
public class BasketController {

    private final BasketService basketService;
    private final VatService vatService;

    @Autowired
    public BasketController(BasketService basketService, VatService vatService) {
        this.basketService = basketService;
        this.vatService = vatService;
    }

    @GetMapping("/baskets/{basketId}")
    public ListItemsInBasketResponse getProductsInBasket(@PathVariable @NotBlank String basketId) {
        System.out.println("okok");
        BasketEntity basketEntity = basketService.findBasketById(basketId);
        return ListItemsInBasketResponse.fromBasketEntity(basketEntity);
    }

    @PostMapping("/baskets")
    public CreateNewBasketResponse createNewBasket() {
        String id = basketService.createNewBasket();
        return CreateNewBasketResponse.builder().basketId(id).build();
    }

    @PatchMapping("/baskets/item")
    public AddAnItemToBasketResponse addItemToBasket(@RequestBody @Valid AddAnItemToBasketRequest addAnItemToBasketRequest) {
        basketService.addItemToBasket(addAnItemToBasketRequest);
        return AddAnItemToBasketResponse.builder().productId(addAnItemToBasketRequest.getProductId()).build();
    }

    @DeleteMapping("/baskets/{basketId}")
    public DeleteBasketResponse deleteBasket(@PathVariable @NotBlank String basketId) {
        basketService.deleteBasketById(basketId);
        return DeleteBasketResponse.builder().basketId(basketId).build();
    }

    @PostMapping("/baskets/checkout")
    public CheckoutBasketResponse checkoutBasket(@RequestBody @Valid CheckoutBasketRequest checkoutBasketRequest) {
        String basketId = checkoutBasketRequest.getBasketId();
        String vatNumber = checkoutBasketRequest.getVatNumber();
        boolean isVatValid = vatService.validateVatNumber(vatNumber);
        return CheckoutBasketResponse.builder()
                .basketId(basketId)
                .validated(isVatValid)
                .build();
    }

}
