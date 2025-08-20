package com.ahmmedalmzini783.wcguide.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ItemBannerBinding;
import com.bumptech.glide.Glide;

public class BannerAdapter extends ListAdapter<Banner, BannerAdapter.BannerViewHolder> {

    private final OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }

    public BannerAdapter(OnBannerClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Banner> DIFF_CALLBACK = new DiffUtil.ItemCallback<Banner>() {
        @Override
        public boolean areItemsTheSame(@NonNull Banner oldItem, @NonNull Banner newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Banner oldItem, @NonNull Banner newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemBannerBinding binding;

        BannerViewHolder(ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Banner banner) {
            binding.bannerTitle.setText(banner.getTitle());

            Glide.with(binding.bannerImage.getContext())
                    .load(banner.getImageUrl())
                    .centerCrop()
                    .into(binding.bannerImage);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBannerClick(banner);
                }
            });
        }
    }
}