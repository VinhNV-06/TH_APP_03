package com.example.thapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private EditText etTitle;
    private EditText etLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Switch switchReminder;
    private Button btnSaveAppointment;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_appointment);

        View root = findViewById(R.id.rootAdd);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment);

        timePicker.setIs24HourView(true);

        btnBackHeader.setOnClickListener(v -> finish());
        btnSaveAppointment.setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Tên lịch hẹn không được để trống");
            etTitle.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("Địa điểm không được để trống");
            etLocation.requestFocus();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, day, hour, minute, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Calendar nowCal = Calendar.getInstance();

        if (!selectedCal.after(nowCal)) {
            Toast.makeText(this, "Thời gian hẹn phải lớn hơn thời điểm hiện tại", Toast.LENGTH_LONG).show();
            return;
        }

        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        boolean reminder = switchReminder.isChecked();

        Appointment appointment = new Appointment(title, location, dateStr, timeStr, reminder);
        long result = dbHelper.insertAppointment(appointment);

        if (result != -1) {
            Toast.makeText(this, "Thêm lịch hẹn thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu lịch hẹn", Toast.LENGTH_SHORT).show();
        }
    }
}
