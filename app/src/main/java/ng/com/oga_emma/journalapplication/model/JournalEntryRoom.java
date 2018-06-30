package ng.com.oga_emma.journalapplication.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "entries")
public class JournalEntryRoom {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "entry_title")
    private String entryTitle;

    @ColumnInfo(name = "entry_body")
    private String entryBody;

    @ColumnInfo(name = "entry_date")
    private Date entryDate;


    @Ignore
    public JournalEntryRoom(String entryTitle, String entryBody, Date entryDate) {
        this.entryTitle = entryTitle;
        this.entryBody = entryBody;
        this.entryDate = entryDate;
    }

    public JournalEntryRoom(int id, String entryTitle, String entryBody, Date entryDate) {
        this.id = id;
        this.entryTitle = entryTitle;
        this.entryBody = entryBody;
        this.entryDate = entryDate;
    }

    public int getId() {
        return id;
    }

    public String getEntryTitle() {
        return entryTitle;
    }

    public String getEntryBody() {
        return entryBody;
    }

    public Date getEntryDate() {
        return entryDate;
    }
}
