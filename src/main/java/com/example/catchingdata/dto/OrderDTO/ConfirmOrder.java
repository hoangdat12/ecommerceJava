package com.example.catchingdata.dto.OrderDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrder {
    @NotBlank(message = "order Id is required!")
    private String orderId;
}
