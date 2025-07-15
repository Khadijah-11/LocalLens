package com.example.locallens.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locallens.R;
import com.example.locallens.database.MyDB;
import com.example.locallens.models.Signalement;

import java.util.Calendar;

public class AddIssueActivity extends AppCompatActivity {

    EditText editTitre, editDescription;
    Spinner spinnerType, spinnerStatut;
    Button buttonDate, buttonSave;

    double latitude = 33.5898;
    double longitude = -7.6039;

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

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.types_problemes, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statutAdapter = ArrayAdapter.createFromResource(
                this, R.array.statuts_problemes, android.R.layout.simple_spinner_item);
        statutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatut.setAdapter(statutAdapter);

        buttonDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) ->
                    buttonDate.setText(year + "-" + (month + 1) + "-" + day), y, m, d);
            dp.show();
        });

        buttonSave.setOnClickListener(v -> saveSignalement());
    }

    private void saveSignalement() {
        String titre = editTitre.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String statut = spinnerStatut.getSelectedItem().toString();
        String date = buttonDate.getText().toString();

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
            MyDB.getInstance(this).signalementDao().insert(s);
            runOnUiThread(() -> {
                Toast.makeText(this, "Signalement enregistré !", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
