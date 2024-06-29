package com.example.bpjournal.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.bpjournal.R;
import com.example.bpjournal.data.remote.Api;
import com.example.bpjournal.data.remote.HistoryResponse;
import com.example.bpjournal.data.remote.Server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrasiActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Api api;
    private Context context;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private EditText etDob;
    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("username", "");
        if(!username.equals("")) {
            Intent intent = new Intent(RegistrasiActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regisrasi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        api = Server.getAPIService();
        context = this;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        init();
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        TextView tvLogin = (TextView) findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnRegistrasi = (Button) findViewById(R.id.btn_registrasi);
        btnRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrasi();
            }
        });
    }

    private void init() {
         etUsername = (EditText) findViewById(R.id.et_username);
         etEmail = (EditText) findViewById(R.id.et_email);
         etPassword = (EditText) findViewById(R.id.et_password);
         etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
         rgGender = (RadioGroup) findViewById(R.id.rg_gender);
         rbMale = (RadioButton) findViewById(R.id.rb_male);
         rbFemale = (RadioButton) findViewById(R.id.rb_female);
         etDob = (EditText) findViewById(R.id.et_dob);
    }
    private void registrasi() {
        String gender = "";
        if(rbMale.isChecked()) {
            gender = "L";
        } else if(rbFemale.isChecked()) {
            gender = "P";
        }

        if(gender.isEmpty()) {
            rbMale.setError("Pilih salah satu");
        } else {
            rbMale.setError(null);
        }

        String username = etUsername.getText().toString().trim();
        if(username.isEmpty())   {
            etUsername.setError("Username tidak boleh kosong");
        } else {
            etUsername.setError(null);
        }

        String email = etEmail.getText().toString().trim();
        if(email.isEmpty())   {
            etEmail.setError("Email tidak boleh kosong");
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if(password.isEmpty())   {
            etPassword.setError("Password tidak boleh kosong");
        } else {
            etPassword.setError(null);
        }

        String confirmPassword = etConfirmPassword.getText().toString();
        if (confirmPassword.isEmpty())   {
            etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
        }else if(!confirmPassword.equals(password))   {
            etConfirmPassword.setError("Password tidak sama");
        } else {
            etPassword.setError(null);
        }

        String dob = etDob.getText().toString();
        if(dob.isEmpty())   {
            etDob.setError("Tanggal lahir tidak boleh kosong");
        } else {
            etDob.setError(null);
        }

        Call<HistoryResponse> call = api.registration(username, password, email, gender, dob);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if(response.isSuccessful()) {
                    HistoryResponse body = response.body();
                    Toast.makeText(context, body.getMessage(), Toast.LENGTH_SHORT).show();
                    if (body.getStatus().equals("success")) {
                        Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, body.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateDialog(){

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                etDob.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}