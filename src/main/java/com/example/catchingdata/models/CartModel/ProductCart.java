package com.example.catchingdata.models.CartModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCart {
    private String productId;
    private Integer quantity;
    private Float price;
}
