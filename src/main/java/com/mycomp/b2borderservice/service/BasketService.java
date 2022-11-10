package com.mycomp.b2borderservice.service;

import com.mycomp.b2borderservice.controller.dto.request.AddAnItemToBasketRequest;
import com.mycomp.b2borderservice.entity.BasketEntity;
import com.mycomp.b2borderservice.entity.BasketProduct;
import com.mycomp.b2borderservice.entity.Product;
import com.mycomp.b2borderservice.exception.BusinessServiceException;
import com.mycomp.b2borderservice.exception.ValidationException;
import com.mycomp.b2borderservice.repository.BasketRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasketService {

    private final BasketRepository basketRepository;

    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    public BasketEntity findBasketById(String basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new BusinessServiceException(String.format("Basket is not found. [basketId=%s]", basketId)));
    }

    public String createNewBasket() {
        BasketEntity savedBasketEntity = basketRepository.save(new BasketEntity());
        return savedBasketEntity.getId();
    }

    public void addItemToBasket(AddAnItemToBasketRequest addAnItemToBasketRequest) {
        String basketId = addAnItemToBasketRequest.getBasketId();
        BasketEntity basketEntity = basketRepository.findById(basketId)
                .orElseThrow(() -> new ValidationException(String.format("Basket is not found. [basketId=%s]", basketId)));
        boolean productExists = basketRepository.existsByBasketProductList_Product_Id(addAnItemToBasketRequest.getProductId());
        if (productExists) {
            updateOrRemoveItemInBasket(addAnItemToBasketRequest);
        } else {
            BasketProduct basketProduct = buildBasketProduct(addAnItemToBasketRequest);
            addNewItemToBasket(basketEntity, basketProduct);
        }
    }

    private void updateOrRemoveItemInBasket(AddAnItemToBasketRequest addAnItemToBasketRequest) {
        if (addAnItemToBasketRequest.getQuantity() <= 0) {
            removeItemFromBasket(addAnItemToBasketRequest);
        } else {
            updateItemInBasket(addAnItemToBasketRequest);
        }
    }

    private void updateItemInBasket(AddAnItemToBasketRequest addAnItemToBasketRequest) {
        String basketId = addAnItemToBasketRequest.getBasketId();
        String productId = addAnItemToBasketRequest.getProductId();
        int quantity = addAnItemToBasketRequest.getQuantity();

        BasketEntity basketEntity = basketRepository.findByIdAndBasketProductList_Product_Id(basketId, productId)
                .orElseThrow(() -> new BusinessServiceException(String.format("Product is not found. [basketId=%s, productId=%s]", basketId, productId)));

        BasketProduct basketProduct = basketEntity.getBasketProductList()
                .stream()
                .filter(e -> StringUtils.equals(e.getProduct().getId(), productId))
                .findFirst()
                .orElseThrow(() -> new BusinessServiceException(String.format("Product is not found. [productId=%s]", productId)));

        basketProduct.setQuantity(quantity);
        basketRepository.save(basketEntity);
    }

    private void removeItemFromBasket(AddAnItemToBasketRequest addAnItemToBasketRequest) {
        String basketId = addAnItemToBasketRequest.getBasketId();
        String productId = addAnItemToBasketRequest.getProductId();
        BasketEntity basketEntity = basketRepository.findByIdAndBasketProductList_Product_Id(basketId, productId)
                .orElseThrow(() -> new BusinessServiceException(String.format("Product is not found. [basketId=%s, productId=%s]", basketId, productId)));
        List<BasketProduct> basketProductList = basketEntity.getBasketProductList();

        basketProductList.removeIf(e -> StringUtils.equals(e.getProduct().getId(), productId));
        basketRepository.save(basketEntity);
    }

    private void addNewItemToBasket(BasketEntity basketEntity, BasketProduct basketProduct) {
        basketEntity.getBasketProductList().add(basketProduct);
        basketRepository.save(basketEntity);
    }

    private BasketProduct buildBasketProduct(AddAnItemToBasketRequest addAnItemToBasketRequest) {
        Product product = new Product();
        product.setId(addAnItemToBasketRequest.getProductId());

        BasketProduct basketProduct = new BasketProduct();
        basketProduct.setProduct(product);
        basketProduct.setQuantity(addAnItemToBasketRequest.getQuantity());

        return basketProduct;
    }

    public void deleteBasketById(String basketId) {
        basketRepository.deleteById(basketId);
    }

}
