package com.example.catchingdata.dto.ProductDTO;

import com.example.catchingdata.models.ProductModel.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDTO {
    private List<Product> products;
    private int page;
    private int limit;
    private String sortedBy;
    private String type;
    private int total;
}
