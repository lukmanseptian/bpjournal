package com.example.bpjournal.data.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("history/all")
    Call<HistoryResponse> getListHistory(
            @Field("userID") int id
    );

    @FormUrlEncoded
    @POST("history/certainTime")
    Call<HistoryResponse> getListHistoryByDate(
            @Field("userID") int id,
            @Field("startDate") String date
    );

    @FormUrlEncoded
    @POST("history/add")
    Call<HistoryResponse> addData(
            @Field("userID") int id,
            @Field("systolic") int sys,
            @Field("diastolic") int dias,
            @Field("date") String date
    );

    @FormUrlEncoded
    @POST("history/edit")
    Call<HistoryResponse> editData(
            @Field("id") int id,
            @Field("userID") int userID,
            @Field("systolic") int sys,
            @Field("diastolic") int dias
    );

    @FormUrlEncoded
    @POST("history/delete")
    Call<HistoryResponse> deleteData(
            @Field("id") int id,
            @Field("userID") int userID
    );

    @FormUrlEncoded
    @POST("auth/registration")
    Call<HistoryResponse> registration(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("gender") String gender,
            @Field("dob") String dob
    );

    @FormUrlEncoded
    @POST("auth/login")
    Call<UserResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );


}
