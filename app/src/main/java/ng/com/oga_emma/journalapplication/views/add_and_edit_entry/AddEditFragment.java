package ng.com.oga_emma.journalapplication.views.add_and_edit_entry;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

import ng.com.oga_emma.journalapplication.MainActivity;
import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;
import ng.com.oga_emma.journalapplication.database.JournalEntryFirebaseDB;
import ng.com.oga_emma.journalapplication.interfaces.AddEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;
import ng.com.oga_emma.journalapplication.utils.FirebaseStringUtils;
import ng.com.oga_emma.journalapplication.utils.SharePreferenceKeys;
import ng.com.oga_emma.journalapplication.utils.SigninMode;

public class AddEditFragment extends Fragment {

    private TextView dateTextView;
    private EditText entryTitleEditText, entryBodyEditText;
    private Button saveEntryButon;
    private JournalEntryRoom entry;


    public AddEditFragment() {
        // Required empty public constructor
    }


    public static AddEditFragment newInstance(JournalEntryRoom entry) {
        AddEditFragment fragment = new AddEditFragment();

        if(null != entry){
            fragment = new AddEditFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AddEditEntryActivity.ENTRY_EXTRA, entry);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_edit, container, false);

        dateTextView = v.findViewById(R.id.current_date_tv);
        entryTitleEditText = v.findViewById(R.id.entry_title_edit_text);
        entryBodyEditText = v.findViewById(R.id.entry_edit_text);
        saveEntryButon = v.findViewById(R.id.save_entry_btn);
        saveEntryButon.setOnClickListener(saveEntryClickListener);

        initViews();

        return v;
    }

    private void initViews() {
        if(getArguments() != null && getArguments().containsKey(AddEditEntryActivity.ENTRY_EXTRA)){
            entry = getArguments().getParcelable(AddEditEntryActivity.ENTRY_EXTRA);
        }

        if(entry != null){
            Date date = entry.getEntryDate();
            SimpleDateFormat dt1 = new SimpleDateFormat("EE dd-MMM-yyyy");
            dateTextView.setText(dt1.format(date));

            entryTitleEditText.setText(entry.getEntryTitle());
            entryBodyEditText.setText(entry.getEntryBody());

        }else{
            Date date = new Date();
            SimpleDateFormat dt1 = new SimpleDateFormat("EE dd-MMM-yyyy");
            dateTextView.setText(dt1.format(date));

            entryTitleEditText.setText("");
            entryBodyEditText.setText("");
        }

    }

    private View.OnClickListener saveEntryClickListener =
            new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String title = entryTitleEditText.getText().toString();
            String body = entryBodyEditText.getText().toString();
            Date date = new Date();

            if(!entryBodyEditText.getText().toString().isEmpty()){

                if(title.isEmpty()) title = "No title";

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if(entry != null){

                        JournalEntry journalEntry = new JournalEntry();

                        journalEntry.setEntryTitle(entry.getEntryTitle());
                        journalEntry.setEntryBody(body);
                        journalEntry.setEntryDate(date.getTime());
                        journalEntry.setUUID(entry.getEntryId());

                        JournalEntryFirebaseDB.getInstance(FirebaseAuth.getInstance().getUid())
                                .updateJournalEntry(journalEntry, new AddEntry.UpdateEntryListener() {
                                    @Override
                                    public void onUpdateEntrySuccess() {
                                        Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onUpdateEntryFail() {
                                        Toast.makeText(getContext(), "Error saving entry", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    JournalEntryFirebaseDB.getInstance(FirebaseAuth.getInstance().getUid())
                            .addJournalEntry(new JournalEntry(title, body, date), new AddEntry.AddEntryListener() {
                                @Override
                                public void onEntryAddSuccess() {
                                    Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }

                                @Override
                                public void onEntryAddFail() {
                                    Toast.makeText(getContext(), "Error saving entry", Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {

                    if(entry != null){

                        entry.setEntryTitle(title);
                        entry.setEntryBody(body);
                        entry.setEntryDate(date);

                        JounalEntryLocalStroage.getInstance(getContext().getApplicationContext())
                                .entryDAO().updateEntry(entry);

                        Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();
                        getActivity().finish();

                    }else {
                        JounalEntryLocalStroage.getInstance(getContext().getApplicationContext())
                                .entryDAO().insertEntry(new JournalEntryRoom(title, body, date));

                        Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }

            }else{
                Toast.makeText(getContext(), "You have not made any entry", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
