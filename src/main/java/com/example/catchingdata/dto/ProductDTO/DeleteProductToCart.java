package com.example.catchingdata.dto.ProductDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductToCart {
    @NotBlank(message = "The cartId is required!")
    private String cartId;
    @NotBlank(message = "The productId is required!")
    private String productId;
}
