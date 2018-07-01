package ng.com.oga_emma.journalapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class JournalApplication extends Application{

    public static JournalApplication application;

    public static JournalApplication getInstance(){
        if (application == null)
            application = new JournalApplication();

        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
