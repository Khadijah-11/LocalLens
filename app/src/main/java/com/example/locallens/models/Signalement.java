package com.example.locallens.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Signalement {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String titre;
    public String description;
    public String type;
    public String statut;
    public String dateSignalement;
    public double latitude;
    public double longitude;
    public String imagePath;
}
