package com.example.bpjournal.data;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.health.connect.datatypes.Record;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.example.bpjournal.data.local.BPJournalDAO;
import com.example.bpjournal.data.local.DB;
import com.example.bpjournal.data.local.RecordEntity;
import com.example.bpjournal.data.remote.Api;
import com.example.bpjournal.data.remote.HistoryResponse;
import com.example.bpjournal.data.remote.ItemHistoryResponse;
import com.example.bpjournal.data.remote.Server;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    public BPJournalDAO dao;
    private DB database;
    private Api api;
    private SharedPreferences sharedPreferences;
    private Context context;

    public Repository(Application application, Context context){
        api = Server.getAPIService();
        database=DB.getInstance(application);
        dao=database.mainDAO();
        this.context = context;
    }

    private int getLoggedUserID() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt("id", 0);
        return id;
    }

    public void insert(List<RecordEntity> records){
        new InsertAsyncTask(dao).execute(records);
    }

    public void downloadAll(){
        // id diambil dari sharedPref
        Call<HistoryResponse> call = api.getListHistory(getLoggedUserID());
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if(response.isSuccessful()) {
                    ArrayList<RecordEntity> list = new ArrayList<>();
                    HistoryResponse body = response.body();
                    for (ItemHistoryResponse item  : body.getData()) {
                        RecordEntity record = new RecordEntity();
                        record.setSys(Integer.parseInt(item.getSystolic()));
                        record.setDias(Integer.parseInt(item.getDiastolic()));
                        record.setDate(item.getCreated_at());
                        record.setIdOnline(item.getId());
                        list.add(record);
                    }

                    insert(list);
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {

            }
        });
    }

    private static class InsertAsyncTask extends AsyncTask<List<RecordEntity>,Void,Void> {
        private BPJournalDAO dao;

        public InsertAsyncTask(BPJournalDAO dao)
        {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(List<RecordEntity>... lists) {
            dao.deleteAll();
            for (List<RecordEntity> records  : lists) {
                for (RecordEntity record: records) {
                    dao.insert(record);
                }
            }
            return null;
        }
    }

    public void deleteAll() {
        Thread newThread = new Thread(() -> {
            dao.deleteAll();
        });
        newThread.start();

    }

    public void insertData(int sys, int dias, String date){
        // id diambil dari sharedPref
        Call<HistoryResponse> call = api.addData(getLoggedUserID(), sys, dias, date);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {

            }
        });
    }

    public void updateData(int id, int sys, int dias){
        // id diambil dari sharedPref
        Call<HistoryResponse> call = api.editData(id, getLoggedUserID(), sys, dias);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {

            }
        });
    }

    public void deleteData(int id){
        // id diambil dari sharedPref
        Call<HistoryResponse> call = api.deleteData(id, getLoggedUserID());
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {

            }
        });
    }


}
