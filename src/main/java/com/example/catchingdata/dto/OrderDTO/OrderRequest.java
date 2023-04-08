package com.example.catchingdata.dto.OrderDTO;

import com.example.catchingdata.models.ProductModel.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotBlank(message = "User Id is required")
    private String userId;
    private List<OrderProduct> products;
    @NotBlank(message = "codeDiscount is required")
    private String codeDiscount;
}
