package com.example.catchingdata.dto.InventoryDTO;

import com.example.catchingdata.models.ProductModel.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInventory {
    @NotBlank(message = "Product Id is required!")
    private String productId;
    @NotBlank(message = "Quantity Id is required!")
    private int quantity;
}
