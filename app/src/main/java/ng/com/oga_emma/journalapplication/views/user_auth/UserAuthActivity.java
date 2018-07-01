package ng.com.oga_emma.journalapplication.views.user_auth;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ng.com.oga_emma.journalapplication.MainActivity;
import ng.com.oga_emma.journalapplication.R;
import ng.com.oga_emma.journalapplication.utils.SharePreferenceKeys;
import ng.com.oga_emma.journalapplication.utils.SigninMode;

public class UserAuthActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        findViewById(R.id.google_signin_button).setOnClickListener(this);
        findViewById(R.id.ananoymous_sign_in_btn).setOnClickListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser() != null || PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SharePreferenceKeys.USER_SIGNED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));

            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.google_signin_button){
            startActivity(new Intent(UserAuthActivity.this, GoogleSigninActivity.class));

        }else if(view.getId() == R.id.ananoymous_sign_in_btn){
            MainActivity.launchActivity(UserAuthActivity.this);
            finish();
        }

    }
}
