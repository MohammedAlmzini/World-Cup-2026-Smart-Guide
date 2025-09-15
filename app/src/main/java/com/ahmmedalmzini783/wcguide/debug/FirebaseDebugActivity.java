package com.ahmmedalmzini783.wcguide.debug;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.data.repository.RestaurantRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDebugActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseDebug";
    private TextView debugText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        
        debugText = findViewById(R.id.debug_text);
        
        // Test direct Firebase connection
        testFirebaseConnection();
        
        // Test Restaurant Repository
        testRestaurantRepository();
    }
    
    private void testFirebaseConnection() {
        Log.d(TAG, "Testing direct Firebase connection to restaurants node");
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Direct Firebase query - Children count: " + dataSnapshot.getChildrenCount());
                StringBuilder result = new StringBuilder("Firebase Direct Test:\n");
                result.append("Children count: ").append(dataSnapshot.getChildrenCount()).append("\n\n");
                
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Child key: " + child.getKey());
                    result.append("Restaurant ID: ").append(child.getKey()).append("\n");
                    
                    if (child.hasChild("name")) {
                        String name = child.child("name").getValue(String.class);
                        Log.d(TAG, "Restaurant name: " + name);
                        result.append("Name: ").append(name).append("\n");
                    }
                    
                    if (child.hasChild("country")) {
                        String country = child.child("country").getValue(String.class);
                        Log.d(TAG, "Restaurant country: " + country);
                        result.append("Country: ").append(country).append("\n");
                    }
                    
                    result.append("---\n");
                }
                
                runOnUiThread(() -> debugText.setText(result.toString()));
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
                runOnUiThread(() -> debugText.setText("Firebase Error: " + databaseError.getMessage()));
            }
        });
    }
    
    private void testRestaurantRepository() {
        Log.d(TAG, "Testing RestaurantRepository");
        
        RestaurantRepository repository = new RestaurantRepository();
        String[] countries = {"usa", "USA", "United States"};
        
        repository.getRestaurantsByMultipleCountries(countries).observe(this, resource -> {
            if (resource != null) {
                Log.d(TAG, "Repository result status: " + resource.getStatus());
                if (resource.getData() != null) {
                    Log.d(TAG, "Repository result count: " + resource.getData().size());
                } else {
                    Log.d(TAG, "Repository result data is null");
                }
            } else {
                Log.d(TAG, "Repository result is null");
            }
        });
    }
}
