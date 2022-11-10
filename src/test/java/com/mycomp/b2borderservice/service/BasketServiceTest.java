package com.mycomp.b2borderservice.service;

import com.mycomp.b2borderservice.controller.dto.request.AddAnItemToBasketRequest;
import com.mycomp.b2borderservice.entity.BasketEntity;
import com.mycomp.b2borderservice.entity.BasketProduct;
import com.mycomp.b2borderservice.entity.Product;
import com.mycomp.b2borderservice.exception.BusinessServiceException;
import com.mycomp.b2borderservice.exception.ValidationException;
import com.mycomp.b2borderservice.repository.BasketRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private BasketService basketService;

    private AddAnItemToBasketRequest mockAddAnItemToBasketRequest(String basketId) {
        return mockAddAnItemToBasketRequest(basketId, null, 0);
    }

    private AddAnItemToBasketRequest mockAddAnItemToBasketRequest(String basketId, String productId, int quantity) {
        AddAnItemToBasketRequest addAnItemToBasketRequest = new AddAnItemToBasketRequest();
        addAnItemToBasketRequest.setBasketId(basketId);
        addAnItemToBasketRequest.setProductId(productId);
        addAnItemToBasketRequest.setQuantity(quantity);
        return addAnItemToBasketRequest;
    }

    private BasketEntity mockBasketEntity(String basketId) {
        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setId(basketId);
        return basketEntity;
    }

    private BasketEntity mockBasketEntity(String basketId, String productId, int quantity) {
        Product product = new Product();
        product.setId(productId);

        BasketProduct basketProduct = new BasketProduct();
        basketProduct.setProduct(product);
        basketProduct.setQuantity(quantity);

        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setId(basketId);
        basketEntity.setBasketProductList(new ArrayList<>(List.of(basketProduct)));

        return basketEntity;
    }

    @Nested
    class FindBasketByIdTests {
        @Test
        void findBasketById_ShouldBeReturned() {
            String basketId = "112233";
            BasketEntity basketEntity = mockBasketEntity(basketId);
            when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));

            BasketEntity basketEntityResult = basketService.findBasketById(basketId);
            assertThat(basketEntityResult).isEqualTo(basketEntity);
        }

        @Test
        void findBasketById_ShouldThrowException_WhenBasketIsNotFound() {
            String basketId = "112233";
            when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> basketService.findBasketById(basketId))
                    .isInstanceOf(BusinessServiceException.class)
                    .hasMessage(String.format("Basket is not found. [basketId=%s]", basketId));
        }
    }

    @Test
    void createNewBasket_ShouldBeCreated() {
        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setId("3333");
        when(basketRepository.save(any(BasketEntity.class))).thenReturn(basketEntity);

        String basketId = basketService.createNewBasket();
        assertThat(basketId).isEqualTo(basketEntity.getId());
    }

    @Nested
    class AddItemToBasketTests {
        @Test
        void addItemToBasket_ShouldThrowException_WhenBasketIsNotFound() {
            String basketId = "123";
            AddAnItemToBasketRequest addAnItemToBasketRequest = mockAddAnItemToBasketRequest(basketId);

            when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> basketService.addItemToBasket(addAnItemToBasketRequest))
                    .isInstanceOf(ValidationException.class)
                    .hasMessage(String.format("Basket is not found. [basketId=%s]", basketId));
        }

        @Test
        void addItemToBasket_ShouldUpdate_WhenProductAlreadyExistsAndQuantityIsNotZero() {
            String basketId = "123";
            String productId = "aaa";
            int quantity = 5;
            AddAnItemToBasketRequest addAnItemToBasketRequest = mockAddAnItemToBasketRequest(basketId, productId, quantity);
            BasketEntity basketEntity = mockBasketEntity(basketId, productId, 1);

            when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));
            when(basketRepository.existsByBasketProductList_Product_Id(productId)).thenReturn(true);
            when(basketRepository.findByIdAndBasketProductList_Product_Id(basketId, productId)).thenReturn(Optional.of(basketEntity));

            basketService.addItemToBasket(addAnItemToBasketRequest);

            verify(basketRepository).save(basketEntity);
            assertThat(basketEntity.getBasketProductList()).first().satisfies(e -> assertThat(e.getQuantity()).isEqualTo(quantity));
        }

        @Test
        void addItemToBasket_ShouldRemove_WhenProductAlreadyExistsAndQuantityIsZero() {
            String basketId = "123";
            String productId = "aaa";
            int quantity = 0;
            AddAnItemToBasketRequest addAnItemToBasketRequest = mockAddAnItemToBasketRequest(basketId, productId, quantity);
            BasketEntity basketEntity = mockBasketEntity(basketId, productId, 1);

            when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));
            when(basketRepository.existsByBasketProductList_Product_Id(productId)).thenReturn(true);
            when(basketRepository.findByIdAndBasketProductList_Product_Id(basketId, productId)).thenReturn(Optional.of(basketEntity));

            basketService.addItemToBasket(addAnItemToBasketRequest);

            verify(basketRepository).save(basketEntity);
            assertThat(basketEntity.getBasketProductList()).isEmpty();
        }

        @Test
        void addItemToBasket_ShouldAdd_WhenProductNotExist() {
            String basketId = "456";
            String productId = "bbb";
            int quantity = 0;
            AddAnItemToBasketRequest addAnItemToBasketRequest = mockAddAnItemToBasketRequest(basketId, productId, quantity);
            BasketEntity basketEntity = mockBasketEntity(basketId);

            when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));
            when(basketRepository.existsByBasketProductList_Product_Id(productId)).thenReturn(false);

            basketService.addItemToBasket(addAnItemToBasketRequest);

            verify(basketRepository).save(basketEntity);
            assertThat(basketEntity.getBasketProductList()).hasSize(1);
        }
    }

    @Test
    void deleteBasketById_ShouldBeDeleted() {
        String basketId = "567";
        basketService.deleteBasketById(basketId);

        verify(basketRepository).deleteById(basketId);
    }

}