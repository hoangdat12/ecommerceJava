package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.CartModel.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToCartDto {
    private String cartId;
    private ProductCart productCart;
}
