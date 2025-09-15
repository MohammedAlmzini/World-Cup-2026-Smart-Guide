package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Review;
import com.ahmmedalmzini783.wcguide.data.repository.ReviewRepository;
import com.ahmmedalmzini783.wcguide.util.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviews;
    private ReviewRepository reviewRepository;
    private OnReviewActionListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnReviewActionListener {
        void onReviewDeleted();
        void onReviewUpdated();
    }

    public ReviewAdapter(Context context, OnReviewActionListener listener) {
        this.context = context;
        this.reviews = new ArrayList<>();
        this.reviewRepository = ReviewRepository.getInstance();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews.clear();
        if (newReviews != null) {
            this.reviews.addAll(newReviews);
        }
        notifyDataSetChanged();
    }

    public void addReview(Review review) {
        this.reviews.add(0, review);
        notifyItemInserted(0);
    }

    public void removeReview(int position) {
        if (position >= 0 && position < reviews.size()) {
            this.reviews.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reviews.size());
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView textReviewerName;
        private TextView textReviewTitle;
        private TextView textReviewDescription;
        private TextView textLandmarkName;
        private TextView textApprovalStatus;
        private TextView textDate;
        private RatingBar ratingBar;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            textReviewerName = itemView.findViewById(R.id.text_reviewer_name);
            textReviewTitle = itemView.findViewById(R.id.text_review_title);
            textReviewDescription = itemView.findViewById(R.id.text_review_description);
            textLandmarkName = itemView.findViewById(R.id.text_landmark_name);
            textApprovalStatus = itemView.findViewById(R.id.text_approval_status);
            textDate = itemView.findViewById(R.id.text_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Review review) {
            // Set reviewer name
            textReviewerName.setText(review.getReviewerName());

            // Set review title
            textReviewTitle.setText(review.getTitle());

            // Set review description
            textReviewDescription.setText(review.getDescription());

            // Set landmark name (you may need to fetch this from another source)
            textLandmarkName.setText("معلم رقم: " + review.getLandmarkId());

            // Set rating
            ratingBar.setRating(review.getRating());

            // Set approval status
            if (review.isApproved()) {
                textApprovalStatus.setText("معتمد");
                textApprovalStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                textApprovalStatus.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_play, 0, 0, 0
                );
            } else {
                textApprovalStatus.setText("في الانتظار");
                textApprovalStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                textApprovalStatus.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_pause, 0, 0, 0
                );
            }

            // Set date
            if (review.getTimestamp() > 0) {
                Date date = new Date(review.getTimestamp());
                textDate.setText(dateFormat.format(date));
            } else {
                textDate.setText("غير محدد");
            }

            // Set click listeners
            itemView.setOnClickListener(v -> openReviewDetails(review));
            btnEdit.setOnClickListener(v -> editReview(review));
            btnDelete.setOnClickListener(v -> showDeleteDialog(review, getAdapterPosition()));
        }

        private void openReviewDetails(Review review) {
            Intent intent = new Intent(context, AddReviewActivity.class);
            intent.putExtra("review", review);
            intent.putExtra("mode", "view");
            context.startActivity(intent);
        }

        private void editReview(Review review) {
            Intent intent = new Intent(context, AddReviewActivity.class);
            intent.putExtra("review", review);
            intent.putExtra("mode", "edit");
            context.startActivity(intent);
        }

        private void showDeleteDialog(Review review, int position) {
            new AlertDialog.Builder(context)
                .setTitle("حذف التقييم")
                .setMessage("هل أنت متأكد من رغبتك في حذف هذا التقييم؟")
                .setPositiveButton("حذف", (dialog, which) -> deleteReview(review, position))
                .setNegativeButton("إلغاء", null)
                .show();
        }

        private void deleteReview(Review review, int position) {
            LiveData<Resource<Void>> resourceLiveData = reviewRepository.deleteReview(review.getId());
            
            resourceLiveData.observeForever(resource -> {
                if (resource != null) {
                    switch (resource.getStatus()) {
                        case SUCCESS:
                            removeReview(position);
                            if (listener != null) {
                                listener.onReviewDeleted();
                            }
                            break;
                        case ERROR:
                            new AlertDialog.Builder(context)
                                .setTitle("خطأ")
                                .setMessage("فشل في حذف التقييم: " + resource.getMessage())
                                .setPositiveButton("موافق", null)
                                .show();
                            break;
                        case LOADING:
                            // Show loading if needed
                            break;
                    }
                }
            });
        }
    }
}
