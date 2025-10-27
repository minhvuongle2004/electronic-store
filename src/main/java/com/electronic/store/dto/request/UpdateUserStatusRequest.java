package com.electronic.store.dto.request;

import com.electronic.store.entity.User;
import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {

    @NotNull(message = "Status không được để trống")
    private User.Status status;

    public UpdateUserStatusRequest() {}

    public UpdateUserStatusRequest(User.Status status) {
        this.status = status;
    }

    public User.Status getStatus() {
        return status;
    }

    public void setStatus(User.Status status) {
        this.status = status;
    }
}