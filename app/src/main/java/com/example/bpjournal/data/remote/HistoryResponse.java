package com.example.bpjournal.data.remote;

import java.util.List;

public class HistoryResponse {
    String status;
    String message;
    List<ItemHistoryResponse> data;

    public void setData(List<ItemHistoryResponse> data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemHistoryResponse> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}