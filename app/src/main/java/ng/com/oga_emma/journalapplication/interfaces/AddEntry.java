package ng.com.oga_emma.journalapplication.interfaces;

import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

public interface AddEntry {

    public interface AddEntryListener{
        void onEntryAddSuccess();
        void onEntryAddFail();
    }

    public interface UpdateEntryListener{
        void onUpdateEntrySuccess();
        void onUpdateEntryFail();
    }

    public interface DeleteEntryListener{
        void onDeleteEntrySuccess();
        void onDeleteEntryFail();
    }
}
