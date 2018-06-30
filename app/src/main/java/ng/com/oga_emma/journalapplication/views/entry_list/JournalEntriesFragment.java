package ng.com.oga_emma.journalapplication.views.entry_list;


import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ng.com.oga_emma.journalapplication.MainActivity;
import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.adapters.EntryAdapter;
import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;
import ng.com.oga_emma.journalapplication.database.JournalEntryFirebaseDB;
import ng.com.oga_emma.journalapplication.interfaces.AddEntry;
import ng.com.oga_emma.journalapplication.interfaces.Entry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;
import ng.com.oga_emma.journalapplication.utils.SetNameDialogFragment;
import ng.com.oga_emma.journalapplication.utils.SharePreferenceKeys;
import ng.com.oga_emma.journalapplication.views.add_and_edit_entry.AddEditEntryActivity;

public class JournalEntriesFragment extends Fragment implements Entry.EntryInteractionListener,
        Observer<List<JournalEntryRoom>>, Entry.JournalEntriesFetchListener, Entry.JournalEntriesFetchOnceListener {

    private static final String TAG = JournalEntriesFragment.class.getSimpleName();

    public static final String USER_NAME_KEY = "user_name_key";
    public static final int USER_NAME_REQUEST_CODE = 400;

    private FirebaseAuth auth;

    private RecyclerView entrieRecyclerView;
    private ArrayList<JournalEntry> journalEntryList;
    private EntryAdapter adapter;

    private ConstraintLayout titleLayout;
    private TextView displayNameTextView, dateTextView, weekTextView, entryCountTextView;

    private JounalEntryLocalStroage entryLocalDB;
    private static JournalEntryFirebaseDB entryFirebaseDB = null;

    private TextView noEntryMessage;
    private LinearLayout loadingLayout;

    LiveData<List<JournalEntryRoom>> entries;
    @Nullable
    private List<JournalEntryRoom> journalEntryRoomList;

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

        if(auth.getCurrentUser() != null && !auth.getCurrentUser().getUid().isEmpty()) {
            Log.i("DATABASE TYPE", "firebase storage");
            entryFirebaseDB = JournalEntryFirebaseDB.getInstance(auth.getCurrentUser().getUid());
        }else{
            Log.i("DATABASE TYPE", "local storage ");
            entryFirebaseDB = null;

        }

        dateTextView = v.findViewById(R.id.date_text_view);
        weekTextView = v.findViewById(R.id.weekday_text_view);
        entryCountTextView = v.findViewById(R.id.entry_count_text_view);

        noEntryMessage = v.findViewById(R.id.no_entry_text_view);
        loadingLayout = v.findViewById(R.id.loading_layout);

        loadingLayout.setVisibility(View.VISIBLE);

        displayNameTextView = v.findViewById(R.id.display_name_text_view);
        displayNameTextView.setText(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(SharePreferenceKeys.DISPLAY_NAME, " "));

        titleLayout = v.findViewById(R.id.constraintLayout);

        entrieRecyclerView = v.findViewById(R.id.entries_recycler_view);
        entrieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        journalEntryList = new ArrayList<>();
        adapter = new EntryAdapter(journalEntryList, this);
        entrieRecyclerView.setAdapter(adapter);

        String name = "" + PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(SharePreferenceKeys.DISPLAY_NAME, " ");

        displayNameTextView.setText(name);

        setUserName();

        Date date = new Date();
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");

        dateTextView.setText(dateFormat.format(date));
        weekTextView.setText(weekFormat.format(date));

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

            entryFirebaseDB.fetchJournalEntries(this);
            entryFirebaseDB.setupFirebaseChildEventListeners(this);

        }else {
            /*for(JournalEntryRoom journalEntryRoom : entryLocalDB.entryDAO().fetchAllEntries()){
                        journalEntryList.add(new JournalEntry(journalEntryRoom));
            }*/

            entries = entryLocalDB.entryDAO().fetchAllEntries();
            entries.observe(this, this);
        }

        refreshUi();
    }

    private void refreshUi() {
        loadingLayout.setVisibility(View.GONE);

        if(journalEntryList.isEmpty())
            noEntryMessage.setVisibility(View.VISIBLE);
        else
            noEntryMessage.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
        entryCountTextView.setText(getString(R.string.entry_count, journalEntryList.size()));
    }

    @Override
    public void onEntryClicked(JournalEntry entry) {

        JournalEntryRoom entryTemp =
                new JournalEntryRoom(entry.getEntryTitle(), entry.getEntryBody(), new Date(entry.getEntryDate()));
        entryTemp.setEntryId(entry.getUUID());

//        Toast.makeText(getContext(),  "Entry clicked", Toast.LENGTH_SHORT).show();
        AddEditEntryActivity.launchActivity(getActivity(), entryTemp);
    }

    @Override
    public void onEntryDeleted(JournalEntry entry) {
//        Toast.makeText(getContext(),  "Delete clicked", Toast.LENGTH_SHORT).show();
        journalEntryList.remove(entry);

        if(entryFirebaseDB != null){

            Log.i(TAG, "firebase initialized success");

            entryFirebaseDB.deleteJournalEntry(entry, new AddEntry.DeleteEntryListener() {
                @Override
                public void onDeleteEntrySuccess() {
                    refreshUi();
                }

                @Override
                public void onDeleteEntryFail() {
                    Toast.makeText(getContext(), "Error removing entry", Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            try {
                JournalEntryRoom journalEntryRoom = new JournalEntryRoom(entry.getEntryTitle(), entry.getEntryBody(),
                        new Date(entry.getEntryDate()));
                journalEntryRoom.setId(Integer.valueOf(entry.getUUID()));

                entryLocalDB.entryDAO().deleteEntry(journalEntryRoom);

                refreshUi();
            }catch (Exception e){
                Log.i(TAG, "Error removing entry");
                Toast.makeText(getContext(), "Error removing entry", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onChanged(@Nullable List<JournalEntryRoom> journalEntryRooms) {
        journalEntryRoomList = journalEntryRooms;
        journalEntryList.clear();

        for(JournalEntryRoom journalEntryRoom : journalEntryRooms){
            journalEntryList.add(new JournalEntry(journalEntryRoom));
        }

        refreshUi();
    }

    @Override
    public void onJournalEntryAdded(JournalEntry entry) {
        journalEntryList.add(0, entry);
        refreshUi();
    }

    @Override
    public void onJournalEntryModified(JournalEntry entry, String positionOnLise) {
        if(journalEntryList.contains(entry)) {
            journalEntryList.remove(entry);
            journalEntryList.add(entry);
        }

        refreshUi();
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

    @Override
    public void onJournalEntryFetchFailed() {

        Log.i(TAG, "firebase initialized failed");
    }

    @Override
    public void onStart() {
        super.onStart();

        retrieveEntries();
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
