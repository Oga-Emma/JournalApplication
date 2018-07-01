package ng.com.oga_emma.journalapplication.views.add_and_edit_entry;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.model.JournalEntry;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

public class AddEditEntryActivity extends AppCompatActivity {

    public static final String ENTRY_EXTRA = "enry_extra";
    private JournalEntryRoom entry;

    public static void launchActivity(Context context, JournalEntryRoom entry){
        Intent intent = new Intent(context, AddEditEntryActivity.class);
        intent.putExtra(ENTRY_EXTRA, entry);


        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_entry_activity);

        setTitle("VIEW JOURNAL");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AddEditFragment fragment = null;

        if(savedInstanceState != null && savedInstanceState.containsKey(ENTRY_EXTRA)){
            entry = savedInstanceState.getParcelable(ENTRY_EXTRA);
            fragment = AddEditFragment.newInstance(entry);

        }else if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(ENTRY_EXTRA)){
            entry = getIntent().getExtras().getParcelable(ENTRY_EXTRA);
            fragment = AddEditFragment.newInstance(entry);
        }else{
            fragment = AddEditFragment.newInstance(null);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        setTitle("New Journal Entry");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(entry != null)
            outState.putParcelable(ENTRY_EXTRA, entry);
    }
}
