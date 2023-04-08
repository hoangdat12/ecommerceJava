package com.example.catchingdata.dto.ProductDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductIds {
    @NotBlank(message = "product Id is required!")
    private List<String> productIds;
}
