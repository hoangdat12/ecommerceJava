package com.example.catchingdata.dto;

import com.example.catchingdata.dto.OrderDTO.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponseDTO {
    private String userId;
    private List<OrderProduct> histories;
}
