package ng.com.oga_emma.journalapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ng.com.oga_emma.journalapplication.utils.SharePreferenceKeys;
import ng.com.oga_emma.journalapplication.views.add_and_edit_entry.AddEditEntryActivity;
import ng.com.oga_emma.journalapplication.views.entry_list.JournalEntriesFragment;
import ng.com.oga_emma.journalapplication.views.user_auth.UserAuthActivity;

public class MainActivity extends AppCompatActivity{

    public static final String NEW_SIGN_IN = "new_sign_in";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private ProgressDialog mSigninOutDialog;

    public static void launchActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NEW_SIGN_IN, true);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFAB();

        boolean newSignIn = false;

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) initFirebase();

        mSigninOutDialog = new ProgressDialog(this);
        mSigninOutDialog.setCancelable(false);
        mSigninOutDialog.setMessage(getString(R.string.signing_out_message));

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(NEW_SIGN_IN))
            newSignIn = extras.getBoolean(NEW_SIGN_IN);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, JournalEntriesFragment.newInstance(newSignIn))
                .commit();
    }

    private void initFirebase() {

        //for offline data persisitence

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddEditEntryActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_logout) {

            mSigninOutDialog.show();

            if(mAuth.getCurrentUser() == null) {

                mSigninOutDialog.dismiss();

                startActivity(new Intent(this, UserAuthActivity.class));
                finish();
            }else{
                //firebase signout
                signOut();
            }

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putBoolean(SharePreferenceKeys.USER_SIGNED_IN, false)
                    .apply();

            return true;
        }else if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {

                    mSigninOutDialog.dismiss();

                    startActivity(new Intent(MainActivity.this, UserAuthActivity.class));

                    // Google sign out
                    mGoogleSignInClient.revokeAccess();
                    finish();
                });

    }
}
