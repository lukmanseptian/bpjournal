package com.example.bpjournal.ui;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpjournal.R;
import com.example.bpjournal.data.Repository;
import com.example.bpjournal.data.local.BPJournalDAO;
import com.example.bpjournal.data.local.DB;
import com.example.bpjournal.data.local.RecordEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements RecordAdapter.RecordListener  {
    private Toolbar toolbar;

    private DB database;
    private ArrayList<RecordEntity> recordArrayList;
    private Repository repository;
    private RecyclerView recyclerView;
    private RecordAdapter adapter;

    private RadioButton rbWeek;
    private RadioButton rbMonth;

    private ProgressBar progressBar;
    private static final int STORAGE_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        init();

        ImageView imgAdd = (ImageView) findViewById(R.id.img_add);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, AddRecordActivity.class));
            }
        });

        ImageView imgDownload = (ImageView) findViewById(R.id.img_download);

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermissions()) {
                    requestForStoragePermissions();
                } else {
                    downloadPdf();
                }
            }
        });

        showData();

        RadioGroup rgTime = (RadioGroup) findViewById(R.id.rg_time);
        rgTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showData();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    private void init() {
        rbWeek = (RadioButton) findViewById(R.id.rb_week);
        rbMonth = (RadioButton) findViewById(R.id.rb_month);

        database = DB.getInstance(this);
        repository=new Repository(this.getApplication(), this.getApplicationContext());

        recordArrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_record);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showData() {
        recordArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        if (rbWeek.isChecked()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            Date todate1 = cal.getTime();
            String sevenLastDays = sdf.format(todate1);

            recordArrayList.addAll(database.mainDAO().getCertainTime(sevenLastDays));
        } else if (rbMonth.isChecked()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -30);
            Date todate1 = cal.getTime();
            String thirtyLastDays = sdf.format(todate1);

            recordArrayList.addAll(database.mainDAO().getCertainTime(thirtyLastDays));

        } else  {
            recordArrayList.addAll(database.mainDAO().getAll());
        }

        adapter = new RecordAdapter(recordArrayList, this, this.getApplication());
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void refreshActivity() {
        showData();
    }

    public boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        } else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (read && write) {
                    Toast.makeText(RecordActivity.this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecordActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {

                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //Android is 11 (R) or above
                                if (Environment.isExternalStorageManager()) {
                                    //Manage External Storage Permissions Granted
                                    Log.d("ACTIVITY RESULT LAUNCHER", "onActivityResult: Manage External Storage Permissions Granted");
                                } else {
                                    Toast.makeText(RecordActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //Below android 11

                            }
                        }
                    });
    private void downloadPdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(48);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);

        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(24);
        headerPaint.setTypeface(Typeface.DEFAULT_BOLD);

        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(24);

        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setTextSize(24);

        String text = "Blood Pressure Report";
        float x = 300;
        float y = 200;

        canvas.drawText(text, x, y, titlePaint);

        float xLeft = 100;
        y += 100;

        canvas.drawText("Date", xLeft, y, headerPaint);
        canvas.drawText("Systolic", xLeft+400, y, headerPaint);
        canvas.drawText("Diastolic", xLeft+600, y, headerPaint);

        y += 75;

        ArrayList<RecordEntity> records = new ArrayList<>();

        if (rbWeek.isChecked()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            Date todate1 = cal.getTime();
            String sevenLastDays = sdf.format(todate1);

            records.addAll(database.mainDAO().getCertainTimeASC(sevenLastDays));
        } else if (rbMonth.isChecked()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -30);
            Date todate1 = cal.getTime();
            String thirtyLastDays = sdf.format(todate1);

            records.addAll(database.mainDAO().getCertainTimeASC(thirtyLastDays));
        } else  {
            records.addAll(database.mainDAO().getAllASC());
        }


        for (RecordEntity record : records) {
            Paint paint = record.getSys() >= 140 || record.getDias() >= 90 ? redPaint: blackPaint;
            canvas.drawText(record.getDate(), xLeft, y, paint);
            canvas.drawText(record.getSys() + " mmHg", xLeft+400, y, paint);
            canvas.drawText(record.getDias() + " mmHg", xLeft+600, y, paint);
            y += 30;
        }

        document.finishPage(page);

        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Blood Pressure Report.pdf";
        File file = new File(downloadDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Written Successfully!!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}