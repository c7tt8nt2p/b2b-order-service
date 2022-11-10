package com.mycomp.b2borderservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Basket")
@Data
public class BasketEntity {
    @Id
    private String id;
    private List<BasketProduct> basketProductList = new ArrayList<>();
}
