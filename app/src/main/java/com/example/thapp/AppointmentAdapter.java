package com.example.thapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    private ArrayList<Appointment> appointments;
    private final OnItemClickListener listener;

    public AppointmentAdapter(ArrayList<Appointment> appointments, OnItemClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    public void updateData(ArrayList<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment item = appointments.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvDateTime, tvReminderBadge;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvReminderBadge = itemView.findViewById(R.id.tvReminderBadge);
        }

        public void bind(final Appointment appointment, final OnItemClickListener listener) {
            tvTitle.setText(appointment.getTitle());
            tvLocation.setText("Địa điểm: " + appointment.getLocation());
            tvDateTime.setText(formatDateTime(appointment.getDate(), appointment.getTime()));

            if (appointment.isReminder()) {
                tvReminderBadge.setText("Có nhắc");
                tvReminderBadge.setBackgroundResource(R.drawable.bg_pill_reminder_on);
                tvReminderBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.orange_reminder_text));
            } else {
                tvReminderBadge.setText("Không nhắc");
                tvReminderBadge.setBackgroundResource(R.drawable.bg_pill_reminder_off);
                tvReminderBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray_no_reminder_text));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(appointment);
                }
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
}
