package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.CartModel.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMultiProductToCartDto {
    private String cartId;
    private List<ProductCart> productCarts;
}
