package fr.ecp.sio.superchat.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

import fr.ecp.sio.superchat.ApiClient;
import fr.ecp.sio.superchat.model.Tweet;

/** Thread to get the list of tweets.
 * Created by Michaël on 05/12/2014.
 */
public class TweetsLoader extends AsyncTaskLoader<List<Tweet>> {

    private String mHandle;
    private List<Tweet> mResult;

    public TweetsLoader(Context context, String handle) {
        super(context);
        mHandle = handle;
    }

    @Override
    public List<Tweet> loadInBackground() {
        try {
            return new ApiClient().getUserTweets(mHandle);
        } catch (Exception e) {
            Log.e(TweetsLoader.class.getName(), "Failed to download tweets", e);
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mResult != null){
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public void deliverResult(List<Tweet> data) {
        Log.i(TweetsLoader.class.getName(), "Returned data: " + data);
        mResult = data;
        super.deliverResult(data);
    }

}