package fr.ecp.sio.superchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * Activity to post tweets.
 * Created by Michaël on 12/12/2014.
 */
public class PostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
    }

    public void post(View view) {
        EditText contentText = (EditText) findViewById(R.id.content);
        String content = contentText.getText().toString();

        if (content.isEmpty()) {
            contentText.setError(getString(R.string.required));
            contentText.requestFocus();
            return;
        }

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String content = params[0];
                    String handle = AccountManager.getUserHandle(PostActivity.this);
                    String token = AccountManager.getUserToken(PostActivity.this);
                    new ApiClient().postTweet(handle, token, content);
                    return true;
                } catch (IOException e) {
                    Log.e(PostActivity.class.getName(), "Post failed", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    finish();
                    Toast.makeText(PostActivity.this, R.string.post_tweet_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostActivity.this, R.string.post_tweet_success, Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(content);

    }
}
