package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.RestaurantMenu;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuAdapter extends RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder> {

    private List<RestaurantMenu> menuItems = new ArrayList<>();
    private OnMenuItemClickListener onMenuItemClickListener;

    public interface OnMenuItemClickListener {
        void onMenuItemEdit(RestaurantMenu menuItem, int position);
        void onMenuItemDelete(RestaurantMenu menuItem, int position);
    }

    public RestaurantMenuAdapter() {
    }

    public RestaurantMenuAdapter(OnMenuItemClickListener listener) {
        this.onMenuItemClickListener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        RestaurantMenu menuItem = menuItems.get(position);
        holder.bind(menuItem, position);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void setMenuItems(List<RestaurantMenu> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<RestaurantMenu> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public void addMenuItem(RestaurantMenu menuItem) {
        menuItems.add(menuItem);
        notifyItemInserted(menuItems.size() - 1);
    }

    public void removeMenuItem(int position) {
        if (position >= 0 && position < menuItems.size()) {
            menuItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateMenuItem(int position, RestaurantMenu menuItem) {
        if (position >= 0 && position < menuItems.size()) {
            menuItems.set(position, menuItem);
            notifyItemChanged(position);
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView menuItemImage;
        private final TextView menuItemName;
        private final TextView menuItemDescription;
        private final TextView menuItemPrice;
        private final TextView menuItemCategory;
        private final TextView menuItemAvailability;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_menu_item);
            menuItemImage = itemView.findViewById(R.id.iv_menu_item_image);
            menuItemName = itemView.findViewById(R.id.tv_menu_item_name);
            menuItemDescription = itemView.findViewById(R.id.tv_menu_item_description);
            menuItemPrice = itemView.findViewById(R.id.tv_menu_item_price);
            menuItemCategory = itemView.findViewById(R.id.tv_menu_item_category);
            menuItemAvailability = itemView.findViewById(R.id.tv_menu_item_availability);
            btnEdit = itemView.findViewById(R.id.btn_edit_menu_item);
            btnDelete = itemView.findViewById(R.id.btn_delete_menu_item);
        }

        public void bind(RestaurantMenu menuItem, int position) {
            menuItemName.setText(menuItem.getItemName());
            menuItemDescription.setText(menuItem.getDescription());
            menuItemPrice.setText(String.format("%.2f ريال", menuItem.getPrice()));
            menuItemCategory.setText(menuItem.getCategory());

            // Set availability status
            if (menuItem.isAvailable()) {
                menuItemAvailability.setText("متوفر");
                menuItemAvailability.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                menuItemAvailability.setText("غير متوفر");
                menuItemAvailability.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }

            // Load menu item image
            if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(menuItem.getImageUrl())
                        .placeholder(R.drawable.ic_food_placeholder)
                        .error(R.drawable.ic_food_placeholder)
                        .into(menuItemImage);
            } else {
                menuItemImage.setImageResource(R.drawable.ic_food_placeholder);
            }

            // Set special item indicator
            if (menuItem.isSpecial()) {
                                        cardView.setStrokeColor(itemView.getContext().getColor(R.color.secondary));
                cardView.setStrokeWidth(4);
            } else {
                cardView.setStrokeWidth(0);
            }

            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onMenuItemEdit(menuItem, position);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onMenuItemDelete(menuItem, position);
                }
            });
        }
    }
}
