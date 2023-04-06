package com.example.catchingdata.dto.OrderDTO;

import com.example.catchingdata.models.ProductModel.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
    private int quantity;
    private String id;
    private String name;
    private String author;
    private double price;
}
