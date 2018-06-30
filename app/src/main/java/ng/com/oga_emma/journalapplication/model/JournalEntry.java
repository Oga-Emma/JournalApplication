package ng.com.oga_emma.journalapplication.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class JournalEntry {
    private String UUID;
    private String entryTitle;
    private String entryBody;
    private Long entryDate;

    public JournalEntry() {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.entryTitle = "";
        this.entryBody = "";
        this.entryDate = 0L;
    }

    public JournalEntry(String UUID, String entryTitle,
                        String entryBody, Date entryDate) {
        this.UUID = UUID;
        this.entryTitle = entryTitle;
        this.entryBody = entryBody;
        this.entryDate = entryDate.getTime();
    }

    public JournalEntry(JournalEntryRoom journalEntryRoom) {
        this.UUID = journalEntryRoom.getId() + "";
        this.entryTitle = journalEntryRoom.getEntryTitle();
        this.entryBody = journalEntryRoom.getEntryBody();
        this.entryDate = journalEntryRoom.getEntryDate().getTime();
    }

    public JournalEntry(String entryTitle,
                        String entryBody, Date entryDate) {

        this.UUID = java.util.UUID.randomUUID().toString();
        this.entryTitle = entryTitle;
        this.entryBody = entryBody;
        this.entryDate = entryDate.getTime();
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getEntryTitle() {
        return entryTitle;
    }

    public void setEntryTitle(String entryTitle) {
        this.entryTitle = entryTitle;
    }

    public String getEntryBody() {
        return entryBody;
    }

    public void setEntryBody(String entryBody) {
        this.entryBody = entryBody;
    }

    public Long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Long entryDate) {
        this.entryDate = entryDate;
    }
}
