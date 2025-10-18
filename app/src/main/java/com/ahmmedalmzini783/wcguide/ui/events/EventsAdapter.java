package com.ahmmedalmzini783.wcguide.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<EventItem> eventsList;
    private OnEventItemClickListener listener;

    public interface OnEventItemClickListener {
        void onEventItemClick(EventItem eventItem);
    }

    public EventsAdapter(List<EventItem> eventsList, OnEventItemClickListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventItem eventItem = eventsList.get(position);
        holder.bind(eventItem);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView eventItemImage;
        private TextView eventItemTitle, eventItemDescription;
        private TextView eventItemDate, eventItemLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventItemImage = itemView.findViewById(R.id.event_item_image);
            eventItemTitle = itemView.findViewById(R.id.event_item_title);
            eventItemDescription = itemView.findViewById(R.id.event_item_description);
            eventItemDate = itemView.findViewById(R.id.event_item_date);
            eventItemLocation = itemView.findViewById(R.id.event_item_location);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventItemClick(eventsList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(EventItem eventItem) {
            eventItemTitle.setText(eventItem.getTitle());
            eventItemDescription.setText(eventItem.getDescription());
            eventItemLocation.setText(eventItem.getLocation());

            // Format date
            if (eventItem.getEventDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                eventItemDate.setText(sdf.format(eventItem.getEventDate()));
            }

            // Load image
            if (eventItem.getImageUrl() != null && !eventItem.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(eventItem.getImageUrl())
                    .placeholder(R.drawable.ic_events)
                    .error(R.drawable.ic_events)
                    .into(eventItemImage);
            }
        }
    }
}
