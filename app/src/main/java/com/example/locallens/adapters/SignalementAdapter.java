package com.example.locallens.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.locallens.MainActivity;
import com.example.locallens.activities.AddIssueActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.example.locallens.R;
import com.example.locallens.database.MyDB;
import com.example.locallens.models.Signalement;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SignalementAdapter extends RecyclerView.Adapter<SignalementAdapter.ViewHolder> {

    private List<Signalement> signalementList;

    public SignalementAdapter(List<Signalement> signalementList) {
        this.signalementList = signalementList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_signalement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Signalement s = signalementList.get(position);
        holder.titreView.setText(s.titre);
        holder.descriptionView.setText(s.description);
        holder.typeView.setText("Type : " + s.type);
        holder.dateView.setText("Date : " + s.dateSignalement);
        holder.coordinatesView.setText(String.format("Lat: %.6f, Lng: %.6f", s.latitude, s.longitude));
        holder.statutView.setText(s.statut);

        if (s.statut.equalsIgnoreCase("Réglé")) {
            holder.statutView.setBackgroundColor(0xFF4CAF50); // Green
        } else if (s.statut.equalsIgnoreCase("En cours")) {
            holder.statutView.setBackgroundColor(0xFFFFC107); // Yellow
        } else {
            holder.statutView.setBackgroundColor(0xFFF44336); // Red
        }

        // Setup map button click
        holder.btnViewMap.setOnClickListener(v -> {
            // Open our custom map view activity
            Intent intent = new Intent(v.getContext(), com.example.locallens.activities.ViewLocationActivity.class);
            intent.putExtra("latitude", s.latitude);
            intent.putExtra("longitude", s.longitude);
            intent.putExtra("title", s.titre);
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddIssueActivity.class);
            intent.putExtra("signalement_id", s.id);
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Supprimer")
                    .setMessage("Voulez-vous supprimer ce signalement ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        new Thread(() -> {
                            MyDB.getInstance(v.getContext()).signalementDao().delete(s);
                            ((MainActivity) v.getContext()).runOnUiThread(() -> {
                                signalementList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, signalementList.size());
                                Toast.makeText(v.getContext(), "Supprimé !", Toast.LENGTH_SHORT).show();
                            });
                        }).start();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
            return true;
        });


    }


    @Override
    public int getItemCount() {
        return signalementList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titreView, descriptionView, typeView, dateView, coordinatesView, statutView;
        public android.widget.Button btnViewMap;

        public ViewHolder(View itemView) {
            super(itemView);
            titreView = itemView.findViewById(R.id.titreView);
            descriptionView = itemView.findViewById(R.id.descriptionView);
            typeView = itemView.findViewById(R.id.typeView);
            dateView = itemView.findViewById(R.id.dateView);
            coordinatesView = itemView.findViewById(R.id.coordinatesView);
            statutView = itemView.findViewById(R.id.statutView);
            btnViewMap = itemView.findViewById(R.id.btnViewMap);
        }
    }
}
