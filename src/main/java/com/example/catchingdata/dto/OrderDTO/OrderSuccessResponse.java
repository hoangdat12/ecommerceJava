package com.example.catchingdata.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSuccessResponse {
    private List<OrderProduct> products;
    private double price;
}
