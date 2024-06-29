package com.example.bpjournal.data.remote;

import android.content.ClipData;

import java.util.List;

public class UserResponse {
    String status;
    String message;
    ItemUserResponse data;

    public void setData(ItemUserResponse data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ItemUserResponse getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
