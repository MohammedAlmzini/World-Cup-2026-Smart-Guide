package com.ahmmedalmzini783.wcguide.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Utility class for image loading with cache management
 */
public class ImageLoader {
    
    /**
     * Load image with default caching (for static images)
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeholder) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .into(imageView);
    }
    
    /**
     * Load image without caching (for frequently updated images like banners)
     */
    public static void loadImageNoCache(Context context, String imageUrl, ImageView imageView, int placeholder) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(imageView);
    }
    
    /**
     * Load image with timestamp-based cache busting
     */
    public static void loadImageWithCacheBusting(Context context, String imageUrl, ImageView imageView, int placeholder) {
        String cacheBustedUrl = imageUrl;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Preserve existing Firebase or signed URL query parameters.
            // If URL already contains '?', append with '&', otherwise start with '?'.
            char joinChar = imageUrl.contains("?") ? '&' : '?';
            cacheBustedUrl = imageUrl + joinChar + "t=" + System.currentTimeMillis();
        }
        
        Glide.with(context)
                .load(cacheBustedUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .skipMemoryCache(true)
                // Allow disk caching of transformed data while still forcing fresh fetch via unique URL
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(imageView);
    }
    
    /**
     * Clear all image caches
     */
    public static void clearImageCache(Context context) {
        // Clear memory cache
        Glide.get(context).clearMemory();
        
        // Clear disk cache in background thread
        new Thread(() -> {
            Glide.get(context).clearDiskCache();
        }).start();
    }
    
    /**
     * Load image with signature for cache invalidation
     */
    public static void loadImageWithSignature(Context context, String imageUrl, ImageView imageView, 
                                            int placeholder, String signature) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .signature(new com.bumptech.glide.signature.ObjectKey(signature));
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Load Firebase Storage image with proper error handling
     */
    public static void loadFirebaseImage(Context context, String imageUrl, ImageView imageView, int placeholder) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(placeholder);
            return;
        }
        
        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Load circular image (for profile pictures)
     */
    public static void loadCircularImage(Context context, String imageUrl, ImageView imageView, int placeholder) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(placeholder);
            return;
        }
        
        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
