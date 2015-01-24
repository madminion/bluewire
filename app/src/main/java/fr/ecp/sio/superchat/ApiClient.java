package fr.ecp.sio.superchat;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import fr.ecp.sio.superchat.model.Tweet;
import fr.ecp.sio.superchat.model.User;

/**
 * Created by Michaël on 12/12/2014.
 */
public class ApiClient {
    /**
     * Hardcoded Url of the rest Server.
     */
    private static final String API_BASE = "http://hackndo.com:5667/mongo/";

    /**
     * To check login.
     * @param handle
     * @param password
     * @return token.
     * @throws IOException
     */
    public String login(String handle, String password) throws IOException {
        String url = Uri.parse(API_BASE + "session").buildUpon()
                .appendQueryParameter("handle", handle)
                .appendQueryParameter("password", password)
                .build().toString();
        Log.i(ApiClient.class.getName(), "Login: " + url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Connection", "close");
        InputStream stream = connection.getInputStream();
        String response = IOUtils.toString(stream);
        stream.close();
        return response;
    }


    /**
     * To get the list of users.
     * @param token Connection authorisation token.
     * @return The lis of Users.
     * @throws IOException
     */
    public List<User> getUsers(String token) throws IOException {
        Log.d(ApiClient.class.getName(), "Get Users : "+API_BASE + "users/");
        if(token == null || token.trim().isEmpty()) {
            InputStream stream = new URL(API_BASE + "users/").openStream();
            String response = IOUtils.toString(stream);
            stream.close();
            return Arrays.asList(new Gson().fromJson(response, User[].class));
        }
        else{//WITH TOKEN IN REQUEST PARAMETER, GET THE FOLLOWINGS
            String url = Uri.parse(API_BASE + "users/").buildUpon()
                    .build().toString();
            Log.i(ApiClient.class.getName(), "getUSer With Authentification : " + url + ": token " + token);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Authorization", "Bearer-" + token);
            InputStream stream = connection.getInputStream();
            String response = IOUtils.toString(stream);
            stream.close();
            Log.i(ApiClient.class.getName(), "response "+ response);
            return Arrays.asList(new Gson().fromJson(response, User[].class));
        }
    }

    /**
     * To get Tweets.
     * @param handle Tweets handle.
     * @return The list of tweets.
     * @throws IOException
     */
    public List<Tweet> getUserTweets(String handle) throws IOException {
        Log.d(ApiClient.class.getName(), "Get tweets : "+API_BASE + handle + "/tweets/");
        InputStream stream = new URL(API_BASE + handle + "/tweets/").openStream();
        String response = IOUtils.toString(stream);
        stream.close();
        return Arrays.asList(new Gson().fromJson(response, Tweet[].class));
    }

    /**
     * To post a tweet.
     * @param handle Tweet handle.
     * @param token Authorization token.
     * @param content tweet content.
     * @throws IOException
     */
    public void postTweet(String handle, String token, String content) throws IOException {
        String url = Uri.parse(API_BASE + handle + "/tweets/post/").buildUpon()
                .appendQueryParameter("content", content)
                .build().toString();
        Log.i(ApiClient.class.getName(), "Post tweet: " + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer-" + token);
        connection.getInputStream();
    }

    /**
     * To get the list of followers.
     * @param handle Tweets Handle.
     * @return The list of followers.
     * @throws IOException
     */
    public List<User> getFollowers(String handle) throws IOException {
        Log.d(ApiClient.class.getName(), "Get Followers url : "+API_BASE + handle + "/followers/");
        InputStream stream = new URL(API_BASE + handle + "/followers/").openStream();
        String response = IOUtils.toString(stream);
        stream.close();
        return Arrays.asList(new Gson().fromJson(response, User[].class));
    }

    /**
     * To get the list of followings.
     * @param handle Tweets handle.
     * @return The list of followings.
     * @throws IOException
     */
    public List<User> getFollowings(String handle) throws IOException {
        Log.d(ApiClient.class.getName(), "Get Followings url : "+API_BASE + handle + "/followings/");
        InputStream stream = new URL(API_BASE + handle + "/followings/").openStream();
        String response = IOUtils.toString(stream);
        return Arrays.asList(new Gson().fromJson(response, User[].class));
    }


    /**
     * To manage a following. Delete the following relation if it is in the list, else add to the following list.
     *
     * @param handle Tweets handle.
     * @param token Authorization token.
     * @param followingToManage Handle of the following to Manage.
     * @throws IOException
     */
    public void manageFollowing(String handle, String token, String followingToManage)  throws IOException {
        List<User> followings = this.getFollowings(handle);
        if (followings == null || followings.isEmpty()){
            this.addFollowing(handle, token, followingToManage);
        }
        else{
            boolean addFollowing = true;
            //Check if followingToManage is in the list
            for(User following : followings){
                if(following.getHandle().equals(followingToManage)){
                    this.deleteFollowing(handle, token, followingToManage);
                    addFollowing=false;
                    break;
                }
                else{
                    continue;
                }
            }
            //
            if(addFollowing) {
                this.addFollowing(handle, token, followingToManage);
            }
        }
    }

    /**
     * Add following
     * /:handle/followers/post/?handle=follower
     * @param handle Tweets handle.
     * @param token Authorization token.
     * @param following Following handle to add to the following list.
     * @throws IOException
     */
    public void addFollowing(String handle, String token, String following) throws IOException {
        String url = Uri.parse(API_BASE + handle + "/followings/post/").buildUpon()
                .appendQueryParameter("handle", following)
                .build().toString();
        Log.d(ApiClient.class.getName(), "Post add Following url : "+url + " Authorization, Bearer-" + token);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer-" + token);
        connection.getInputStream();
    }

    /**
     * Delete following.
     * /:handle/followers/delete/?handle=follower
     * @param handle Tweets handle.
     * @param following Following handle to delete from the following list.
     * @throws IOException
     */
    public void deleteFollowing(String handle,String token, String following) throws IOException {
        String url = Uri.parse(API_BASE + handle + "/followings/delete/").buildUpon()
                .appendQueryParameter("handle", following)
                .build().toString();
        Log.d(ApiClient.class.getName(), "Post Delete following : "+url + " Authorization, Bearer-" + token);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer-" + token);
        connection.getInputStream();
    }

    /**
     * To check if a user is in the followers list.
     * @param handle Tweets handle.
     * @param followerHandleToCheck Follower to check.
     * @return true if followerHandleToCheck is a follower.
     * @throws IOException
     */
    public boolean isFollower(String handle, String followerHandleToCheck) throws IOException {
        List<User> followers = this.getFollowers(handle);
        if (followers == null || followers.isEmpty()){
            return false;
        }
        else{
            //check if follower is in the list
            for(User follower : followers){
                if(follower.getHandle().equals(followerHandleToCheck)){
                    return true;
                }
                else{
                    continue;
                }
            }
            return false;
        }
    }

    /**
     * To check if a user is in the followings list.
     * @param handle Tweets handle.
     * @param followingHandleToCheck Following to check.
     * @return true if followingHandleToCheck is a following.
     * @throws IOException
     */
    public boolean isFollowing(String handle, String followingHandleToCheck) throws IOException {
        List<User> followings = this.getFollowings(handle);
        if (followings == null || followings.isEmpty()){
           return false;
        }
        else{
            //check if following is in the list
            for(User following : followings){
                if(following.getHandle().equals(followingHandleToCheck)){
                    return true;
                }
                else{
                    continue;
                }
            }
            return false;
        }
    }

}