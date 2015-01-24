package fr.ecp.sio.superchat.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import fr.ecp.sio.superchat.ApiClient;
import fr.ecp.sio.superchat.model.User;

/** Thread to get the list of Followings.
 * Created by jmikolaj on 14/01/15.
 */
public class FollowingsLoader  extends AsyncTaskLoader<List<User>> {

    private String mHandle;
    private List<User> mResult;

    public FollowingsLoader(Context context,String handle) {
        super(context);
        mHandle=handle;
    }

    @Override
    public List<User> loadInBackground() {
        try {
            return new ApiClient().getFollowings(mHandle);
        } catch (IOException e) {
            Log.e(UsersLoader.class.getName(), "Failed to download followings", e);
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
