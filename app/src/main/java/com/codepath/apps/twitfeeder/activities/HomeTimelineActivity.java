package com.codepath.apps.twitfeeder.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.twitfeeder.R;
import com.codepath.apps.twitfeeder.adapters.TweetAdapter;
import com.codepath.apps.twitfeeder.fragments.ComposeNewTweetFragment;
import com.codepath.apps.twitfeeder.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitfeeder.models.Tweet;
import com.codepath.apps.twitfeeder.models.User;
import com.codepath.apps.twitfeeder.net.TwitApplication;
import com.codepath.apps.twitfeeder.net.TwitterRestClient;
import com.codepath.apps.twitfeeder.utils.ApplicationHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeTimelineActivity extends AppCompatActivity {

    private TwitterRestClient client;

    final static int REFRESH_OPERATION = 1;
    final static int SCROLL_OPERATION = 0;
    final static int COMPOSE = 0;
    final static int REPLY = 1;


    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @Bind(R.id.toolbar) Toolbar toolbar;


    TweetAdapter adapter;
    ArrayList<Tweet> fetchedTweets;
    long since_id, max_id;

    static User owner = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        ButterKnife.bind(this);
        ApplicationHelper.setContext(HomeTimelineActivity.this);
        client = TwitApplication.getRestClient();

        if(!ApplicationHelper.isNetworkAvailable(getApplicationContext()) || !ApplicationHelper.isOnline()){
            ApplicationHelper.showWarning(HomeTimelineActivity.this);
        } else {
            getUserCredentials();
        }

        setSupportActionBar(toolbar);
        //never setup back/up button on the home screen
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("@" +owner.getScreenName());
        getSupportActionBar().setTitle("Timeline");

        getSupportActionBar().setLogo(R.drawable.ic_twitter_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_action_composetweet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                showComposeTweetDialog(0, 0);
            }
        });


        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        fetchedTweets = new ArrayList<>();

        // Create adapter passing in the sample user data
        adapter = new TweetAdapter(this);

        adapter.setCustomObjectListener(new TweetAdapter.ActionButtonListener() {
            @Override
            public void onRemoveFavouriteButtonClicked(long Id, final int position) {
                client.removeFavoriteTweet(Id, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("info"," UnFavourited succesfully");
                        adapter.tweets.get(position).setFavourited(false);
                        int fcount = adapter.tweets.get(position).getFavoritesCount();
                        adapter.tweets.get(position).setFavoritesCount(fcount - 1);
                        adapter.notifyItemChanged(position);
                        ApplicationHelper.persistData(adapter.tweets);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    }

                });
            }

            @Override
            public void onFavouriteButtonClicked(long Id, final int position) {
                client.addFavoriteTweet(Id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("info", " Favourited succesfully");
                        adapter.tweets.get(position).setFavourited(true);
                        int fcount = adapter.tweets.get(position).getFavoritesCount();
                        adapter.tweets.get(position).setFavoritesCount(fcount+1);
                        adapter.notifyItemChanged(position);
                        ApplicationHelper.persistData(adapter.tweets);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    }
                });
            }

            @Override
            public void onRetweetButtonClicked(long Id, int position) {

            }

            @Override
            public void onReplyButtonClicked(long Id, int position) {
                showComposeTweetDialog(Id, position);

            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvTweets.setAdapter(adapter);

        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvTweets.setLayoutManager(linearLayoutManager);

        since_id = 1;
        max_id = 1;

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager){

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("info","scroll - new tweets needed "+since_id);
                getTimeline(0, max_id, SCROLL_OPERATION);
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                Log.i("info", "refresh - new items needed " + since_id);
                getTimeline(since_id, 0, REFRESH_OPERATION);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if(!ApplicationHelper.isNetworkAvailable(getApplicationContext()) || !ApplicationHelper.isOnline()){
            //ApplicationHelper.showWarning(HomeTimelineActivity.this);
            ArrayList<Tweet> twts = Tweet.getDBTweets();

            for(Tweet twt: twts){
                Log.i("info","db tweet: "+twt.getTweetText() +" "+twt.getTweetId());
            }

            adapter.addAtStartList(twts);
            Log.i("info", "adapter size " + adapter.tweets.size());
            //adapter.notifyItemRangeInserted(0, twts.size()-1);
            adapter.notifyDataSetChanged();

        } else {
            getTimeline(since_id, 0, REFRESH_OPERATION);
        }
    }


    private void showComposeTweetDialog(final long Id, int pos) {
        FragmentManager fm = getSupportFragmentManager();
        Tweet t = new Tweet();
        String title = "";
        User self;
        int operation;
        if(Id > 0){
            operation = REPLY;
            title = "Reply to";
            //t.setUser(owner);
        } else {
            operation = COMPOSE;
            title = "Write on timeline";
            //t.setUser(owner);
        }
        self = owner;
        t.setUser(adapter.tweets.get(pos).getUser());

        //t.retweetedUser = adapter.tweets.get(pos).retweetedUser;

        ComposeNewTweetFragment composeNewTweetFragment = ComposeNewTweetFragment.newInstance(t, title, operation, self);
        composeNewTweetFragment.show(fm, "fragment_compose_tweet");

        composeNewTweetFragment.setCustomObjectListener(new ComposeNewTweetFragment.NewTweetDialogListener() {

            @Override
            public void onFinishPostingTweet(Tweet newTweet) {

                Log.i("info","Returned from dialog with tweet details");

                String status = newTweet.getTweetText();

                // make POST to new send new tweet
                // new tweet to the adapter and call notifyItemRangeInserted
                client.updateStatus(Id, status, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        Log.i("info","Successfully posted the tweet "+response.toString());

                        // refresh the timeline to get the new tweet
                        getTimeline(since_id, 0, REFRESH_OPERATION);
                        ApplicationHelper.persistData(adapter.tweets);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    }
                });
            }
        });

    }

    private void getTimeline(final long sinceId, final long maxId, final int operation) {

        client.getHomeTimeline(sinceId, maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.i("info", "timeline: " + response.toString());
                Log.i("info", "Tweets: " + response.length());

                try {
                    fetchedTweets = Tweet.getAllTweets(response);

                    if (fetchedTweets.size() > 0) {

                        if (operation == SCROLL_OPERATION) {
                            // get current size of the adapter
                            int curSize = adapter.getItemCount();
                            adapter.appendList(fetchedTweets);
                            adapter.notifyItemRangeInserted(curSize, adapter.getItemCount() - 1);

                                /* this doesnt work because if we scroll down to new elements the adapter thinks more
                                   elements are needed so it keeps scrolling infinitely making n/w calls to get new data */
                            //rvArticles.scrollToPosition(adapter.getItemCount() - 1);
                            //rvArticles.scrollToPosition(curSize+2);

                            Log.i("info", fetchedTweets.toString());
                            Log.i("info", "Scroll - Range inserted [" + curSize + "-" + adapter.getItemCount() + "]");
                        } else {
                            // get current size of the adapter
                            int curSize = fetchedTweets.size() - 1;
                            adapter.addAtStartList(fetchedTweets);
                            adapter.notifyItemRangeInserted(0, curSize);
                            rvTweets.smoothScrollToPosition(0);

                            Log.i("info", fetchedTweets.toString());
                            Log.i("info", "REFRESH - Range inserted [0-" + curSize + "]");

                        }
                        since_id = fetchedTweets.get(0).tweetId;
                        max_id = fetchedTweets.get(fetchedTweets.size() - 1).tweetId;
                        ApplicationHelper.persistData(adapter.tweets);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //hideProgressBar();
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                /// when internet not there this log statement crashes .. NULL pointer..
                Log.i("info", "error: " + errorResponse.toString());
                try {

                } catch (Exception e){

                }
            }
        });

    }

    private void getUserCredentials() {
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.i("info","Successful to get credentials "+response.toString());
                owner = User.fromJSON(response);
                Log.i("info","Owner name: "+owner.getName()+" "+owner.getProfile_image_url());
                getSupportActionBar().setTitle(" @" + owner.getScreenName());
                ApplicationHelper.persistData(adapter.tweets);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_timeline, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                client.searchTweets();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
