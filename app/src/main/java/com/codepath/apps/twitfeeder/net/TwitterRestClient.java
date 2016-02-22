package com.codepath.apps.twitfeeder.net;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterRestClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "DPxQCvZTs9pdRHfWflhz8gG7g";       // Change this
	public static final String REST_CONSUMER_SECRET = "2DNzk25nT5dkLUA5Viyuw5qLX9MiMy2p7JO1uWvveZWNrPUlaI"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptwitfeeder"; // Change this (here and in manifest)

    public static final String REST_UPDATE_STATUS_URL = "statuses/update.json";
    public static final String REST_DETAIL_TWEET_URL = "statuses/show.json";
    public static final String REST_FIND_TWEET_URL = "search/tweets.json";
    public static final String REST_ADD_FAVORITES_URL = "favorites/create.json";
    public static final String REST_REMOVE_FAVORITES_URL = "favorites/destroy.json";
    public static final String REST_VERIFY_CREDENTIALS_URL = "account/verify_credentials.json";

	public TwitterRestClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/home_timeline.json");

        //add params
        RequestParams params = new RequestParams();
        params.put("count", 20);
        if(since_id > 0) {
            params.put("since_id", since_id);
        }
        if(max_id > 0) {
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void searchTweets(){

    }
    public void addFavoriteTweet(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_ADD_FAVORITES_URL);
        RequestParams params = new RequestParams();
        if (tweetId > 0) {
            params.put("id", tweetId);
        }
        client.post(apiUrl, params, handler);
    }

    public void removeFavoriteTweet(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_REMOVE_FAVORITES_URL);
        RequestParams params = new RequestParams();
        if (tweetId > 0) {
            params.put("id", tweetId);
        }
        client.post(apiUrl, params, handler);
    }

    public void updateStatus(long tweetId, String status, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_UPDATE_STATUS_URL);
        RequestParams params = new RequestParams();
        if (tweetId > 0) {
            Log.i("info","replying to a post");
            params.put("in_reply_to_status_id", tweetId);
        }
        params.put("status", status);
        client.post(apiUrl, params, handler);
    }

    public void getTweetDetails(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_DETAIL_TWEET_URL);
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.get(apiUrl, params, handler);
    }

    public void verifyCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_VERIFY_CREDENTIALS_URL);
        client.get(apiUrl, handler);
    }

}