package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.ui.events.EventItem;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.AdminEventViewHolder> {

    private List<EventItem> eventsList;
    private OnEventItemClickListener listener;
    private OnEditEventClickListener editListener;
    private OnDeleteEventClickListener deleteListener;

    public interface OnEventItemClickListener {
        void onEventItemClick(EventItem eventItem);
    }

    public interface OnEditEventClickListener {
        void onEditEventClick(EventItem eventItem);
    }

    public interface OnDeleteEventClickListener {
        void onDeleteEventClick(EventItem eventItem);
    }

    public AdminEventsAdapter(List<EventItem> eventsList, OnEventItemClickListener listener, 
                             OnEditEventClickListener editListener, OnDeleteEventClickListener deleteListener) {
        this.eventsList = eventsList;
        this.listener = listener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new AdminEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {
        EventItem eventItem = eventsList.get(position);
        holder.bind(eventItem);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    class AdminEventViewHolder extends RecyclerView.ViewHolder {
        private ImageView eventItemImage;
        private TextView eventItemTitle, eventItemDescription;
        private TextView eventItemDate, eventItemLocation;
        private LinearLayout adminActionsLayout;
        private ImageView btnEditEvent, btnDeleteEvent;

        public AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventItemImage = itemView.findViewById(R.id.event_item_image);
            eventItemTitle = itemView.findViewById(R.id.event_item_title);
            eventItemDescription = itemView.findViewById(R.id.event_item_description);
            eventItemDate = itemView.findViewById(R.id.event_item_date);
            eventItemLocation = itemView.findViewById(R.id.event_item_location);
            adminActionsLayout = itemView.findViewById(R.id.admin_actions_layout);
            btnEditEvent = itemView.findViewById(R.id.btn_edit_event);
            btnDeleteEvent = itemView.findViewById(R.id.btn_delete_event);

            // Show admin actions
            adminActionsLayout.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventItemClick(eventsList.get(getAdapterPosition()));
                }
            });

            btnEditEvent.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditEventClick(eventsList.get(getAdapterPosition()));
                }
            });

            btnDeleteEvent.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteEventClick(eventsList.get(getAdapterPosition()));
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
