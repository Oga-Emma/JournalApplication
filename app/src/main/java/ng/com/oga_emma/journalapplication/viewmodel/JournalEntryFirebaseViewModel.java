package ng.com.oga_emma.journalapplication.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;
import ng.com.oga_emma.journalapplication.database.JournalEntryFirebaseDB;
import ng.com.oga_emma.journalapplication.interfaces.Entry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

public class JournalEntryFirebaseViewModel extends AndroidViewModel {

    public static final String TAG = JournalEntryFirebaseViewModel.class.getSimpleName();
    private JournalEntryFirebaseDB db;

    private List<JournalEntryRoom> listLiveData;
    private Entry.JournalEntriesFetchOnceListener listener;

    public JournalEntryFirebaseViewModel(@NonNull Application application) {
        super(application);

        Log.d(TAG, "Retrieving data");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = JournalEntryFirebaseDB.getInstance(userId);
    }

    public void setOnDataFetchListener(Entry.JournalEntriesFetchOnceListener listener) {
        this.listener = listener;

        db .fetchJournalEntries(new Entry.JournalEntriesFetchOnceListener() {
            @Override
            public void onJournalEntryFetchFailed() {
                listener.onJournalEntryFetchFailed();
            }

            @Override
            public void onJournalEntryFetchSuccess(Map<String, JournalEntry> entryMap) {
                listener.onJournalEntryFetchSuccess(entryMap);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if(db != null){
            db.clearListeners();
        }
    }
}
