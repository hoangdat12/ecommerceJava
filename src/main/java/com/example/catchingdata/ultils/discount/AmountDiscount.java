package com.example.catchingdata.ultils.discount;

import java.time.LocalDateTime;

public class AmountDiscount extends DiscountCore {
    public AmountDiscount(
            String discountCode,
            double discount,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        super(discountCode, discount, "amount", key, description, createdAt, effectedFrom, expiresIn, condition);
    }

    @Override
    public double getPrice(double price) {
        double priceAfterApply = price - this.getDiscount();
        if (priceAfterApply < 0) {
            return 0;
        } else {
            return priceAfterApply;
        }
    }
}
