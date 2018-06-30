package ng.com.oga_emma.journalapplication.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@Entity(tableName = "entries")
public class JournalEntryRoom implements Parcelable {

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
    private String entryId;

    @Ignore
    public String getEntryId() {
        return entryId;
    }

    @Ignore
    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setEntryTitle(String entryTitle) {
        this.entryTitle = entryTitle;
    }

    public void setEntryBody(String entryBody) {
        this.entryBody = entryBody;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.entryTitle);
        dest.writeString(this.entryBody);
        dest.writeLong(this.entryDate != null ? this.entryDate.getTime() : -1);
        dest.writeString(this.entryId);
    }


    @Ignore
    protected JournalEntryRoom(Parcel in) {
        this.id = in.readInt();
        this.entryTitle = in.readString();
        this.entryBody = in.readString();
        long tmpEntryDate = in.readLong();
        this.entryDate = tmpEntryDate == -1 ? null : new Date(tmpEntryDate);
        this.entryId = in.readString();
    }


    @Ignore
    public static final Parcelable.Creator<JournalEntryRoom> CREATOR = new Parcelable.Creator<JournalEntryRoom>() {
        @Override
        public JournalEntryRoom createFromParcel(Parcel source) {
            return new JournalEntryRoom(source);
        }

        @Override
        public JournalEntryRoom[] newArray(int size) {
            return new JournalEntryRoom[size];
        }
    };
}
