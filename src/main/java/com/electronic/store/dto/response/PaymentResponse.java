package com.electronic.store.dto.response;

import java.util.Map;

public class PaymentResponse {

    private boolean success;
    private String message;
    private Map<String, Object> data;
    private String error;

    // Constructors
    public PaymentResponse() {}

    public PaymentResponse(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public PaymentResponse(boolean success, String message, String error) {
        this.success = success;
        this.message = message;
        this.error = error;
    }

    // Static factory methods
    public static PaymentResponse success(String message, Map<String, Object> data) {
        return new PaymentResponse(true, message, data);
    }

    public static PaymentResponse success(String message) {
        return new PaymentResponse(true, message, (Map<String, Object>) null);
    }

    public static PaymentResponse error(String message) {
        return new PaymentResponse(false, message, message);
    }

    public static PaymentResponse error(String message, String error) {
        return new PaymentResponse(false, message, error);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}