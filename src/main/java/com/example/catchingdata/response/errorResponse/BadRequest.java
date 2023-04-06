package com.example.catchingdata.response.errorResponse;

public class BadRequest extends RuntimeException{
    public BadRequest(String errMessage) {
        super(errMessage);
    }
}
