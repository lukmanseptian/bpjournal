package com.example.bpjournal.ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpjournal.R;
import com.example.bpjournal.data.Repository;
import com.example.bpjournal.data.local.DB;
import com.example.bpjournal.data.local.RecordEntity;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private ArrayList<RecordEntity> recordArrayList;
    private Context context;
    private Application application;
    private RecordListener listener;

    public RecordAdapter(ArrayList<RecordEntity> recordArrayList, Context context, Application application) {
        this.recordArrayList = recordArrayList;
        this.context = context;
        this.application = application;

        listener = (RecordListener) context;
    }

    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        DB database = DB.getInstance(context);
        Repository repository = new Repository(application, context);

        RecordEntity record = recordArrayList.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvSystolic.setText(record.getSys() + "");
        holder.tvDiastolic.setText(record.getDias() + "");
        if(record.getSys() >= 140 || record.getDias() >= 90) {
            holder.containerStatus.setVisibility(View.VISIBLE);
        } else {
            holder.containerStatus.setVisibility(View.GONE);
        }
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.mainDAO().delete(record);
                repository.deleteData(record.getIdOnline());
                listener.refreshActivity();
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, EditRecordActivity.class);
                intent.putExtra("SYSTOLIC",record.getSys());
                intent.putExtra("DIASTOLIC",record.getDias());
                intent.putExtra("ID",record.getId());
                intent.putExtra("IDONLINE",record.getIdOnline());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSystolic, tvDiastolic, tvStatus;
        LinearLayout containerStatus;
        ImageView imgEdit, imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSystolic = itemView.findViewById(R.id.tv_systolic);
            tvDiastolic = itemView.findViewById(R.id.tv_diastolic);
            tvStatus = itemView.findViewById(R.id.tv_status);
            containerStatus = itemView.findViewById(R.id.container_status);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    interface RecordListener {
        public void refreshActivity();
    }
}