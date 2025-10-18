package com.ahmmedalmzini783.wcguide.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManageEventsActivity extends AppCompatActivity implements AdminEventAdapter.OnEventActionListener {

    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    private Button fabAddEvent;
    private Spinner spinnerFilter;
    
    private List<Event> allEvents;
    private List<Event> filteredEvents;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        createSimpleUI();
        setupRecyclerView();
        setupFirebase();
        setupFilterSpinner();
        loadEvents();
        
        setTitle("إدارة الفعاليات");
    }

    private void createSimpleUI() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);
        
        // فلتر النوع
        spinnerFilter = new Spinner(this);
        mainLayout.addView(spinnerFilter);
        
        // قائمة الأحداث
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                0, 1.0f));
        mainLayout.addView(recyclerView);
        
        // زر إضافة حدث
        fabAddEvent = new Button(this);
        fabAddEvent.setText("إضافة فعالية جديدة");
        fabAddEvent.setOnClickListener(v -> {
            Intent intent = AddEditEventActivity.createIntent(this, null);
            startActivity(intent);
        });
        mainLayout.addView(fabAddEvent);
        
        setContentView(mainLayout);
    }

    private void setupRecyclerView() {
        allEvents = new ArrayList<>();
        filteredEvents = new ArrayList<>();
        adapter = new AdminEventAdapter(filteredEvents, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFirebase() {
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
    }

    private void setupFilterSpinner() {
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("جميع الفعاليات");
        filterOptions.add("الاحتفالات");
        filterOptions.add("المباريات");
        filterOptions.add("الفعاليات العامة");
        filterOptions.add("الفعاليات القادمة");
        filterOptions.add("الفعاليات المنتهية");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
        
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                filterEvents(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();
                
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        allEvents.add(event);
                    }
                }
                
                // ترتيب الفعاليات حسب التاريخ
                Collections.sort(allEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        if (e1.getDate() == null && e2.getDate() == null) return 0;
                        if (e1.getDate() == null) return 1;
                        if (e2.getDate() == null) return -1;
                        return e1.getDate().compareTo(e2.getDate());
                    }
                });
                
                filterEvents(spinnerFilter.getSelectedItem().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ManageEventsActivity.this, 
                    "فشل في تحميل الفعاليات: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterEvents(String filter) {
        filteredEvents.clear();
        
        long currentTime = System.currentTimeMillis();
        
        for (Event event : allEvents) {
            boolean shouldInclude = false;
            
            switch (filter) {
                case "جميع الفعاليات":
                    shouldInclude = true;
                    break;
                case "الاحتفالات":
                    shouldInclude = "celebration".equals(event.getType());
                    break;
                case "المباريات":
                    shouldInclude = "match".equals(event.getType());
                    break;
                case "الفعاليات العامة":
                    shouldInclude = "general".equals(event.getType());
                    break;
                case "الفعاليات القادمة":
                    shouldInclude = event.getDate() != null && 
                        event.getDate().getTime() > currentTime;
                    break;
                case "الفعاليات المنتهية":
                    shouldInclude = event.getDate() != null && 
                        event.getDate().getTime() < currentTime;
                    break;
            }
            
            if (shouldInclude) {
                filteredEvents.add(event);
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditEvent(Event event) {
        Intent intent = AddEditEventActivity.createIntent(this, event);
        startActivity(intent);
    }

    @Override
    public void onDeleteEvent(Event event) {
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("حذف الفعالية");
        builder.setMessage("هل أنت متأكد من حذف هذه الفعالية؟");
        builder.setPositiveButton("حذف", (dialog, which) -> {
            deleteEvent(event);
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    @Override
    public void onViewEvent(Event event) {
        Intent intent = com.ahmmedalmzini783.wcguide.ui.events.EventDetailsActivity.createIntent(this, event);
        startActivity(intent);
    }

    private void deleteEvent(Event event) {
        eventsRef.child(event.getId()).removeValue()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "تم حذف الفعالية بنجاح", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "فشل في حذف الفعالية: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onToggleCountdown(Event event) {
        event.setHasCountdown(!event.isHasCountdown());
        event.setUpdatedAt(System.currentTimeMillis());
        
        eventsRef.child(event.getId()).setValue(event)
            .addOnSuccessListener(aVoid -> {
                String message = event.isHasCountdown() ? 
                    "تم تفعيل العد التنازلي" : "تم إلغاء العد التنازلي";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "فشل في تحديث الفعالية: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onToggleFeatured(Event event) {
        event.setFeatured(!event.isFeatured());
        event.setUpdatedAt(System.currentTimeMillis());
        
        eventsRef.child(event.getId()).setValue(event)
            .addOnSuccessListener(aVoid -> {
                String message = event.isFeatured() ? 
                    "تم تمييز الفعالية" : "تم إلغاء تمييز الفعالية";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "فشل في تحديث التمييز: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
}