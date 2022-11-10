package com.mycomp.b2borderservice.repository;

import com.mycomp.b2borderservice.entity.BasketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BasketRepository extends MongoRepository<BasketEntity, String> {

    Optional<BasketEntity> findByIdAndBasketProductList_Product_Id(String id, String productId);
    boolean existsByBasketProductList_Product_Id(String productId);

}
