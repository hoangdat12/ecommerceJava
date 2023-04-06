package com.example.catchingdata.models.CartModel;

import com.example.catchingdata.models.UserModel.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    @DBRef
    private User user;
    private List<ProductCart> products = new ArrayList<>();
}
