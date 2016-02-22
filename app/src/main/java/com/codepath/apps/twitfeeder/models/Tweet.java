package com.codepath.apps.twitfeeder.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.twitfeeder.utils.ApplicationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(name = "Tweet")
public class Tweet extends Model implements Parcelable {

    //@Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    //public long remoteId;

    @Column(name = "TweetText")
    public String tweetText;

    @Column(name = "TweetId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long tweetId;

    @Column(name = "CreatedAt")
    public Date createdAt;

    @Column(name = "UserId")
    public long userId;

    @Column(name = "RetweetCount")
    public int retweetCount;

    @Column(name = "FavouriteCount")
    public int favoritesCount;

    @Column(name = "Favourited")
    public boolean favourited;

    @Column(name = "Retweeted")
    public boolean retweeted;

    @Column(name = "DisplayUrl")
    public String displayUrl;

    @Column(name = "ActualUrl")
    public String actualUrl;

    @Column(name = "PictureUrl")
    public String pictureUrl;

    @Column(name = "VideoUrl")
    public String videoUrl;

    @Column(name = "retweetedUserId")
    public long retweetedUserId;

    @Column(name = "RawDate")
    public String rawDate;

    @Column(name = "IsMedia")
    public boolean isMedia;

    @Column(name = "MediaType")
    public String mediaType;

    @Column(name = "IsVideo")
    public String videoType;

    public User user;

    public User retweetedUser = null;

    public Tweet(){
        super();
    }

    public static ArrayList<Tweet> getDBTweets() {
        // This is how you execute a query

        ArrayList<Tweet> temp = new ArrayList(new Select().all().from(Tweet.class).execute());

        Log.i("info", "Returned tweets: " +temp.size());

        for( Tweet t: temp){
            t.setUser(User.getUser(t.getUserId()));
            Log.i("info","Returned user: "+t.getUser().getScreenName());

        }
        return temp;
        /*return new Select()
                .from(Tweet.class)
                .where("Category = ?", category.getId())
                .orderBy("Name ASC")
                .execute();*/
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public void setActualUrl(String actualUrl) {
        this.actualUrl = actualUrl;
    }

    public long getRetweetedUserId() {
        return retweetedUserId;
    }

    public void setRetweetedUserId(long retweetedUserId) {
        this.retweetedUserId = retweetedUserId;
    }

    public String getRawDate() {
        return rawDate;
    }

    public void setRawDate(String rawDate) {
        this.rawDate = rawDate;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public void setIsMedia(boolean isMedia) {
        this.isMedia = isMedia;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public static Tweet fromJSON(JSONObject jsonObject){

        Tweet tweet = new Tweet();

        try{
            tweet.setTweetText(jsonObject.getString("text"));
            tweet.setRawDate(jsonObject.getString("created_at"));
            tweet.setCreatedAt(ApplicationHelper.convertToDate(jsonObject.getString("created_at")));
            tweet.setTweetId(jsonObject.getLong("id"));
            tweet.setRetweetCount(jsonObject.getInt("retweet_count"));
            tweet.setFavoritesCount(jsonObject.optInt("favorite_count"));
            tweet.setFavourited(jsonObject.getBoolean("favorited"));
            tweet.setRetweeted(jsonObject.getBoolean("retweeted"));
            User user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.setUser(user);
            tweet.setUserId(user.getUserId());
            if (jsonObject.has("entities")) {
                final JSONObject entity = jsonObject.getJSONObject("entities");
                if (entity.has("urls")) {
                    final JSONArray urls = entity.getJSONArray("urls");
                    if (urls.length() == 1) {
                        tweet.setDisplayUrl(urls.getJSONObject(0).getString(
                                "display_url"));
                        tweet.setActualUrl(urls.getJSONObject(0)
                                .getString("url"));
                    }
                }
                if (entity.has("media")) {
                    tweet.setIsMedia(true);
                    final JSONArray media = entity.getJSONArray("media");
                    if (media.length() == 1) {
                        tweet.setMediaType(media.getJSONObject(0).getString(
                                "type"));

                        tweet.setPictureUrl(media.getJSONObject(0).getString(
                                "media_url"));
                    }
                } else {
                    tweet.setIsMedia(false);

                }

            }

            if (jsonObject.has("extended_entities")) {
                final JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");
                if (extendedEntities.has("media")) {
                    tweet.setIsMedia(true);
                    final JSONArray media = extendedEntities.getJSONArray("media");
                    if (media.length() == 1) {
                        tweet.setMediaType(media.getJSONObject(0).getString(
                                "type"));

                        tweet.setPictureUrl(media.getJSONObject(0).getString(
                                "media_url"));
                        if(tweet.getMediaType().equalsIgnoreCase("video")){
                            tweet.setMediaType("video");
                            final JSONArray videoJson = media.getJSONObject(0).getJSONObject("video_info").getJSONArray("variants");

                            for(int i=0;i<videoJson.length();i++){
                                String type = videoJson.getJSONObject(i).getString("content_type");
                                if(type.contains("mpegURL")){
                                    String videoUrl = videoJson.getJSONObject(i).getString("url");
                                    Log.i("Video url ",videoUrl);
                                    tweet.setVideoUrl(videoUrl);
                                }
                            }

                        }
                    }

                } else {
                    tweet.setIsMedia(false);

                }

            }

            if (jsonObject.has("retweeted_status")) {

                final JSONObject retweetedStatus = jsonObject
                        .getJSONObject("retweeted_status");
                final User retweetedUser = User.fromJSON(retweetedStatus
                        .getJSONObject("user"));
                tweet.retweetedUser = tweet.getUser();
                tweet.setUserId(retweetedUser.userId);
                tweet.setUser(retweetedUser);
                tweet.retweetedUserId = user.userId;
                tweet.tweetText = retweetedStatus.getString("text");
                tweet.retweetCount = retweetedStatus.getInt("retweet_count");
                tweet.favoritesCount = retweetedStatus
                        .getInt("favorite_count");
            }

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static ArrayList<Tweet> getAllTweets(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject tweetJSON = jsonArray.getJSONObject(i);
                Tweet t = fromJSON(tweetJSON);
                if(t != null) {
                    tweets.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tweetText);
        dest.writeLong(this.tweetId);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeLong(this.userId);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoritesCount);
        dest.writeByte(favourited ? (byte) 1 : (byte) 0);
        dest.writeByte(retweeted ? (byte) 1 : (byte) 0);
        dest.writeString(this.displayUrl);
        dest.writeString(this.actualUrl);
        dest.writeString(this.pictureUrl);
        dest.writeString(this.videoUrl);
        dest.writeLong(this.retweetedUserId);
        dest.writeString(this.rawDate);
        dest.writeByte(isMedia ? (byte) 1 : (byte) 0);
        dest.writeString(this.mediaType);
        dest.writeString(this.videoType);
        dest.writeParcelable(this.user, 0);
        dest.writeParcelable(this.retweetedUser, 0);
    }

    protected Tweet(Parcel in) {
        this.tweetText = in.readString();
        this.tweetId = in.readLong();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.userId = in.readLong();
        this.retweetCount = in.readInt();
        this.favoritesCount = in.readInt();
        this.favourited = in.readByte() != 0;
        this.retweeted = in.readByte() != 0;
        this.displayUrl = in.readString();
        this.actualUrl = in.readString();
        this.pictureUrl = in.readString();
        this.videoUrl = in.readString();
        this.retweetedUserId = in.readLong();
        this.rawDate = in.readString();
        this.isMedia = in.readByte() != 0;
        this.mediaType = in.readString();
        this.videoType = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.retweetedUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
