package com.codepath.apps.twitfeeder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.twitfeeder.R;
import com.codepath.apps.twitfeeder.activities.TweetDetailsActivity;
import com.codepath.apps.twitfeeder.models.Tweet;
import com.codepath.apps.twitfeeder.utils.ApplicationHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    Context context;

    private ActionButtonListener listener;

    public ArrayList<Tweet> tweets = new ArrayList<>();

    public TweetAdapter(Context context){
        this.context = context;
    }

    public interface ActionButtonListener {
        void onRemoveFavouriteButtonClicked(long Id, int position);
        void onFavouriteButtonClicked(long Id, int position);
        void onRetweetButtonClicked(long Id, int position);
        void onReplyButtonClicked(long Id, int position);
    }

    public void setCustomObjectListener(ActionButtonListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context context;

        @Bind(R.id.ivProfileImage) ImageView mProfileImage;
        @Bind(R.id.tvName) TextView mName;
        @Nullable @Bind(R.id.ivMedia) ImageView mMedia;
        @Bind(R.id.tvScreenName) TextView mScreenName;
        @Bind(R.id.tvRelativeTimestamp) TextView mRelativeTimestamp;
        @Bind(R.id.tvTweetText) TextView mTweetText;
        @Bind(R.id.llMediaContainer) LinearLayout mMediaContainer;
        @Bind(R.id.favouriteCount) TextView mFavouriteCount;
        @Bind(R.id.retweetCount) TextView mRetweetCount;
        @Bind(R.id.favourite) ImageView mFavouriteImage;
        @Bind(R.id.retweet) ImageView mRetweetImage;
        @Bind(R.id.reply) ImageView mReplyImage;
        @Bind(R.id.llRetweetStatusContainer) LinearLayout mRetweetContainer;


        public ViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
            itemView.setOnClickListener(this);
            mFavouriteImage.setOnClickListener(this);
            mRetweetImage.setOnClickListener(this);
            mReplyImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getLayoutPosition(); // gets item position

            switch(v.getId()) {

                case R.id.favourite:

                    Log.i("info", "Received fav click " + String.valueOf(position));

                    if(tweets.get(position).isFavourited()){

                        listener.onRemoveFavouriteButtonClicked(tweets.get(position).getTweetId(), position);

                    } else {
                        listener.onFavouriteButtonClicked(tweets.get(position).getTweetId(), position);

                    }
                    break;

                case R.id.retweet:
                    Log.i("info", "Received retwt click " + String.valueOf(position));
                    listener.onRetweetButtonClicked(tweets.get(position).getTweetId(), position);
                    break;

                case R.id.reply:
                    Log.i("info", "Received reply click " + String.valueOf(position));
                    listener.onReplyButtonClicked(tweets.get(position).getTweetId(), position);

                    break;

                default:
                    Tweet tweet = tweets.get(position);
                    Log.i("info","tweet pic "+tweet.getPictureUrl() + " "+tweet.isMedia());
                    //Log.i("info", "Tweet clicked " + tweet.getTweetText());
                    Intent i = new Intent(context, TweetDetailsActivity.class);
                    i.putExtra("tweet", tweet);
                    context.startActivity(i);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    break;

            }

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.list_tweet_details, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context, tweetView);
        //FavouriteTweet t = new FavouriteTweet(tweetView.findViewById(R.id.retweet));
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Tweet tweet = tweets.get(position);

        holder.mTweetText.setText(tweet.getTweetText());

        final ImageView ivTemp = holder.mProfileImage;
        ivTemp.setImageResource(0);

        holder.mFavouriteImage.setImageResource(0);
        holder.mRetweetImage.setImageResource(0);

        Glide.with(context)
                .load(tweet.getUser().getProfile_image_url())
            .asBitmap()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(new BitmapImageViewTarget(ivTemp) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        //circularBitmapDrawable.setCircular(true);
                        circularBitmapDrawable.setCornerRadius(10);
                        //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                    ivTemp.setImageDrawable(circularBitmapDrawable);
                }
            });

        holder.mMediaContainer.removeAllViews();

        if(tweet.isMedia()) {
            if (tweet.getMediaType().equalsIgnoreCase("photo")) {
                View v = LayoutInflater.from(context).inflate(R.layout.tweet_media_image, null);
                ImageView ivMediaImage = (ImageView) v.findViewById(R.id.ivMedia);
                //String url = tweet.getPictureUrl();
                //Log.i("info", "media:" + url);

                Glide.with(context)
                        .load(tweet.getPictureUrl())
                        .crossFade()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .into(ivMediaImage);
                holder.mMediaContainer.addView(v);
            }


            if (tweet.getMediaType().equalsIgnoreCase("video")) {

            }
        }

        holder.mName.setText(tweet.getUser().getName());
        holder.mScreenName.setText("@"+tweet.getUser().getScreenName());
        holder.mRelativeTimestamp.setText(ApplicationHelper.getRelativeTimeAgo(tweet.getRawDate()));
        holder.mRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        holder.mFavouriteCount.setText(String.valueOf(tweet.getFavoritesCount()));
        holder.mFavouriteImage.setImageResource(R.drawable.ic_favorite);
        holder.mRetweetImage.setImageResource(R.drawable.ic_retweet);

        if(tweet.isFavourited()){

            holder.mFavouriteImage.setImageResource(R.drawable.ic_favorite_yellow);
        }

        if(tweet.isRetweeted()){
            holder.mRetweetImage.setImageResource(R.drawable.ic_retweet_green);
        }

        holder.mRetweetContainer.removeAllViews();

        if(tweet.retweetedUser != null){
            View v = LayoutInflater.from(context).inflate(R.layout.retweet_user_details, null);
            TextView tvRetweetUserName = (TextView) v.findViewById(R.id.tvRetweetUserName);
            tvRetweetUserName.setText(tweet.retweetedUser.getName() + " retweeted");
            holder.mRetweetContainer.addView(v);
        }

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void appendList (ArrayList<Tweet> t) {
        tweets.addAll(t);
        Log.i("info", "Number of tweets appended " + tweets.size());
    }

    public void addAtStartList (ArrayList<Tweet> t) {

        tweets.addAll(0, t);
        Log.i("info", "Number of tweets prepended " + tweets.size());
    }

}
