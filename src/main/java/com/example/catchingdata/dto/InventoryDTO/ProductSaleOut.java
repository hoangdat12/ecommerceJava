package com.example.catchingdata.dto.InventoryDTO;

import com.example.catchingdata.models.ProductModel.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaleOut {
    private List<Product> productSaleOuts = new ArrayList<>();
    private boolean isStock;
}
