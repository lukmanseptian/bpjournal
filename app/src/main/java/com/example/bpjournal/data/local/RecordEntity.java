package com.example.bpjournal.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "Record")
public class RecordEntity implements Serializable {
    @ColumnInfo(name = "record_id")
    @PrimaryKey(autoGenerate = true)
    int id=0;
    @ColumnInfo(name = "sys")
    int sys;
    @ColumnInfo(name = "dias")
    int dias;
    @ColumnInfo(name = "created_at")
    String date;
    @ColumnInfo(name = "online_id")
    int idOnline;

    public int getId() {
        return id;
    }

    public int getSys() {
        return sys;
    }

    public int getDias() {
        return dias;
    }

    public String getDate() {
        return date;
    }

    public int getIdOnline() {
        return idOnline;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSys(int sys) {
        this.sys = sys;
    }
    public void setDias(int dias) {
        this.dias = dias;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIdOnline(int idOnline) {
        this.idOnline = idOnline;
    }
}
