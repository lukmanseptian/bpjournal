package com.example.bpjournal.data.local;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BPJournalDAO {
    //DAO - Data Access Object
    @Insert(onConflict = REPLACE)
    void insert(RecordEntity record);

    @Query("UPDATE Record SET sys = :sys, dias = :dias WHERE record_id = :id")
    void update(int sys, int dias, int id);

    @Delete
    void delete(RecordEntity record);

    @Query("SELECT * FROM Record ORDER BY record_id DESC")
    List<RecordEntity> getAll();

    @Query("SELECT * FROM Record WHERE created_at >= :time ORDER BY record_id DESC")
    List<RecordEntity> getCertainTime(String time);

    @Query("SELECT * FROM Record ORDER BY record_id")
    List<RecordEntity> getAllASC();

    @Query("SELECT * FROM Record WHERE created_at >= :time ORDER BY record_id")
    List<RecordEntity> getCertainTimeASC(String time);

    @Query("DELETE FROM Record")
    void deleteAll();
}
