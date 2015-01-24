package fr.ecp.sio.superchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.FragmentManager;

/**
 *  Menu preferences.
 * Created by Michaël on 12/12/2014.
 *
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        //LOGOUT
        Preference logoutPref = findPreference("logout");
        logoutPref.setEnabled(AccountManager.isConnected(this));
        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.logout_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AccountManager.logout(SettingsActivity.this);
                                preference.setEnabled(false);
                                startActivityForResult(new Intent(SettingsActivity.this, MainActivity.class), MainActivity.REQUEST_CODE_RESUME_ACTIVITY );
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            }
        });
    }
}
