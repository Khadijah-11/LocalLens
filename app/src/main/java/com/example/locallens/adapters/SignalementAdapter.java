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
        holder.typeView.setText("Type : " + s.type);
        holder.dateView.setText("Date : " + s.dateSignalement);
        holder.statutView.setText(s.statut);

        if (s.statut.equalsIgnoreCase("Réglé")) {
            holder.statutView.setBackgroundColor(0xFF4CAF50); // Green
        } else if (s.statut.equalsIgnoreCase("En cours")) {
            holder.statutView.setBackgroundColor(0xFFFFC107); // Yellow
        } else {
            holder.statutView.setBackgroundColor(0xFFF44336); // Red
        }

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
        public TextView titreView, typeView, dateView, statutView;

        public ViewHolder(View itemView) {
            super(itemView);
            titreView = itemView.findViewById(R.id.titreView);
            typeView = itemView.findViewById(R.id.typeView);
            dateView = itemView.findViewById(R.id.dateView);
            statutView = itemView.findViewById(R.id.statutView);
        }
    }
}
