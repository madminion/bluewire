package fr.ecp.sio.superchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity. Display the UsersFragment.
 * Created by Michaël on 05/12/2014.
 * @author jmikolaj
 */
public class MainActivity extends ActionBarActivity {
    static final int REQUEST_CODE_RESUME_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if(id==R.id.action_connect){
           showLoginDialog();
            return true;
        }
        else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * To reload the Main Activity.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //refresh after change in Preferences
        if (requestCode == REQUEST_CODE_RESUME_ACTIVITY) {
            finish(); // "refresh" code
            startActivity(getIntent());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(AccountManager.isConnected(this)){
            MenuItem item = menu.findItem(R.id.action_connect);
            item.setEnabled(false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * To Display Login Dialog from menu option.
     */
    private void showLoginDialog() {
        LoginFragment fragment = new LoginFragment();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm, "login_dialog");

        //to reload activity
        if(AccountManager.isConnected(this)) {
            finish();
            startActivity(getIntent());
        }
        else{
            //do nothing
        }
    }

    /**
     * To Display help Dialog from menu option.
     */
    private void showHelpDialog() {
        HelpFragment fragment = new HelpFragment();
        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm, "help_dialog");
    }

}
