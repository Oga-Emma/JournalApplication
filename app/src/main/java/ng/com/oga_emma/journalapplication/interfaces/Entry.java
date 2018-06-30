package ng.com.oga_emma.journalapplication.interfaces;

import java.util.List;
import java.util.Map;

import ng.com.oga_emma.journalapplication.model.JournalEntry;

public interface Entry {

    interface View{
        void showUserInfo();
        void showEntry(List<JournalEntry> journalEntryList);
        void showProgess();
        void hideProgress();
        void showNoEntryMessage();
        void showLoadEntryError();
        void showNoNetworkError();
        void showSettings();
        void logout();
    }

    interface Presenter{
        void displayJournalEntries(String userId);
        void addNewEntry(JournalEntry entry);

    }

    interface Repository{
        void setupFirebaseChildEventListeners(JournalEntriesFetchListener listener);
        void fetchJournalEntries(JournalEntriesFetchOnceListener listener);
        void addJournalEntry(JournalEntry entry, AddEntry.AddEntryListener listener);
        void updateJournalEntry(JournalEntry entry, AddEntry.UpdateEntryListener listener);
        void deleteJournalEntry(JournalEntry entry, AddEntry.DeleteEntryListener listener);
    }

    interface JournalEntriesFetchOnceListener{
        void onJournalEntryFetchFailed();
        void onJournalEntryFetchSuccess(Map<String, JournalEntry> entryMap);
    }

    interface JournalEntriesFetchListener{
        void onJournalEntryFetchFailed();
        void onJournalEntryAdded(JournalEntry entry);
        void onJournalEntryModified(JournalEntry entry, String positionOnLise);
        void onJournalEntryRemoved(JournalEntry entry, String positionOnLise);

        void onJournalEntryFetchSuccess(Map<String, JournalEntry> entryMap);
    }
}
