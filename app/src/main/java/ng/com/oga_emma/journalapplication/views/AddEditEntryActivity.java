package ng.com.oga_emma.journalapplication.views;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

        setTitle("New Entry");
    }
}
