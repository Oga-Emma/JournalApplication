package ng.com.oga_emma.journalapplication.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;

public class AddEntryToRoomViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final JounalEntryLocalStroage database;
    private final int entryId;

    public AddEntryToRoomViewModelFactory(JounalEntryLocalStroage database, int entryId) {
        this.database = database;
        this.entryId = entryId;
    }

}
