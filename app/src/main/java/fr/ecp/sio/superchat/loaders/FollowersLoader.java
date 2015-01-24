package fr.ecp.sio.superchat.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import java.io.IOException;
import java.util.List;

import fr.ecp.sio.superchat.ApiClient;
import fr.ecp.sio.superchat.model.User;

/** Thread to get the list of followers.
 * Created by jmikolaj on 14/01/15.
 */
public class FollowersLoader extends AsyncTaskLoader<List<User>>{

    private String mHandle;
    private List<User> mResult;

    public FollowersLoader(Context context, String handle) {
        super(context);
        mHandle=handle;
    }

    @Override
    public List<User> loadInBackground() {
        try {
            return new ApiClient().getFollowers(mHandle);
        } catch (IOException e) {
            Log.e(UsersLoader.class.getName(), "Failed to download followers", e);
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
    public void deliverResult(List<User> data) {
        Log.i(FollowersLoader.class.getName(), "Returned data: " + data);
        mResult = data;
        super.deliverResult(data);
    }

}
