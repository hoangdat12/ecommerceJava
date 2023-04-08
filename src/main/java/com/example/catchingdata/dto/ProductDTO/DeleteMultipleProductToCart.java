package com.example.catchingdata.dto.ProductDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMultipleProductToCart {
    @NotBlank(message = "The userId is required!")
    private String userId;
    @NotBlank(message = "The cartId is required!")
    private String cartId;
    @NotBlank(message = "The productIds is required!")
    private List<String> productIds;
}
