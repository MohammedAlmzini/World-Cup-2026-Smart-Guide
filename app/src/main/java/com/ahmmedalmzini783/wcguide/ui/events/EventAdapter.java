package com.ahmmedalmzini783.wcguide.ui.events;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.ahmmedalmzini783.wcguide.databinding.ItemEventBinding;
import com.ahmmedalmzini783.wcguide.util.DateTimeUtil;
import com.bumptech.glide.Glide;

import java.util.Locale;

public class EventAdapter extends ListAdapter<Event, EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(Event event, boolean isFavorite);
    }

    private final OnEventClickListener onEventClickListener;
    private final OnFavoriteToggleListener onFavoriteToggleListener;

    public EventAdapter(OnEventClickListener onEventClickListener,
                        OnFavoriteToggleListener onFavoriteToggleListener) {
        super(DIFF_CALLBACK);
        this.onEventClickListener = onEventClickListener;
        this.onFavoriteToggleListener = onFavoriteToggleListener;
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventBinding binding;
        private boolean isFavorite;

        EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.eventTitle.setText(event.getTitle());

            String venueText;
            if (event.getVenueName() != null && !event.getVenueName().isEmpty()) {
                if (event.getCity() != null && !event.getCity().isEmpty()) {
                    venueText = event.getVenueName() + ", " + event.getCity();
                } else {
                    venueText = event.getVenueName();
                }
            } else {
                venueText = event.getCity() != null ? event.getCity() : "";
            }
            binding.eventVenue.setText(venueText);

            String dateText = DateTimeUtil.formatDateTime(
                    DateTimeUtil.convertUtcToLocal(event.getStartUtc()),
                    Locale.getDefault());
            binding.eventDate.setText(dateText);

            binding.eventType.setText(event.getType() != null ? event.getType() : "");

            String statusText = DateTimeUtil.getEventStatus(
                    binding.getRoot().getContext(), event.getStartUtc(), event.getEndUtc());
            binding.eventStatus.setText(statusText);

            Glide.with(binding.eventImage.getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.ic_events)
                    .centerCrop()
                    .into(binding.eventImage);

            binding.getRoot().setOnClickListener(v -> {
                if (onEventClickListener != null) {
                    onEventClickListener.onEventClick(event);
                }
            });

            binding.favoriteButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                binding.favoriteButton.setImageResource(
                        isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                if (onFavoriteToggleListener != null) {
                    onFavoriteToggleListener.onFavoriteToggle(event, isFavorite);
                }
            });
        }
    }
}

