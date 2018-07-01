package ng.com.oga_emma.journalapplication.views.add_and_edit_entry;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.dao.EntryDAO;
import ng.com.oga_emma.journalapplication.database.JounalEntryLocalStroage;
import ng.com.oga_emma.journalapplication.database.JournalEntryFirebaseDB;
import ng.com.oga_emma.journalapplication.interfaces.AddEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

public class AddEditFragment extends Fragment {

    private TextView mDateTextView;
    private EditText mEntryTitleEditText, mEntryBodyEditText;
    private Button mSaveEntryButon;
    private JournalEntryRoom mEntry;


    public AddEditFragment() {
        // Required empty public constructor
    }

    //returns a new instance of AddEditFragmentActivity
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mDateTextView = v.findViewById(R.id.current_date_tv);
        mEntryTitleEditText = v.findViewById(R.id.entry_title_edit_text);
        mEntryBodyEditText = v.findViewById(R.id.entry_edit_text);
        mSaveEntryButon = v.findViewById(R.id.save_entry_btn);
        mSaveEntryButon.setOnClickListener(saveEntryClickListener);

        initViews();

        return v;
    }

    //used to initialize views
    private void initViews() {
        if(getArguments() != null && getArguments().containsKey(AddEditEntryActivity.ENTRY_EXTRA)){
            mEntry = getArguments().getParcelable(AddEditEntryActivity.ENTRY_EXTRA);
        }

        if(mEntry != null){

            Date date = mEntry.getEntryDate();
            SimpleDateFormat dt1 = new SimpleDateFormat("EE dd-MMM-yyyy", Locale.getDefault());
            mDateTextView.setText(dt1.format(date));

            mEntryTitleEditText.setText(mEntry.getEntryTitle());
            mEntryBodyEditText.setText(mEntry.getEntryBody());

        }else{
            Date date = new Date();
            SimpleDateFormat dt1 = new SimpleDateFormat("EE dd-MMM-yyyy", Locale.getDefault());
            mDateTextView.setText(dt1.format(date));

            mEntryTitleEditText.setText("");
            mEntryBodyEditText.setText("");
        }

    }

    private View.OnClickListener saveEntryClickListener =
            new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String title = mEntryTitleEditText.getText().toString();
            String body = mEntryBodyEditText.getText().toString();
            Date date = new Date();

            if(!mEntryBodyEditText.getText().toString().isEmpty()){

                if(title.isEmpty()) title = getString(R.string.no_title_placeholder);

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if(mEntry != null){

                        JournalEntry journalEntry = new JournalEntry();

                        journalEntry.setEntryTitle(mEntry.getEntryTitle());
                        journalEntry.setEntryBody(body);
                        journalEntry.setEntryDate(date.getTime());
                        journalEntry.setUUID(mEntry.getEntryId());

                        JournalEntryFirebaseDB.getInstance(FirebaseAuth.getInstance().getUid())
                                .updateJournalEntry(journalEntry, new AddEntry.UpdateEntryListener() {
                                    @Override
                                    public void onUpdateEntrySuccess() {
                                        Toast.makeText(getContext(), R.string.entry_saved_msg, Toast.LENGTH_SHORT).show();
                                        Objects.requireNonNull(getActivity()).finish();
                                    }

                                    @Override
                                    public void onUpdateEntryFail() {
                                        Toast.makeText(getContext(), R.string.error_saving_entry_msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {

                        JournalEntryFirebaseDB.getInstance(FirebaseAuth.getInstance().getUid())
                                .addJournalEntry(new JournalEntry(title, body, date), new AddEntry.AddEntryListener() {
                                    @Override
                                    public void onEntryAddSuccess() {
                                        Toast.makeText(getContext(), R.string.entry_saved_msg, Toast.LENGTH_SHORT).show();
                                        Objects.requireNonNull(getActivity()).finish();
                                    }

                                    @Override
                                    public void onEntryAddFail() {
                                        Toast.makeText(getContext(), R.string.error_saving_entry_msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }else {

                    EntryDAO entryDAO = JounalEntryLocalStroage.getInstance(Objects.requireNonNull(getContext()).
                            getApplicationContext()).entryDAO();

                    if(mEntry != null){

                        mEntry.setEntryTitle(title);
                        mEntry.setEntryBody(body);
                        mEntry.setEntryDate(date);

                        entryDAO.updateEntry(mEntry);
                        Objects.requireNonNull(getActivity()).finish();

                    }else {

                        entryDAO.insertEntry(new JournalEntryRoom(title, body, date));
                        Objects.requireNonNull(getActivity()).finish();
                    }

                    Toast.makeText(getContext(), R.string.entry_saved_msg, Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getContext(), R.string.cannot_save_empt_document_error_label, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
