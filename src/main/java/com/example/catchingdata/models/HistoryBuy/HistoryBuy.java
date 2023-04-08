package com.example.catchingdata.models.HistoryBuy;

import com.example.catchingdata.dto.OrderDTO.OrderProduct;
import com.example.catchingdata.models.ProductModel.Product;
import com.example.catchingdata.models.UserModel.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "histories")
public class HistoryBuy {
    @Id
    private String id;
    @DBRef
    private User user;
    private List<OrderProduct> productsBought;
}
