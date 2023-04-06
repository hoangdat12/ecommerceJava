package com.example.catchingdata.models.ProductModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specials {
    private String brand;
    private String operatingSystem;
    private String memory;
    private String capacity;
    private String size;
    private String color;
    private String wireLess;
    private String material;
}
