package com.example.locallens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locallens.activities.AddIssueActivity;
import com.example.locallens.adapters.SignalementAdapter;
import com.example.locallens.database.MyDB;
import com.example.locallens.models.Signalement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    SignalementAdapter adapter;
    List<Signalement> signalements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddIssueActivity.class);
            startActivity(i);
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

                if (signalements.isEmpty()) {
                    Toast.makeText(this, "Aucun signalement trouvé.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}
