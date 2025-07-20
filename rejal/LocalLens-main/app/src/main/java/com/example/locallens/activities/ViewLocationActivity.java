package com.example.locallens.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.locallens.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        // Get location data from intent
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        title = getIntent().getStringExtra("title");

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : "Emplacement");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        LatLng location = new LatLng(latitude, longitude);
        
        // Add marker
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title != null ? title : "Emplacement sélectionné"));
        
        // Move camera to location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 