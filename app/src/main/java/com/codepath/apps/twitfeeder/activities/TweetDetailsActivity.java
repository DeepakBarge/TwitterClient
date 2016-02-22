package com.codepath.apps.twitfeeder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.twitfeeder.R;
import com.codepath.apps.twitfeeder.models.Tweet;
import com.codepath.apps.twitfeeder.utils.ApplicationHelper;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.ivProfileImage) ImageView mProfileImage;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tvName) TextView mUserName;
    @Bind(R.id.tvScreenName) TextView mScreenName;
    @Bind(R.id.tvTweetText) TextView mTweetText;
    @Bind(R.id.tvDate) TextView mDate;
    @Bind(R.id.tvRetweetsCount) TextView mRetweetsCount;
    @Bind(R.id.tvFavouritesCount) TextView mFavouritesCount;
    @Bind(R.id.llmediaContainer) LinearLayout mLLMediaContainer;
    @Bind(R.id.llCountsContainer) LinearLayout mLLCountsContainer;
    @Bind(R.id.bReply) Button mReplyButton;
    @Bind(R.id.bRetweet) Button mRetweetButton;
    @Bind(R.id.bFavorite) Button mFavouriteButton;
    @Bind(R.id.bShare) Button mShareButton;
    @Nullable @Bind(R.id.ivMedia) ImageView mMedia;
    @Bind(R.id.llRetweetStatusContainer) LinearLayout mRetweetContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        mReplyButton.setOnClickListener(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        Tweet tweet = i.getParcelableExtra("tweet");

        getSupportActionBar().setTitle("@" + tweet.getUser().getScreenName());

        mTweetText.setText(tweet.getTweetText());

        final ImageView ivTemp = mProfileImage;

        //ivTemp.setImageResource(0);

        Glide.with(getApplicationContext())
                .load(tweet.getUser().getProfile_image_url())
                .asBitmap()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(new BitmapImageViewTarget(ivTemp) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        //circularBitmapDrawable.setCircular(true);
                        circularBitmapDrawable.setCornerRadius(10);
                        //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                        ivTemp.setImageDrawable(circularBitmapDrawable);
                    }
                });

        //mLLMediaContainer.removeAllViews();
        Log.i("info","tweet pic "+tweet.getPictureUrl() + " "+tweet.isMedia());

        if(tweet.isMedia()) {

            if (tweet.getMediaType().equalsIgnoreCase("photo")) {

                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tweet_media_image, null);
                ImageView ivMediaImage = (ImageView) v.findViewById(R.id.ivMedia);
                //String url = tweet.getPictureUrl();
                //Log.i("info", "media:" + url);

                Glide.with(getApplicationContext())
                        .load(tweet.getPictureUrl())
                        .crossFade()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .into(ivMediaImage);
                mLLMediaContainer.addView(v);
            }
        }

        mUserName.setText(tweet.getUser().getName());
        mScreenName.setText("@"+tweet.getUser().getScreenName());
        //mRelativeTimestamp.setText(ApplicationHelper.getRelativeTimeAgo(tweet.getRawDate()));
        mRetweetsCount.setText(String.valueOf(tweet.getRetweetCount()));
        mFavouritesCount.setText(String.valueOf(tweet.getFavoritesCount()));
        //mFavouriteImage.setImageResource(R.drawable.ic_favorite);
        //mRetweetImage.setImageResource(R.drawable.ic_retweet);

        if(tweet.isFavourited()){
            mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_yellow, 0, 0, 0);

            //mFavouriteImage.setImageResource(R.drawable.ic_favorite_yellow);
        }

        if(tweet.isRetweeted()){
            //mRetweetImage.setImageResource(R.drawable.ic_retweet_green);
        }

        mRetweetContainer.removeAllViews();

        if(tweet.retweetedUser != null){
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.retweet_user_details, null);
            TextView tvRetweetUserName = (TextView) v.findViewById(R.id.tvRetweetUserName);
            tvRetweetUserName.setText(tweet.retweetedUser.getName() + " retweeted");
            mRetweetContainer.addView(v);
        }

        long dateMillis = ApplicationHelper.convertToDate(tweet.getRawDate()).getTime();

        Date d = new Date(dateMillis);
        mDate.setText(d.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.bReply:


            break;
        }
    }

    /* this method is overridden to prevent the UP/BACK button from creating a new activity
   instead of showing the old activity */
    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
    }

}
