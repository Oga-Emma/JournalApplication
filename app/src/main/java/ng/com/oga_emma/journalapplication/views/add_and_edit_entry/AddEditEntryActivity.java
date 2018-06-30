package ng.com.oga_emma.journalapplication.views.add_and_edit_entry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ng.com.oga_emma.journalapplication.R;

public class AddEditEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_entry_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new AddEditFragment())
                .commit();

        setTitle("New Journal Entry");
    }
}
