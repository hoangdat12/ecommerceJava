package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.CartModel.ProductCart;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMultiProductToCartDto {
    @NotBlank(message = "The cartId is required!")
    private String cartId;
    private List<ProductCart> productCarts;
}
