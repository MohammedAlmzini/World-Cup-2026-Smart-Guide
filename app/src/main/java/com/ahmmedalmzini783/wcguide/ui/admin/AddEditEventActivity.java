package com.ahmmedalmzini783.wcguide.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditEventActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT = "extra_event";

    private EditText etTitle, etDescription, etLocation, etVenueName, etTicketUrl;
    private EditText etCapacity, etLat, etLng, etImageUrl;
    private Spinner spinnerType;
    private Button btnSelectDate, btnSave, btnPreviewImage;
    private ImageView ivSelectedImage;
    private LinearLayout layoutCelebrationFields, layoutMatchFields;
    private CheckBox cbFeatured;
    
    // حقول الاحتفالات
    private EditText etCelebrationType, etDuration, etActivities, etPerformers;
    
    // حقول المباريات
    private EditText etHomeTeam, etAwayTeam, etReferee, etMatchType, etGroup;
    private EditText etHomeTeamFlag, etAwayTeamFlag, etStadiumCapacity;

    private Event currentEvent;
    private DatabaseReference eventsRef;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        createUI();
        setupSpinner();
        setupFirebase();
        setupDateTimePicker();
        
        // التحقق من وجود حدث للتحرير
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_EVENT)) {
            currentEvent = (Event) intent.getSerializableExtra(EXTRA_EVENT);
            if (currentEvent != null) {
                populateFields();
                setTitle("تحرير الفعالية");
            }
        } else {
            setTitle("إضافة فعالية جديدة");
        }

        setupClickListeners();
    }

    private void createUI() {
        // إنشاء التخطيط الرئيسي
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);
        
        // العنوان
        etTitle = new EditText(this);
        etTitle.setHint("عنوان الفعالية");
        mainLayout.addView(etTitle);
        
        // الوصف
        etDescription = new EditText(this);
        etDescription.setHint("وصف الفعالية");
        etDescription.setLines(3);
        mainLayout.addView(etDescription);
        
        // الموقع
        etLocation = new EditText(this);
        etLocation.setHint("الموقع");
        mainLayout.addView(etLocation);
        
        // الفعالية المميزة
        cbFeatured = new CheckBox(this);
        cbFeatured.setText("فعالية مميزة");
        mainLayout.addView(cbFeatured);
        
        // نوع الفعالية
        spinnerType = new Spinner(this);
        mainLayout.addView(spinnerType);
        
        // تاريخ الفعالية
        btnSelectDate = new Button(this);
        btnSelectDate.setText("اختر التاريخ");
        mainLayout.addView(btnSelectDate);
        
        // اسم المكان
        etVenueName = new EditText(this);
        etVenueName.setHint("اسم المكان");
        mainLayout.addView(etVenueName);
        
        // رابط التذاكر
        etTicketUrl = new EditText(this);
        etTicketUrl.setHint("رابط التذاكر");
        mainLayout.addView(etTicketUrl);
        
        // السعة
        etCapacity = new EditText(this);
        etCapacity.setHint("السعة");
        etCapacity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mainLayout.addView(etCapacity);
        
        // عنوان الإحداثيات
        TextView coordinatesTitle = new TextView(this);
        coordinatesTitle.setText("إحداثيات الموقع");
        coordinatesTitle.setTextSize(16);
        coordinatesTitle.setTextColor(getResources().getColor(android.R.color.black));
        coordinatesTitle.setPadding(0, 20, 0, 10);
        mainLayout.addView(coordinatesTitle);
        
        // الإحداثيات
        etLat = new EditText(this);
        etLat.setHint("خط العرض");
        etLat.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mainLayout.addView(etLat);
        
        etLng = new EditText(this);
        etLng.setHint("خط الطول");
        etLng.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mainLayout.addView(etLng);
        
        // أزرار الإحداثيات
        LinearLayout coordinatesButtonsLayout = new LinearLayout(this);
        coordinatesButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        coordinatesButtonsLayout.setPadding(0, 10, 0, 0);
        
        Button btnGetCurrentLocation = new Button(this);
        btnGetCurrentLocation.setText("الموقع الحالي");
        btnGetCurrentLocation.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        coordinatesButtonsLayout.addView(btnGetCurrentLocation);
        
        Button btnSelectOnMap = new Button(this);
        btnSelectOnMap.setText("اختيار على الخريطة");
        btnSelectOnMap.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        coordinatesButtonsLayout.addView(btnSelectOnMap);
        
        mainLayout.addView(coordinatesButtonsLayout);
        
        // رابط الصورة
        etImageUrl = new EditText(this);
        etImageUrl.setHint("رابط الصورة (URL)");
        etImageUrl.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        mainLayout.addView(etImageUrl);
        
        // زر معاينة الصورة
        btnPreviewImage = new Button(this);
        btnPreviewImage.setText("👁️ معاينة الصورة");
        btnPreviewImage.setBackgroundColor(0xFF2196F3); // لون أزرق
        btnPreviewImage.setTextColor(0xFFFFFFFF); // نص أبيض
        mainLayout.addView(btnPreviewImage);
        
        // صورة الفعالية
        ivSelectedImage = new ImageView(this);
        ivSelectedImage.setLayoutParams(new LinearLayout.LayoutParams(300, 200));
        ivSelectedImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivSelectedImage.setVisibility(View.GONE); // مخفية في البداية
        mainLayout.addView(ivSelectedImage);
        
        // تخطيط حقول الاحتفال
        layoutCelebrationFields = new LinearLayout(this);
        layoutCelebrationFields.setOrientation(LinearLayout.VERTICAL);
        layoutCelebrationFields.setVisibility(View.GONE);
        
        etCelebrationType = new EditText(this);
        etCelebrationType.setHint("نوع الاحتفال");
        layoutCelebrationFields.addView(etCelebrationType);
        
        etDuration = new EditText(this);
        etDuration.setHint("مدة الاحتفال");
        layoutCelebrationFields.addView(etDuration);
        
        etActivities = new EditText(this);
        etActivities.setHint("الأنشطة");
        layoutCelebrationFields.addView(etActivities);
        
        etPerformers = new EditText(this);
        etPerformers.setHint("المؤدون");
        layoutCelebrationFields.addView(etPerformers);
        
        mainLayout.addView(layoutCelebrationFields);
        
        // تخطيط حقول المباراة
        layoutMatchFields = new LinearLayout(this);
        layoutMatchFields.setOrientation(LinearLayout.VERTICAL);
        layoutMatchFields.setVisibility(View.GONE);
        
        etHomeTeam = new EditText(this);
        etHomeTeam.setHint("الفريق المضيف");
        layoutMatchFields.addView(etHomeTeam);
        
        etAwayTeam = new EditText(this);
        etAwayTeam.setHint("الفريق الضيف");
        layoutMatchFields.addView(etAwayTeam);
        
        etReferee = new EditText(this);
        etReferee.setHint("الحكم");
        layoutMatchFields.addView(etReferee);
        
        etMatchType = new EditText(this);
        etMatchType.setHint("نوع المباراة");
        layoutMatchFields.addView(etMatchType);
        
        etGroup = new EditText(this);
        etGroup.setHint("المجموعة");
        layoutMatchFields.addView(etGroup);
        
        etHomeTeamFlag = new EditText(this);
        etHomeTeamFlag.setHint("علم الفريق المضيف");
        layoutMatchFields.addView(etHomeTeamFlag);
        
        etAwayTeamFlag = new EditText(this);
        etAwayTeamFlag.setHint("علم الفريق الضيف");
        layoutMatchFields.addView(etAwayTeamFlag);
        
        etStadiumCapacity = new EditText(this);
        etStadiumCapacity.setHint("سعة الملعب");
        etStadiumCapacity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layoutMatchFields.addView(etStadiumCapacity);
        
        mainLayout.addView(layoutMatchFields);
        
        // زر الحفظ
        btnSave = new Button(this);
        btnSave.setText("حفظ");
        mainLayout.addView(btnSave);
        
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }

    private void setupSpinner() {
        List<String> eventTypes = new ArrayList<>();
        eventTypes.add("اختر نوع الفعالية");
        eventTypes.add("celebration");
        eventTypes.add("match");
        eventTypes.add("general");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                toggleFieldsVisibility(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void toggleFieldsVisibility(String eventType) {
        layoutCelebrationFields.setVisibility(View.GONE);
        layoutMatchFields.setVisibility(View.GONE);
        
        if ("celebration".equals(eventType)) {
            layoutCelebrationFields.setVisibility(View.VISIBLE);
        } else if ("match".equals(eventType)) {
            layoutMatchFields.setVisibility(View.VISIBLE);
        }
    }

    private void setupFirebase() {
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        selectedDateTime = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    private void setupDateTimePicker() {
        btnSelectDate.setOnClickListener(v -> showDateTimePicker());
    }

    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                showTimePicker();
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                
                btnSelectDate.setText(dateTimeFormat.format(selectedDateTime.getTime()));
            },
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            true
        );
        
        timePickerDialog.show();
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDateTimePicker());
        btnPreviewImage.setOnClickListener(v -> previewImage());
        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void previewImage() {
        String imageUrl = etImageUrl.getText().toString().trim();
        if (TextUtils.isEmpty(imageUrl)) {
            etImageUrl.setError("يرجى إدخال رابط الصورة");
            return;
        }
        
        // التحقق من صحة الرابط
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            etImageUrl.setError("يرجى إدخال رابط صحيح يبدأ بـ http:// أو https://");
            return;
        }
        
        // تحميل الصورة للمعاينة
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_event)
            .error(R.drawable.placeholder_event)
            .into(ivSelectedImage);
        
        ivSelectedImage.setVisibility(View.VISIBLE);
        Toast.makeText(this, "تم تحميل الصورة للمعاينة", Toast.LENGTH_SHORT).show();
    }





    private void populateFields() {
        if (currentEvent == null) return;
        
        etTitle.setText(currentEvent.getTitle());
        etDescription.setText(currentEvent.getDescription());
        etLocation.setText(currentEvent.getLocation());
        etVenueName.setText(currentEvent.getVenueName());
        etTicketUrl.setText(currentEvent.getTicketUrl());
        etCapacity.setText(String.valueOf(currentEvent.getCapacity()));
        etLat.setText(String.valueOf(currentEvent.getLat()));
        etLng.setText(String.valueOf(currentEvent.getLng()));
        cbFeatured.setChecked(currentEvent.isFeatured());
        
        // تحديد نوع الفعالية
        String eventType = currentEvent.getType();
        if (eventType != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerType.getAdapter();
            int position = adapter.getPosition(eventType);
            if (position >= 0) {
                spinnerType.setSelection(position);
            }
        }
        
        // تعبئة التاريخ
        if (currentEvent.getDate() != null) {
            selectedDateTime.setTime(currentEvent.getDate());
            btnSelectDate.setText(dateTimeFormat.format(currentEvent.getDate()));
        }
        
        // تعبئة حقول الاحتفالات
        if ("celebration".equals(eventType)) {
            etCelebrationType.setText(currentEvent.getCelebrationType());
            etDuration.setText(currentEvent.getDuration());
            if (currentEvent.getActivities() != null) {
                etActivities.setText(String.join(", ", currentEvent.getActivities()));
            }
            if (currentEvent.getPerformers() != null) {
                etPerformers.setText(String.join(", ", currentEvent.getPerformers()));
            }
        }
        
        // تعبئة حقول المباريات
        if ("match".equals(eventType)) {
            etHomeTeam.setText(currentEvent.getHomeTeam());
            etAwayTeam.setText(currentEvent.getAwayTeam());
            etReferee.setText(currentEvent.getReferee());
            etMatchType.setText(currentEvent.getMatchType());
            etGroup.setText(currentEvent.getGroup());
            etHomeTeamFlag.setText(currentEvent.getHomeTeamFlag());
            etAwayTeamFlag.setText(currentEvent.getAwayTeamFlag());
            etStadiumCapacity.setText(String.valueOf(currentEvent.getCapacity()));
        }
        
        // تعبئة رابط الصورة
        if (!TextUtils.isEmpty(currentEvent.getImageUrl())) {
            etImageUrl.setText(currentEvent.getImageUrl());
            Glide.with(this).load(currentEvent.getImageUrl()).into(ivSelectedImage);
            ivSelectedImage.setVisibility(View.VISIBLE);
        }
    }

    private void saveEvent() {
        if (!validateInputs()) return;
        
        btnSave.setEnabled(false);
        btnSave.setText("جاري الحفظ...");
        
        String imageUrl = etImageUrl.getText().toString().trim();
        saveEventToDatabase(imageUrl);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            etTitle.setError("العنوان مطلوب");
            return false;
        }
        
        if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
            etDescription.setError("الوصف مطلوب");
            return false;
        }
        
        if (spinnerType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "يرجى اختيار نوع الفعالية", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (selectedDateTime == null) {
            Toast.makeText(this, "يرجى اختيار التاريخ والوقت", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        String eventType = spinnerType.getSelectedItem().toString();
        
        if ("match".equals(eventType)) {
            if (TextUtils.isEmpty(etHomeTeam.getText().toString().trim()) ||
                TextUtils.isEmpty(etAwayTeam.getText().toString().trim())) {
                Toast.makeText(this, "يرجى إدخال أسماء الفرق", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }


    private void saveEventToDatabase(String imageUrl) {
        String eventId = currentEvent != null ? currentEvent.getId() : 
            eventsRef.push().getKey();
        
        if (eventId == null) {
            Toast.makeText(this, "حدث خطأ في إنشاء المعرف", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Event event = createEventFromInputs(eventId, imageUrl);
        
        eventsRef.child(eventId).setValue(event)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btnSave.setEnabled(true);
                    btnSave.setText("حفظ");
                    Toast.makeText(AddEditEventActivity.this, 
                        "تم حفظ الفعالية بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btnSave.setEnabled(true);
                    btnSave.setText("حفظ");
                    Toast.makeText(AddEditEventActivity.this, 
                        "فشل في حفظ الفعالية: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private Event createEventFromInputs(String eventId, String imageUrl) {
        Event event = new Event();
        event.setId(eventId);
        event.setTitle(etTitle.getText().toString().trim());
        event.setDescription(etDescription.getText().toString().trim());
        event.setLocation(etLocation.getText().toString().trim());
        event.setVenueName(etVenueName.getText().toString().trim());
        event.setTicketUrl(etTicketUrl.getText().toString().trim());
        event.setType(spinnerType.getSelectedItem().toString());
        event.setDate(selectedDateTime.getTime());
        event.setImageUrl(imageUrl);
        event.setFeatured(cbFeatured.isChecked());
        
        // إعداد الإحداثيات
        try {
            if (!TextUtils.isEmpty(etLat.getText().toString())) {
                event.setLat(Double.parseDouble(etLat.getText().toString()));
            }
            if (!TextUtils.isEmpty(etLng.getText().toString())) {
                event.setLng(Double.parseDouble(etLng.getText().toString()));
            }
            if (!TextUtils.isEmpty(etCapacity.getText().toString())) {
                event.setCapacity(Integer.parseInt(etCapacity.getText().toString()));
            }
        } catch (NumberFormatException e) {
            // استخدم القيم الافتراضية
        }
        
        String eventType = event.getType();
        
        // إعداد حقول الاحتفالات
        if ("celebration".equals(eventType)) {
            event.setCelebrationType(etCelebrationType.getText().toString().trim());
            event.setDuration(etDuration.getText().toString().trim());
            
            String activitiesText = etActivities.getText().toString().trim();
            if (!TextUtils.isEmpty(activitiesText)) {
                List<String> activities = new ArrayList<>();
                for (String activity : activitiesText.split(",")) {
                    activities.add(activity.trim());
                }
                event.setActivities(activities);
            }
            
            String performersText = etPerformers.getText().toString().trim();
            if (!TextUtils.isEmpty(performersText)) {
                List<String> performers = new ArrayList<>();
                for (String performer : performersText.split(",")) {
                    performers.add(performer.trim());
                }
                event.setPerformers(performers);
            }
        }
        
        // إعداد حقول المباريات
        if ("match".equals(eventType)) {
            event.setHomeTeam(etHomeTeam.getText().toString().trim());
            event.setAwayTeam(etAwayTeam.getText().toString().trim());
            event.setReferee(etReferee.getText().toString().trim());
            event.setMatchType(etMatchType.getText().toString().trim());
            event.setGroup(etGroup.getText().toString().trim());
            event.setHomeTeamFlag(etHomeTeamFlag.getText().toString().trim());
            event.setAwayTeamFlag(etAwayTeamFlag.getText().toString().trim());
        }
        
        // إعداد العد التنازلي
        event.setHasCountdown(event.shouldShowCountdown());
        event.setUpdatedAt(System.currentTimeMillis());
        
        if (currentEvent == null) {
            event.setCreatedAt(System.currentTimeMillis());
        } else {
            event.setCreatedAt(currentEvent.getCreatedAt());
        }
        
        return event;
    }

    public static Intent createIntent(android.content.Context context, Event event) {
        Intent intent = new Intent(context, AddEditEventActivity.class);
        if (event != null) {
            intent.putExtra(EXTRA_EVENT, event);
        }
        return intent;
    }
}