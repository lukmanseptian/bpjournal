package com.example.bpjournal.data.remote;

public class ItemHistoryResponse {
    int id;
    int user_id;
    String diastolic;
    String systolic;
    String category;
    String created_at;

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCategory() {
        return category;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public String getSystolic() {
        return systolic;
    }


}
