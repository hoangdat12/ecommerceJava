package com.example.catchingdata.response.errorResponse;

public class Forbbiden extends RuntimeException{
    public Forbbiden(String errMessage) {
        super(errMessage);
    }
}
