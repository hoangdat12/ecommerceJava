package com.example.catchingdata.ultils.discount;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class DiscountFactory {
    public DiscountFactory() {

    }
    public DiscountCore createDiscount(
            String type,
            String discountCode,
            double discount,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        switch (type) {
            case "percentage" -> {
                return new PercentageDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            }
            case "amount" -> {
                return new AmountDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            }
            case "coin" -> {
                return new CoinDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            }
            default -> {
                log.info("Invalid discount type: {}", type);
                throw new IllegalArgumentException("Invalid discount type: " + type);
            }
        }
    }
}
