package com.electronic.store.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BulkDeleteUsersRequest {

    @NotNull(message = "Danh sách user IDs không được null")
    @NotEmpty(message = "Danh sách user IDs không được rỗng")
    private List<Long> userIds;

    public BulkDeleteUsersRequest() {}

    public BulkDeleteUsersRequest(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}