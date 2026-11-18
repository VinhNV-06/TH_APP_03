package com.example.thapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private TextView tvTitle;
    private TextView tvLocation;
    private TextView tvDateTime;
    private Switch switchReminder;
    private TextView tvReminderStatus;
    private Button btnBack;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private int appointmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_appointment_detail);

        View root = findViewById(R.id.rootDetail);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        switchReminder = findViewById(R.id.switchReminder);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        appointmentId = getIntent().getIntExtra("APPOINTMENT_ID", -1);
        if (appointmentId == -1) {
            finish();
            return;
        }

        Appointment appointment = dbHelper.getAppointmentById(appointmentId);
        if (appointment == null) {
            finish();
            return;
        }

        tvTitle.setText(appointment.getTitle());
        tvLocation.setText(appointment.getLocation());
        tvDateTime.setText(formatDateTime(appointment.getDate(), appointment.getTime()));

        switchReminder.setChecked(appointment.isReminder());
        tvReminderStatus.setText(appointment.isReminder() ? "Đang bật" : "Đang tắt");

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvReminderStatus.setText(isChecked ? "Đang bật" : "Đang tắt");
            dbHelper.updateReminder(appointmentId, isChecked);
        });

        btnBackHeader.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteAppointment(appointmentId);
            Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String formatDateTime(String dateStr, String timeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = date != null ? outputFormat.format(date) : dateStr;
            return formattedDate + " · " + timeStr;
        } catch (Exception e) {
            return dateStr + " · " + timeStr;
        }
    }
}
