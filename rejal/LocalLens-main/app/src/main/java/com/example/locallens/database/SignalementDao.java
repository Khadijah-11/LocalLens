package com.example.locallens.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.locallens.models.Signalement;

import java.util.List;

@Dao
public interface SignalementDao {

    @Insert
    void insert(Signalement s);

    @Update
    void update(Signalement s);

    @Delete
    void delete(Signalement s);

    @Query("SELECT * FROM Signalement ORDER BY dateSignalement DESC")
    List<Signalement> getAll();

    @Query("SELECT * FROM Signalement WHERE type = :type")
    List<Signalement> filterByType(String type);

    @Query("SELECT * FROM Signalement WHERE titre LIKE '%' || :motCle || '%' OR description LIKE '%' || :motCle || '%'")
    List<Signalement> search(String motCle);


    @Query("SELECT * FROM Signalement WHERE id = :signalementId LIMIT 1")
    Signalement getById(int signalementId);

}
