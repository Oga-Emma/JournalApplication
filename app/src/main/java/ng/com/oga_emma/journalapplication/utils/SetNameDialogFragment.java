package ng.com.oga_emma.journalapplication.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import ng.com.oga_emma.journalapplication.MainActivity;
import ng.com.oga_emma.journalapplication.views.JournalEntriesFragment;

public class SetNameDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Button[] positiveButton = {null};

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        final EditText edittext = new EditText(getContext());
        edittext.setSingleLine();
        edittext.setPadding(16, 16, 16, 16);

        alert.setMessage("Enter your name");
        alert.setTitle("Set display name");

        alert.setView(edittext);

        alert.setPositiveButton("Set name", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String displayName = edittext.getText().toString();

                if(!displayName.isEmpty()) {

                    Intent i = new Intent()
                            .putExtra(JournalEntriesFragment.USER_NAME_KEY, displayName.toUpperCase());
                    assert getTargetFragment() != null;
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    dismiss();
                }
            }
        });

        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                Intent i = new Intent();
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, i);
                dismiss();
            }
        });

        alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    getActivity().finish();
                return false;
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.setOnShowListener((onDialogShowListener) -> {
            positiveButton[0] = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton[0] != null) {
                positiveButton[0].setEnabled(false);
            }
        });

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1) {
                    if (positiveButton[0] != null)
                        positiveButton[0].setEnabled(false);
                } else {
                    positiveButton[0].setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return alertDialog;
    }

    public interface SetUserName{
        void OnUserNameSetListener(String name);
    }
}
