package com.ahmmedalmzini783.wcguide.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ItemAdminBannerBinding;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;

import java.util.List;

public class AdminBannerAdapter extends ListAdapter<Banner, AdminBannerAdapter.AdminBannerViewHolder> {

    private final OnBannerEditListener editListener;
    private final OnBannerDeleteListener deleteListener;

    public interface OnBannerEditListener {
        void onBannerEdit(Banner banner);
    }

    public interface OnBannerDeleteListener {
        void onBannerDelete(Banner banner);
    }

    public AdminBannerAdapter(OnBannerEditListener editListener, OnBannerDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
        this.editListener = editListener;
        this.deleteListener = deleteListener;
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
    public AdminBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminBannerBinding binding = ItemAdminBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminBannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBannerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setBanners(List<Banner> banners) {
        submitList(banners);
    }

    class AdminBannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminBannerBinding binding;

        AdminBannerViewHolder(ItemAdminBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Banner banner) {
            binding.bannerTitle.setText(banner.getTitle());
            binding.bannerDescription.setText(banner.getDescription() != null ? 
                banner.getDescription() : binding.getRoot().getContext().getString(R.string.no_description_available));

            // Load banner image
            ImageLoader.loadImageWithCacheBusting(
                binding.bannerImage.getContext(),
                banner.getImageUrl(),
                binding.bannerImage,
                R.drawable.placeholder_banner
            );

            // Set up edit button
            binding.btnEdit.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onBannerEdit(banner);
                }
            });

            // Set up delete button
            binding.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onBannerDelete(banner);
                }
            });
        }
    }
}
