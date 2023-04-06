package com.example.catchingdata.ultils.discount;

import lombok.Data;

@Data
public class Condition {
    private double greater;
    private double lesser;
    public Condition() {

    }
    public Condition(double greater, double lesser) {
        this.greater = greater;
        this.lesser = lesser;
    }
    public Condition(String message, double condition) {
        if (message == "lesser") {
            this.lesser = condition;
        } else {
            this.greater = condition;
        }
    }
}
