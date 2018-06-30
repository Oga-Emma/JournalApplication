package ng.com.oga_emma.journalapplication.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

@Dao
public interface EntryDAO {
/*
    @Query("SELECT * FROM entries")
    List<JournalEntryRoom> fetchAllEntries();*/

    @Query("SELECT * FROM entries")
    LiveData<List<JournalEntryRoom>> fetchAllEntries();

    @Query("SELECT * FROM entries WHERE _id= :id")
    LiveData<List<JournalEntryRoom>> fetchEntry(int id);

    @Insert
    void insertEntry(JournalEntryRoom entry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(JournalEntryRoom entry);

    @Delete
    void deleteEntry(JournalEntryRoom entry);
}
