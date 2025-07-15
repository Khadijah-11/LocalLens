package com.example.locallens.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.locallens.R;
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
