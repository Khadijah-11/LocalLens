package com.example.locallens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locallens.activities.AddIssueActivity;
import com.example.locallens.adapters.SignalementAdapter;
import com.example.locallens.database.MyDB;
import com.example.locallens.models.Signalement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    SignalementAdapter adapter;
    List<Signalement> signalements;

    SearchView searchView;
    Spinner spinnerFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddIssueActivity.class);
            startActivity(i);
        });

        // Setup spinner
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                this, R.array.statuts_filter, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        // Default search text
        String[] searchQuery = {""};


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery[0] = newText;
                applyFilters(newText, spinnerFilter.getSelectedItem().toString());
                return true;
            }
        });


        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters(searchQuery[0], parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        refreshData();

    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        new Thread(() -> {
            List<Signalement> signalements = MyDB.getInstance(this).signalementDao().getAll();

            runOnUiThread(() -> {
                adapter = new SignalementAdapter(signalements);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                if (signalements.isEmpty()) {
                    Toast.makeText(this, "Aucun signalement trouvé.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    private void applyFilters(String query, String selectedStatus) {
        new Thread(() -> {
            List<Signalement> fullList = MyDB.getInstance(this).signalementDao().getAll();
            List<Signalement> filtered = new ArrayList<>();

            for (Signalement s : fullList) {
                boolean matchesQuery = s.titre.toLowerCase().contains(query.toLowerCase())
                        || s.type.toLowerCase().contains(query.toLowerCase())
                        || s.statut.toLowerCase().contains(query.toLowerCase());

                boolean matchesStatus = selectedStatus.equals("Tous") || s.statut.equalsIgnoreCase(selectedStatus);

                if (matchesQuery && matchesStatus) {
                    filtered.add(s);
                }
            }

            runOnUiThread(() -> {
                adapter = new SignalementAdapter(filtered);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }



}
