package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.CartModel.ProductCart;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToCartDto {
    @NotBlank(message = "The cartId is required!")
    private String cartId;
    private ProductCart productCart;
}
