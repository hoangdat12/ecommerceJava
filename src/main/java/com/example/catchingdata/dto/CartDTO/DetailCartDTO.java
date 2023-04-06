package com.example.catchingdata.dto.CartDTO;

import com.example.catchingdata.models.CartModel.ProductCart;
import com.example.catchingdata.models.UserModel.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailCartDTO {
    private String id;
    private String userId;
    private List<ProductCart> products = new ArrayList<>();
}
