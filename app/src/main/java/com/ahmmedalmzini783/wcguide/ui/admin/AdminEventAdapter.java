package com.ahmmedalmzini783.wcguide.ui.admin;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private List<Event> events;
    private OnEventActionListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnEventActionListener {
        void onEditEvent(Event event);
        void onDeleteEvent(Event event);
        void onViewEvent(Event event);
        void onToggleCountdown(Event event);
        void onToggleFeatured(Event event);
    }

    public AdminEventAdapter(List<Event> events, OnEventActionListener listener) {
        this.events = events;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // إنشاء تخطيط بسيط برمجياً
        LinearLayout itemLayout = new LinearLayout(parent.getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 16, 16, 16);
        itemLayout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT));
        
        return new EventViewHolder(itemLayout);
    }    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvType, tvDate, tvLocation, tvStatus, tvCountdown;
        private Button btnEdit, btnDelete, btnView, btnToggleCountdown, btnToggleFeatured;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // إنشاء المكونات برمجياً
            LinearLayout layout = (LinearLayout) itemView;
            
            tvTitle = new TextView(itemView.getContext());
            tvTitle.setTextSize(18);
            tvTitle.setTextColor(Color.BLACK);
            layout.addView(tvTitle);
            
            tvType = new TextView(itemView.getContext());
            tvType.setTextSize(14);
            tvType.setTextColor(Color.GRAY);
            layout.addView(tvType);
            
            tvDate = new TextView(itemView.getContext());
            tvDate.setTextSize(14);
            layout.addView(tvDate);
            
            tvLocation = new TextView(itemView.getContext());
            tvLocation.setTextSize(14);
            layout.addView(tvLocation);
            
            tvStatus = new TextView(itemView.getContext());
            tvStatus.setTextSize(14);
            layout.addView(tvStatus);
            
            tvCountdown = new TextView(itemView.getContext());
            tvCountdown.setTextSize(12);
            tvCountdown.setTextColor(Color.RED);
            layout.addView(tvCountdown);
            
            // أزرار الإجراءات
            LinearLayout buttonLayout = new LinearLayout(itemView.getContext());
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            
            btnEdit = new Button(itemView.getContext());
            btnEdit.setText("تعديل");
            btnEdit.setTextSize(12);
            buttonLayout.addView(btnEdit);
            
            btnDelete = new Button(itemView.getContext());
            btnDelete.setText("حذف");
            btnDelete.setTextSize(12);
            buttonLayout.addView(btnDelete);
            
            btnView = new Button(itemView.getContext());
            btnView.setText("عرض");
            btnView.setTextSize(12);
            buttonLayout.addView(btnView);
            
            btnToggleCountdown = new Button(itemView.getContext());
            btnToggleCountdown.setText("عداد");
            btnToggleCountdown.setTextSize(12);
            buttonLayout.addView(btnToggleCountdown);
            
            btnToggleFeatured = new Button(itemView.getContext());
            btnToggleFeatured.setText("مميز");
            btnToggleFeatured.setTextSize(12);
            buttonLayout.addView(btnToggleFeatured);
            
            layout.addView(buttonLayout);
        }

        public void bind(Event event) {
            // عرض العنوان
            if (event.isMatch()) {
                tvTitle.setText(event.getEventTitle());
            } else {
                tvTitle.setText(event.getTitle());
            }
            
            // عرض النوع
            String eventType = getEventTypeInArabic(event.getType());
            tvType.setText(eventType);
            
            // عرض التاريخ
            if (event.getDate() != null) {
                tvDate.setText(dateFormat.format(event.getDate()));
            } else {
                tvDate.setText("غير محدد");
            }
            
            // عرض الموقع
            if (!TextUtils.isEmpty(event.getLocation())) {
                tvLocation.setText(event.getLocation());
                tvLocation.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(View.GONE);
            }
            
            // عرض حالة الحدث
            String status = getEventStatus(event);
            tvStatus.setText(status);
            setStatusColor(tvStatus, status);
            
            // عرض العد التنازلي
            setupCountdownDisplay(event);
            
            // تحميل الصورة - سيتم إضافتها لاحقاً
            /*
            if (!TextUtils.isEmpty(event.getImageUrl())) {
                Glide.with(itemView.getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(ivEvent);
            } else {
                ivEvent.setImageResource(R.drawable.placeholder_event);
            }
            */
            
            // إعداد الأزرار
            setupButtons(event);
            
            // إعداد زر الفعالية المميزة
            setupFeaturedButton(event);
        }

        private String getEventTypeInArabic(String type) {
            switch (type) {
                case "celebration":
                    return "احتفال";
                case "match":
                    return "مباراة";
                case "general":
                    return "فعالية عامة";
                default:
                    return "غير محدد";
            }
        }

        private String getEventStatus(Event event) {
            if (event.getDate() == null) return "غير محدد";
            
            long currentTime = System.currentTimeMillis();
            long eventTime = event.getDate().getTime();
            
            if (eventTime < currentTime) {
                return "انتهت";
            } else if (event.shouldShowCountdown()) {
                return "قريباً";
            } else {
                return "مجدولة";
            }
        }

        private void setStatusColor(TextView textView, String status) {
            int colorRes;
            switch (status) {
                case "انتهت":
                    colorRes = Color.GRAY;
                    break;
                case "قريباً":
                    colorRes = Color.RED;
                    break;
                case "مجدولة":
                    colorRes = Color.BLUE;
                    break;
                default:
                    colorRes = Color.BLACK;
                    break;
            }
            textView.setTextColor(colorRes);
        }

        private void setupCountdownDisplay(Event event) {
            if (event.shouldShowCountdown() && event.isHasCountdown()) {
                Event.CountdownTime countdown = event.getCountdownTime();
                if (countdown != null) {
                    tvCountdown.setText("المتبقي: " + countdown.toArabicString());
                    tvCountdown.setVisibility(View.VISIBLE);
                } else {
                    tvCountdown.setVisibility(View.GONE);
                }
            } else {
                tvCountdown.setVisibility(View.GONE);
            }
        }

        private void setupButtons(Event event) {
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditEvent(event);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteEvent(event);
                }
            });
            
            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewEvent(event);
                }
            });
            
            // زر تفعيل/إلغاء العد التنازلي
            if (event.shouldShowCountdown()) {
                btnToggleCountdown.setVisibility(View.VISIBLE);
                btnToggleCountdown.setText(event.isHasCountdown() ? 
                    "إلغاء العد التنازلي" : "تفعيل العد التنازلي");
                btnToggleCountdown.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onToggleCountdown(event);
                    }
                });
            } else {
                btnToggleCountdown.setVisibility(View.GONE);
            }
        }
        
        private void setupFeaturedButton(Event event) {
            if (event.isFeatured()) {
                btnToggleFeatured.setText("إلغاء التمييز");
                btnToggleFeatured.setBackgroundColor(Color.parseColor("#FFD700")); // ذهبي
            } else {
                btnToggleFeatured.setText("جعلها مميزة");
                btnToggleFeatured.setBackgroundColor(Color.parseColor("#E0E0E0")); // رمادي
            }
            
            btnToggleFeatured.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleFeatured(event);
                }
            });
        }
    }
}