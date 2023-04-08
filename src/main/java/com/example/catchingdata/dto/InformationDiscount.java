package com.example.catchingdata.dto;

import com.example.catchingdata.ultils.discount.Condition;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationDiscount {
    @NotBlank(message = "Type is required!")
    private String type;
    @NotBlank(message = "discountCode is required!")
    private String discountCode;
    @NotBlank(message = "Quantity is required!")
    private int quantity;
    @NotBlank(message = "Discount is required!")
    private double discount;
    @NotBlank(message = "Key is required!")
    private String key;
    private String description;
    private Condition condition;
    private LocalDateTime createdAt;
    @NotBlank(message = "effectedFrom is required!")
    private int effectedFrom;
    @NotBlank(message = "ExpiresIn is required!")
    private int expiresIn;
}
