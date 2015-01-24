package fr.ecp.sio.superchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import fr.ecp.sio.superchat.model.User;

/**
 *
 * Created by Michaël on 05/12/2014.
 */
public class UsersAdapter extends BaseAdapter implements DialogInterface.OnShowListener{
    private static final int REQUEST_LOGIN_FOR_POST = 1;
    private List<User> mUsers;

    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        }

        final User user = getItem(position);

        TextView handleView = (TextView) convertView.findViewById(R.id.handle);
        handleView.setText(user.getHandle());

        TextView statusView = (TextView) convertView.findViewById(R.id.status);
        switch (user.getStatus()) {
            case "online": statusView.setText(R.string.online); break;
            case "offline": statusView.setText(R.string.offline); break;
            default: statusView.setText("");
        }

        //Text for follower : follows user
        final TextView isFollowerView = (TextView) convertView.findViewById(R.id.isFollower);

        //Text for following : followed by user
        final TextView isFollowingView = (TextView) convertView.findViewById(R.id.isFollowing);

        //Buttton to get Followers
        ImageButton followersButton = (ImageButton) convertView.findViewById(R.id.followers_list);
        //Button to get Followings
        ImageButton followingsButton = (ImageButton) convertView.findViewById(R.id.followings_list);
        //Button to post followings action : add/delete following
        final ImageButton postFollowingButton = (ImageButton) convertView.findViewById(R.id.post_followings);

        //
        if(AccountManager.isConnected(parent.getContext()) && !user.getHandle().equals(AccountManager.getUserHandle(parent.getContext()))) {
             ManageFollowTaskRunner  aF = new ManageFollowTaskRunner(AccountManager.getUserHandle(parent.getContext()), user.getHandle(),postFollowingButton);
             aF.execute(isFollowerView,isFollowingView);
        }
        else {
            isFollowerView.setVisibility(View.INVISIBLE);
            isFollowingView.setVisibility(View.INVISIBLE);
        }

        //Buttton to get Followers
        //ImageButton followersButton = (ImageButton) convertView.findViewById(R.id.followers_list);
        followersButton.setTag(position);
        followersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowers(parent.getContext(), (int) v.getTag());
            }
        });

        //Button to get Followings
        // ImageButton followingsButton = (ImageButton) convertView.findViewById(R.id.followings_list);
        followingsButton.setTag(position);
        followingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowings(parent.getContext(),(int) v.getTag());
            }
        });

        //set the clickListener to add or delete following of the logger user
        postFollowingButton.setTag(position);
        if((AccountManager.isConnected(parent.getContext()) && user.getHandle().equals(AccountManager.getUserHandle(parent.getContext())))){
            convertView.setBackgroundColor(Color.YELLOW);
        }
        else{
            convertView.setBackgroundColor(Color.WHITE);
        }
        if(!AccountManager.isConnected(parent.getContext()) || (AccountManager.isConnected(parent.getContext()) && user.getHandle().equals(AccountManager.getUserHandle(parent.getContext())))) {
            postFollowingButton.setVisibility(View.INVISIBLE);

        }
        else{
            postFollowingButton.setVisibility(View.VISIBLE);
            //Click listener to update following relation
            postFollowingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postFollowing(parent.getContext(),(int)v.getTag());

                   ManageFollowTaskRunner  aF = new ManageFollowTaskRunner(AccountManager.getUserHandle(parent.getContext()), user.getHandle(),postFollowingButton);
                    aF.execute(isFollowerView,isFollowingView);
                }
            });
        }

        ImageView profilePictureView = (ImageView) convertView.findViewById(R.id.profile_picture);
        Picasso.with(convertView.getContext()).load(user.getProfilePicture()).into(profilePictureView);
        return convertView;
    }

    /**
     * Assync task to update follower and following.
     */
    private class ManageFollowTaskRunner extends AsyncTask<TextView, Void, Boolean> {
        TextView t0;
        TextView t1;
        ImageButton imgBtn;

        private final  String handle ;
        private final String handleToCheck;

        private boolean isFollower;
        private boolean isFollowing;

        protected ManageFollowTaskRunner(String handle, String handleToCheck,ImageButton imgBtn) {
            super();
            this.handle = handle;
            this.handleToCheck = handleToCheck;
            this.imgBtn = imgBtn;
        }


        @Override
        protected Boolean doInBackground(TextView... params) {
            try {
                t0 = params[0];
                t1 = params[1];
                 this.isFollower = new ApiClient().isFollower(handle, handleToCheck);

            } catch (IOException e) {
                Log.e(UsersAdapter.class.getName(), "get failed", e);
                return false;
            }

            //
            try {
                this.isFollowing = new ApiClient().isFollowing(handle,handleToCheck);
            }
            catch (IOException e) {
                Log.e(UsersAdapter.class.getName(), "get failed", e);
                return false;

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(!result){
                t0.setVisibility(View.VISIBLE);
                t0.setText("follows " + "Error");
                t1.setVisibility(View.VISIBLE);
                t1.setText("is followed by " + "Error");
            }
            else{
                if (isFollower) {
                    t0.setText("follows " + handle);
                    t0.setVisibility(View.VISIBLE);
                } else {
                    t0.setVisibility(View.INVISIBLE);
                }
                if (isFollowing) {
                    Log.d(UsersAdapter.class.getName(), "IS FOLLOWING TRUE", null);
                    imgBtn.setImageResource(R.drawable.ic_person_remove_white_36dp);
                    imgBtn.setVisibility(View.VISIBLE);
                    t1.setText("is followed by " + handle);
                    t1.setVisibility(View.VISIBLE);
                }else{
                    imgBtn.setImageResource(R.drawable.ic_person_add_white_36dp);
                    imgBtn.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.INVISIBLE);
                }

            }

        }
    }

     /**
     * Display the list of followers.
     */
    private void getFollowers(Context context, int position){
        Intent intent = new Intent(context, FollowersActivity.class);
        User user = getItem(position);
        intent.putExtras(FollowersFragment.newArguments(user));
        context.startActivity(intent);
        //
    }

    /**
     * Display the list of followings
     */
    private void getFollowings(Context context, int position){
        Intent intent = new Intent(context, FollowingsActivity.class);
        User user = getItem(position);
        intent.putExtras(FollowingsFragment.newArguments(user));
        context.startActivity(intent);
    }

    /**
     * Manage following. Done in background.
     * @param context Current context.
     * @param position Item position.
     * @return true if following update is succesfull.
     */
    private boolean postFollowing(final Context context, int position) {
        User user = getItem(position);
        FragmentActivity activity = (FragmentActivity)(context);
        if (AccountManager.isConnected(context)) {
                String content = user.getHandle();
                String handle = AccountManager.getUserHandle(context);
                String token = AccountManager.getUserToken(context);

                new AsyncTask<String,Void,String>(){

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            new ApiClient().manageFollowing(params[0],params[1],params[2]);
                        } catch (IOException e) {
                            Log.e(UsersAdapter.class.getName(), "get failed", e);
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Toast.makeText(context, "followings updated", Toast.LENGTH_SHORT).show();
                    }
                }.execute(handle,token,content);

                notifyDataSetChanged();
                return true;
        } else {//Display the Login dialog
            LoginFragment fragment = new LoginFragment();
            FragmentManager fm = activity.getSupportFragmentManager();
            fragment.show(fm, "login_dialog");
            //TODO REFRESH
            notifyDataSetChanged();
            return false;
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }
}
