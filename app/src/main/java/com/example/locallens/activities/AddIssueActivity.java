package com.example.locallens.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.locallens.R;
import com.example.locallens.database.MyDB;
import com.example.locallens.models.Signalement;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class AddIssueActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    EditText editTitre, editDescription;
    Spinner spinnerType, spinnerStatut;
    Button buttonDate, buttonSave;
    TextView tvLocationInfo;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker selectedLocationMarker;
    
    double latitude = 33.5898; // Default to Casablanca
    double longitude = -7.6039;
    boolean locationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        editTitre = findViewById(R.id.editTitre);
        editDescription = findViewById(R.id.editDescription);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatut = findViewById(R.id.spinnerStatut);
        buttonDate = findViewById(R.id.buttonDate);
        buttonSave = findViewById(R.id.buttonSave);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnRefreshLocation = findViewById(R.id.btnRefreshLocation);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.types_problemes, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statutAdapter = ArrayAdapter.createFromResource(
                this, R.array.statuts_problemes, android.R.layout.simple_spinner_item);
        statutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatut.setAdapter(statutAdapter);

        int signalementId = getIntent().getIntExtra("signalement_id", -1);
        if (signalementId != -1) {
            // Edit mode
            new Thread(() -> {
                Signalement existing = MyDB.getInstance(this).signalementDao().getById(signalementId);
                runOnUiThread(() -> {
                    editTitre.setText(existing.titre);
                    editDescription.setText(existing.description);

                    // Set spinner selections
                    setSpinnerSelection(spinnerType, existing.type);
                    setSpinnerSelection(spinnerStatut, existing.statut);

                    // Set date
                    buttonDate.setText(existing.dateSignalement);

                    // Set coordinates for editing
                    latitude = existing.latitude;
                    longitude = existing.longitude;
                    locationSelected = true;

                    // Save the ID for later update
                    buttonSave.setTag(existing.id);

                });
            }).start();
        }


        buttonDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) ->
                    buttonDate.setText(year + "-" + (month + 1) + "-" + day), y, m, d);
            dp.show();
        });

        buttonSave.setOnClickListener(v -> saveSignalement());
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        btnRefreshLocation.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });

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
        
        // Set up map click listener for manual location selection
        mMap.setOnMapClickListener(latLng -> {
            // Remove previous marker
            if (selectedLocationMarker != null) {
                selectedLocationMarker.remove();
            }
            
            // Add new marker
            selectedLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Emplacement sélectionné"));
            
            // Update coordinates
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            locationSelected = true;
            
            // Update location info text
            tvLocationInfo.setText(String.format("Position actuelle: Lat: %.6f, Lng: %.6f", latitude, longitude));
        });
        
        // Check location permission and get current location automatically
        if (checkLocationPermission()) {
            getCurrentLocation();
        } else {
            // Request location permission
            requestLocationPermission();
        }
        
        // If editing an existing issue, show the marker
        if (locationSelected) {
            LatLng existingLocation = new LatLng(latitude, longitude);
            selectedLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(existingLocation)
                    .title("Emplacement sélectionné"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(existingLocation, 15));
            tvLocationInfo.setText(String.format("Lat: %.6f, Lng: %.6f", latitude, longitude));
        }
    }
    
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, 
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
            LOCATION_PERMISSION_REQUEST_CODE);
    }
    
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // Show loading message
        tvLocationInfo.setText("Récupération de votre position...");
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        
                        // Remove any existing marker
                        if (selectedLocationMarker != null) {
                            selectedLocationMarker.remove();
                        }
                        
                        // Add marker for current location
                        selectedLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Votre position actuelle"));
                        
                        // Move camera to current location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        
                        // Set as default coordinates
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        locationSelected = true;
                        
                        // Update location info text
                        tvLocationInfo.setText(String.format("Position actuelle: Lat: %.6f, Lng: %.6f", latitude, longitude));
                        
                        Toast.makeText(this, "Position détectée automatiquement", Toast.LENGTH_SHORT).show();
                    } else {
                        // Location is null, show default location
                        tvLocationInfo.setText("Impossible de détecter votre position. Appuyez sur la carte pour sélectionner.");
                        LatLng casablanca = new LatLng(33.5898, -7.6039);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 12));
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle failure
                    tvLocationInfo.setText("Erreur de localisation. Appuyez sur la carte pour sélectionner.");
                    LatLng casablanca = new LatLng(33.5898, -7.6039);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 12));
                });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get current location
                getCurrentLocation();
                Toast.makeText(this, "Permission de localisation accordée", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show message
                tvLocationInfo.setText("Permission refusée. Appuyez sur la carte pour sélectionner l'emplacement.");
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
                
                // Move to default location
                LatLng casablanca = new LatLng(33.5898, -7.6039);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 12));
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }


    private void saveSignalement() {
        String titre = editTitre.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String statut = spinnerStatut.getSelectedItem().toString();
        String date = buttonDate.getText().toString();

        // Validate required fields
        if (titre.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un titre", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (description.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir une description", Toast.LENGTH_SHORT).show();
            return;
        }

        Signalement s = new Signalement();
        s.titre = titre;
        s.description = description;
        s.type = type;
        s.statut = statut;
        s.dateSignalement = date;
        s.latitude = latitude;
        s.longitude = longitude;
        s.imagePath = null;

        new Thread(() -> {
            Integer existingId = (Integer) buttonSave.getTag();
            if (existingId != null) {
                s.id = existingId;
                MyDB.getInstance(this).signalementDao().update(s);
            } else {
                MyDB.getInstance(this).signalementDao().insert(s);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Signalement enregistré !", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }


}
