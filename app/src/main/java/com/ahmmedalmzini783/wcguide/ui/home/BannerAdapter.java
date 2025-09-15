package com.ahmmedalmzini783.wcguide.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.databinding.ItemBannerBinding;
import com.ahmmedalmzini783.wcguide.util.ImageLoader;
import com.ahmmedalmzini783.wcguide.R;

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
            setupCardAnimation();
        }

        private void setupCardAnimation() {
            // Add modern card press animation with ripple effect
            binding.getRoot().setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        // Scale down with shadow reduction
                        v.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .translationZ(-4f)
                            .setDuration(150)
                            .setInterpolator(new android.view.animation.AccelerateInterpolator())
                            .start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        // Scale back with shadow restoration
                        v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .translationZ(0f)
                            .setDuration(200)
                            .setInterpolator(new android.view.animation.DecelerateInterpolator())
                            .start();
                        break;
                }
                return false;
            });
        }

        void bind(Banner banner) {
            // Set title with animation
            binding.bannerTitle.setText(banner.getTitle());

            // Load image with modern loading transition
            binding.bannerImage.setAlpha(0.3f);
            String url = banner.getImageUrl();
            if (url != null && !url.trim().isEmpty()) {
                // Use normal caching first to avoid flicker and keep original image intact
                ImageLoader.loadImage(
                        binding.bannerImage.getContext(),
                        url,
                        binding.bannerImage,
                        R.drawable.placeholder_banner
                );
            } else {
                binding.bannerImage.setImageResource(R.drawable.placeholder_banner);
            }
            
            // Fade in image when loaded
            binding.bannerImage.animate()
                .alpha(1.0f)
                .setDuration(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

            // Set click listener with modern feedback
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    // Modern click animation with haptic feedback
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                    
                    // Elegant click animation
                    v.animate()
                        .scaleX(1.02f)
                        .scaleY(1.02f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(200)
                                .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                                .withEndAction(() -> listener.onBannerClick(banner))
                                .start();
                        })
                        .start();
                }
            });

            // Add entrance animation with stagger
            binding.getRoot().setAlpha(0f);
            binding.getRoot().setTranslationX(100f);
            binding.getRoot().animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(500)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .setStartDelay(getAdapterPosition() * 100L) // Stagger animation
                .start();
        }
    }
}