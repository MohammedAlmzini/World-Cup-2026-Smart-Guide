package com.ahmmedalmzini783.wcguide.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events != null ? events : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_compact, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents != null ? newEvents : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final ImageView eventImage;
        private final TextView eventTitle;
        private final TextView eventDate;
        private final TextView eventVenue;
        private final TextView eventType;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDate = itemView.findViewById(R.id.event_date);
            eventVenue = itemView.findViewById(R.id.event_venue);
            eventType = itemView.findViewById(R.id.event_type);
        }

        public void bind(Event event, OnEventClickListener listener) {
            eventTitle.setText(event.getTitle());
            eventDate.setText(event.getDate() != null ? event.getDate().toString() : "");
            eventVenue.setText(event.getLocation());
            eventType.setText(event.getType());

            // تحميل الصورة
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(event.getImageUrl())
                        .placeholder(R.drawable.placeholder_event)
                        .error(R.drawable.placeholder_event)
                        .into(eventImage);
            } else {
                eventImage.setImageResource(R.drawable.placeholder_event);
            }

            // إضافة click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
}