package com.example.catchingdata.dto.ProductDTO;

import com.example.catchingdata.models.ProductModel.Specials;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Code is required!")
    private String code;
    @NotBlank(message = "Name is required!")
    private String name;
    @NotBlank(message = "Author is required!")
    private String author;
    private String description;
    @NotBlank(message = "Image Url is required!")
    private String image_url;
    @NotBlank(message = "Price is required!")
    private Double price;
    @NotBlank(message = "Type is required!")
    private List<String> type;
    private Specials specs;
}
