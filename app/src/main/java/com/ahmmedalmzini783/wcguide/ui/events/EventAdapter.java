package com.ahmmedalmzini783.wcguide.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.bumptech.glide.Glide;

public class EventAdapter extends ListAdapter<Event, EventAdapter.EventViewHolder> {

    private final OnEventClickListener onEventClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Event event, boolean isFavorite);
    }

    public EventAdapter(OnEventClickListener onEventClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        super(new DiffUtil.ItemCallback<Event>() {
            @Override
            public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onEventClickListener = onEventClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
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
        Event event = getItem(position);
        holder.bind(event);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ImageView eventImage;
        private final TextView eventTitle;
        private final TextView eventDate;
        private final TextView eventVenue;
        private final TextView eventType;
        private final ImageView favoriteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDate = itemView.findViewById(R.id.event_date);
            eventVenue = itemView.findViewById(R.id.event_venue);
            eventType = itemView.findViewById(R.id.event_type);
            favoriteButton = itemView.findViewById(R.id.favorite_button);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onEventClickListener != null) {
                    onEventClickListener.onEventClick(getItem(position));
                }
            });

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onFavoriteClickListener != null) {
                    Event event = getItem(position);
                    onFavoriteClickListener.onFavoriteClick(event, !event.isFavorite());
                }
            });
        }

        public void bind(Event event) {
            eventTitle.setText(event.getTitle());
            eventDate.setText(event.getFormattedDate());
            eventVenue.setText(event.getVenueName() != null ? event.getVenueName() : event.getLocation());
            eventType.setText(event.getType());

            // Load event image
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(event.getImageUrl())
                        .placeholder(R.drawable.placeholder_event)
                        .error(R.drawable.placeholder_event)
                        .into(eventImage);
            }

            // Update favorite button
            favoriteButton.setImageResource(event.isFavorite() ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }
    }
}