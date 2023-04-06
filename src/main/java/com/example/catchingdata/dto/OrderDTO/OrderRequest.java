package com.example.catchingdata.dto.OrderDTO;

import com.example.catchingdata.models.ProductModel.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String userId;
    private List<OrderProduct> products;
    private String codeDiscount;
}
