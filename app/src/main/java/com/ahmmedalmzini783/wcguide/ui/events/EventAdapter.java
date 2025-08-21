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

public class EventAdapter extends ListAdapter<Event, EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Event event, boolean isFavorite);
    }

    private final OnEventClickListener clickListener;
    private final OnFavoriteClickListener favoriteListener;

    public EventAdapter(OnEventClickListener clickListener, OnFavoriteClickListener favoriteListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
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
        private boolean isFavorite = false;

        EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.eventTitle.setText(event.getTitle());
            
            // Set venue text
            String venueText = buildVenueText(event);
            binding.eventVenue.setText(venueText);
            
            // Set date text
            String dateText = DateTimeUtil.formatDateTime(event.getStartUtc(), 
                    binding.getRoot().getContext().getResources().getConfiguration().getLocales().get(0));
            binding.eventDate.setText(dateText);
            
            // Set event type chip
            binding.eventType.setText(event.getType());
            
            // Set event status
            binding.eventStatus.setText(getEventStatus(event));
            
            // Load event image
            Glide.with(binding.eventImage.getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.ic_events)
                    .centerCrop()
                    .into(binding.eventImage);

            // Set click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onEventClick(event);
                }
            });

            binding.favoriteButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                updateFavoriteIcon();
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(event, isFavorite);
                }
            });
            
            updateFavoriteIcon();
        }

        private String buildVenueText(Event event) {
            String venueName = event.getVenueName();
            String city = event.getCity();
            String country = event.getCountry();
            
            if (venueName != null && !venueName.isEmpty()) {
                if (city != null && !city.isEmpty()) {
                    return venueName + ", " + city;
                }
                return venueName;
            }
            
            if (city != null && !city.isEmpty() && country != null && !country.isEmpty()) {
                return city + ", " + country;
            }
            
            if (city != null && !city.isEmpty()) {
                return city;
            }
            
            return country != null ? country : "";
        }
        
        private String getEventStatus(Event event) {
            long currentTime = System.currentTimeMillis();
            long startTime = event.getStartUtc();
            long endTime = event.getEndUtc();
            
            if (currentTime < startTime) {
                return "Upcoming";
            } else if (currentTime >= startTime && currentTime <= endTime) {
                return "Live";
            } else {
                return "Finished";
            }
        }
        
        private void updateFavoriteIcon() {
            binding.favoriteButton.setImageResource(
                    isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        }
    }
}