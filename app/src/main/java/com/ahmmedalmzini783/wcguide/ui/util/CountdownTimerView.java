package com.ahmmedalmzini783.wcguide.ui.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;

import java.util.Locale;

/**
 * مكون عرض العد التنازلي للفعاليات
 */
public class CountdownTimerView extends LinearLayout {

    private TextView tvDays, tvHours, tvMinutes, tvSeconds;
    private Handler handler;
    private Runnable countdownRunnable;
    private Event event;
    private OnCountdownFinishedListener onFinishedListener;

    public interface OnCountdownFinishedListener {
        void onCountdownFinished();
    }

    public CountdownTimerView(Context context) {
        super(context);
        init(context);
    }

    public CountdownTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountdownTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // إنشاء تخطيط بسيط برمجياً
        setOrientation(LinearLayout.HORIZONTAL);
        setPadding(16, 8, 16, 8);
        
        // إنشاء TextView للعد التنازلي
        TextView countdownText = new TextView(context);
        countdownText.setTextSize(16);
        countdownText.setTextColor(0xFF4CAF50); // أخضر
        countdownText.setPadding(8, 4, 8, 4);
        addView(countdownText);
        
        // حفظ المرجع
        tvDays = countdownText;
        tvHours = countdownText;
        tvMinutes = countdownText;
        tvSeconds = countdownText;
        
        handler = new Handler();
    }

    /**
     * بدء العد التنازلي للفعالية
     */
    public void startCountdown(Event event) {
        this.event = event;
        
        if (event == null || !event.shouldShowCountdown()) {
            setVisibility(GONE);
            return;
        }
        
        setVisibility(VISIBLE);
        startCountdownTimer();
    }

    /**
     * إيقاف العد التنازلي
     */
    public void stopCountdown() {
        if (handler != null && countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
    }

    /**
     * تعيين مستمع انتهاء العد التنازلي
     */
    public void setOnCountdownFinishedListener(OnCountdownFinishedListener listener) {
        this.onFinishedListener = listener;
    }

    private void startCountdownTimer() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdown();
                handler.postDelayed(this, 1000); // تحديث كل ثانية
            }
        };
        
        handler.post(countdownRunnable);
    }

    private void updateCountdown() {
        if (event == null) return;
        
        Event.CountdownTime countdown = event.getCountdownTime();
        
        if (countdown != null) {
            // تحديث النص الواحد
            String countdownText = String.format(Locale.getDefault(),
                "%d يوم، %02d:%02d:%02d",
                countdown.getDays(),
                countdown.getHours(),
                countdown.getMinutes(),
                countdown.getSeconds());
            
            tvDays.setText("⏰ " + countdownText);
        } else {
            // انتهى العد التنازلي
            stopCountdown();
            setVisibility(GONE);
            
            if (onFinishedListener != null) {
                onFinishedListener.onCountdownFinished();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCountdown();
    }
}