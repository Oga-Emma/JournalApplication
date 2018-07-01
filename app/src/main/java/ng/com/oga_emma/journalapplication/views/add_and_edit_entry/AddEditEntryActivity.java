package ng.com.oga_emma.journalapplication.views.add_and_edit_entry;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.model.JournalEntryRoom;

public class AddEditEntryActivity extends AppCompatActivity {

    public static final String ENTRY_EXTRA = "enry_extra";
    private JournalEntryRoom mEntry;

    public static void launchActivity(Context context, JournalEntryRoom entry){
        Intent intent = new Intent(context, AddEditEntryActivity.class);
        intent.putExtra(ENTRY_EXTRA, entry);


        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_entry_activity);

        setTitle(getString(R.string.view_journal_activity_title));

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AddEditFragment mFragment;

        if(savedInstanceState != null && savedInstanceState.containsKey(ENTRY_EXTRA)){
            mEntry = savedInstanceState.getParcelable(ENTRY_EXTRA);
            mFragment = AddEditFragment.newInstance(mEntry);

        }else if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(ENTRY_EXTRA)){
            mEntry = getIntent().getExtras().getParcelable(ENTRY_EXTRA);
            mFragment = AddEditFragment.newInstance(mEntry);
        }else{
            mFragment = AddEditFragment.newInstance(null);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mEntry != null)
            outState.putParcelable(ENTRY_EXTRA, mEntry);
    }
}
