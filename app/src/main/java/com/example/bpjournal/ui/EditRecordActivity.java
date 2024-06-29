package com.example.bpjournal.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bpjournal.R;
import com.example.bpjournal.data.Repository;
import com.example.bpjournal.data.local.DB;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRecordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button btnSave;
    private EditText etSystolic;
    private EditText etDiastolic;
    private DB database;
    private Repository repository;

    private int id = 0;
    private int idOnline = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord();
            }
        });
    }


    private void init() {
        btnSave = (Button) findViewById(R.id.btn_save);
        etSystolic = (EditText) findViewById(R.id.et_systolic);
        etDiastolic = (EditText) findViewById(R.id.et_diastolic);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            etSystolic.setText(bundle.getInt("SYSTOLIC") + "");
            etDiastolic.setText(bundle.getInt("DIASTOLIC") + "");
            id = bundle.getInt("ID");
            idOnline = bundle.getInt("IDONLINE");
        }

        database = DB.getInstance(this);
        repository=new Repository(this.getApplication(), this.getApplicationContext());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateRecord() {
        if (etSystolic.getText().length()==0 || etDiastolic.getText().length()==0){
            showToast("fill systolic and diastolic...");
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = new Date();
            String strDate = sdf.format(now);

            int sys = Integer.parseInt(etSystolic.getText().toString());
            int dias = Integer.parseInt(etDiastolic.getText().toString());

            database.mainDAO().update(sys, dias, id);
            repository.updateData(idOnline, sys, dias);
            showToast("updated successfully...");
            finish();
        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}