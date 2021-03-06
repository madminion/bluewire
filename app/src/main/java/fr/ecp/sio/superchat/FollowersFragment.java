package fr.ecp.sio.superchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;


import fr.ecp.sio.superchat.loaders.FollowersLoader;

import fr.ecp.sio.superchat.model.User;

/**
* Fragment which contains the list of Followers.
*/
public class FollowersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<User>> {

    private static final int LOADER_FOLLOWERS = 1000;

    private static final String ARG_USER = "user";

    private User mUser;
    private UsersAdapter mListAdapter;
    private boolean mIsMasterDetailsMode;

    public static Bundle newArguments(User user) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();//JM test
        mUser = getArguments().getParcelable(ARG_USER);
    }

    /**
     * JM
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.followers_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new UsersAdapter();
        getListView().setDividerHeight(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsMasterDetailsMode = getActivity().findViewById(R.id.tweets_content) != null;
        if (mIsMasterDetailsMode) {
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
        if (getActivity() instanceof FollowersActivity) {
            getActivity().setTitle("Followers of " + mUser.getHandle());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_FOLLOWERS, null, this);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new FollowersLoader(getActivity(), mUser.getHandle());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> followers) {
        mListAdapter.setUsers(followers);
        setListAdapter(mListAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) { }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = mListAdapter.getItem(position);
        //JM TO DISPLAY TWEETS
        if (mIsMasterDetailsMode) {
            Fragment tweetsFragment = new TweetsFragment();
            tweetsFragment.setArguments(TweetsFragment.newArguments(user));
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.tweets_content, tweetsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), TweetsActivity.class);
            intent.putExtras(TweetsFragment.newArguments(user));
            startActivity(intent);
        }
    }

}
