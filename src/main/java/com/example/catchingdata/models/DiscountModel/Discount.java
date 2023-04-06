package com.example.catchingdata.models.DiscountModel;

import com.example.catchingdata.ultils.discount.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "discounts")
public class Discount {
    @Id
    private String id;
    private String type;
    private String discountCode;
    private int quantity;
    private double discount;
    private String key;
    private String description;
    private Condition condition;
    private List<UserUsedDiscount> userUsed;
//    @Indexed(name = "createdAtIndex", expireAfterSeconds = 172800)
//    @Field(name = "createdAt", targetType = FieldType.TIMESTAMP)
    private LocalDateTime createdAt;
    private int effectedFrom;
    private int expiresIn;
}
