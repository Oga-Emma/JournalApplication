package ng.com.oga_emma.journalapplication.views;


import android.app.Activity;
import android.app.DialogFragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ng.com.oga_emma.journalapplication.MainActivity;
import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.adapters.EntryAdapter;
import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;
import ng.com.oga_emma.journalapplication.database.JournalEntryFirebaseDB;
import ng.com.oga_emma.journalapplication.interfaces.Entry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;
import ng.com.oga_emma.journalapplication.utils.SetNameDialogFragment;
import ng.com.oga_emma.journalapplication.utils.SharePreferenceKeys;
import ng.com.oga_emma.journalapplication.utils.SigninMode;

public class JournalEntriesFragment extends Fragment {
    private static final String TAG = JournalEntriesFragment.class.getSimpleName();

    public static final String USER_NAME_KEY = "user_name_key";
    public static final int USER_NAME_REQUEST_CODE = 400;

    private FirebaseAuth auth;

    private RecyclerView entrieRecyclerView;
    private ArrayList<JournalEntry> journalEntryList;
    private EntryAdapter adapter;

    private ConstraintLayout titleLayout;
    private TextView displayNameTextView;

    private JounalEntryLocalStroage entryLocalDB;
    private static JournalEntryFirebaseDB entryFirebaseDB = null;


    LiveData<List<JournalEntryRoom>> entries;

    public JournalEntriesFragment() {
        // Required empty public constructor
    }
    
    public static Fragment newInstance(boolean newSignIn) {
        JournalEntriesFragment fragment = new JournalEntriesFragment();
        Bundle extra = new Bundle();
        extra.putBoolean(MainActivity.NEW_SIGN_IN, newSignIn);

        fragment.setArguments(extra);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_journal_entries, container, false);

        auth = FirebaseAuth.getInstance();
        entryLocalDB = JounalEntryLocalStroage.getInstance(getContext().getApplicationContext());

        if(auth.getCurrentUser() != null)
            entryFirebaseDB = JournalEntryFirebaseDB.getInstance(auth.getCurrentUser().getUid());

        displayNameTextView = v.findViewById(R.id.display_name_text_view);
        displayNameTextView.setText(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(SharePreferenceKeys.DISPLAY_NAME, " "));

        titleLayout = v.findViewById(R.id.constraintLayout);

        entrieRecyclerView = v.findViewById(R.id.entries_recycler_view);
        entrieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        journalEntryList = new ArrayList<>();
        adapter = new EntryAdapter(journalEntryList);
        entrieRecyclerView.setAdapter(adapter);

        String name = "" + PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(SharePreferenceKeys.DISPLAY_NAME, " ");

        displayNameTextView.setText(name);

        retrieveEntries();
        setUserName();

        return v;
    }

    private void setUserName() {
        if(getArguments() != null) {

            if (getArguments().getBoolean(MainActivity.NEW_SIGN_IN, false)) {
                SetNameDialogFragment dialg = new SetNameDialogFragment();
                dialg.setTargetFragment(this, USER_NAME_REQUEST_CODE);
                dialg.setCancelable(false);
                dialg.show(getActivity().getSupportFragmentManager(), "JournalEntriesFragment");
            }
        }
    }

    private void retrieveEntries() {

        journalEntryList.clear();

        if(entryFirebaseDB != null){

            Log.i(TAG, "firebase initialized success");

            entryFirebaseDB.fetchJournalEntries(new Entry.JournalEntriesFetchOnceListener() {
                @Override
                public void onJournalEntryFetchFailed() {

                    Log.i(TAG, "firebase initialized success");
                }

                @Override
                public void onJournalEntryFetchSuccess(Map<String, JournalEntry> entryMap) {
                    journalEntryList.clear();

                    journalEntryList.addAll(entryMap.values());
                    refreshUi();

                }
            });

            entryFirebaseDB.setupFirebaseChildEventListeners(new Entry.JournalEntriesFetchListener() {
                @Override
                public void onJournalEntryFetchFailed() {

                }

                @Override
                public void onJournalEntryAdded(JournalEntry entry) {
                    journalEntryList.add(0, entry);
                    adapter.notifyItemInserted(0);
                }

                @Override
                public void onJournalEntryModified(JournalEntry entry, String positionOnLise) {
                    if(journalEntryList.contains(entry)) {
                        int index = journalEntryList.indexOf(entry);
                        journalEntryList.remove(entry);
                        journalEntryList.add(index, entry);
                    }
                }

                @Override
                public void onJournalEntryRemoved(JournalEntry entry, String positionOnLise) {
                    journalEntryList.remove(entry);
                    refreshUi();
                }

                @Override
                public void onJournalEntryFetchSuccess(Map<String, JournalEntry> entryMap) {
                    journalEntryList.clear();

                    journalEntryList.addAll(entryMap.values());
                    refreshUi();
                }
            });

        }else {
            /*for(JournalEntryRoom journalEntryRoom : entryLocalDB.entryDAO().fetchAllEntries()){
                        journalEntryList.add(new JournalEntry(journalEntryRoom));
            }*/

            entries = entryLocalDB.entryDAO().fetchAllEntries();
            entries.observe(this, new Observer<List<JournalEntryRoom>>() {
                @Override
                public void onChanged(@Nullable List<JournalEntryRoom> journalEntryRooms) {

                    for(JournalEntryRoom journalEntryRoom : journalEntryRooms){
                        journalEntryList.add(new JournalEntry(journalEntryRoom));
                    }

                    Log.i(TAG, journalEntryRooms.size() + "");
                }
            });
        }

        refreshUi();
    }

    private void refreshUi() {
/*
        adapter = new EntryAdapter(journalEntryList);
        entrieRecyclerView.setAdapter(adapter);*/
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshUi();
    }

    @Override
    public void onStop() {
        super.onStop();


        if(entryFirebaseDB != null){
            entryFirebaseDB.clearListeners();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == USER_NAME_REQUEST_CODE ){

            if(resultCode == Activity.RESULT_OK) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit();

                String name = data.getExtras().getString(USER_NAME_KEY, " ");
                displayNameTextView.setText(name);

                editor.putString(SharePreferenceKeys.DISPLAY_NAME, name)
                        .putBoolean(SharePreferenceKeys.USER_SIGNED_IN, true)
                        .apply();
            }else if(resultCode == Activity.RESULT_CANCELED){
                getActivity().finish();
            }
        }
    }
}
