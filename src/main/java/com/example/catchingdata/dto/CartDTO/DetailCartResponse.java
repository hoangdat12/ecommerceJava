package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.ProductModel.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailCartResponse {
    private String id;
    private String userId;
    private List<Product> products;
    private double price;
    private int quantityAllProduct;
}
