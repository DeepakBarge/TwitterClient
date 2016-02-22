package com.codepath.apps.twitfeeder.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "User")
public class User extends Model implements Parcelable {

    // This is the unique id given by the server
    //@Column(name = "remote_id", unique = true)
    //public long remoteId;

    @Column(name = "Name")
    public String name;

    @Column(name = "UserId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long userId;

    @Column(name = "Profile_Image_Url")
    public String profile_image_url;

    @Column(name = "ScreenName")
    public String screenName;

    @Column(name = "Description")
    public String description;

    @Column(name = "TweetCount")
    public int tweetCount;

    @Column(name = "FollowersCount")
    public int followersCount;

    @Column(name = "FriendsCount")
    public int friendsCount;


    public User (){
        super();
    }


    public static List<User> getDBUsers() {
        // This is how you execute a query

        return new Select().all().from(User.class).execute();
        /*return new Select()
                .from(Tweet.class)
                .where("Category = ?", category.getId())
                .orderBy("Name ASC")
                .execute();*/
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(int tweetCount) {
        this.tweetCount = tweetCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public static User fromJSON(JSONObject jsonObject){

        User user = new User();

        try{
            user.setName(jsonObject.getString("name"));
            user.setProfile_image_url(jsonObject.getString("profile_image_url"));
            user.setScreenName(jsonObject.getString("screen_name"));
            user.setUserId(jsonObject.getLong("id"));
            user.setDescription(jsonObject.getString("description"));
            user.setProfile_image_url(jsonObject.getString("profile_image_url"));
            user.setProfile_image_url(user.getProfile_image_url().replace("_normal.", "_bigger."));
            user.setTweetCount(jsonObject.getInt("statuses_count"));
            user.setFollowersCount(jsonObject.getInt("followers_count"));
            user.setFriendsCount(jsonObject.getInt("friends_count"));

        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    public static User getUser(long userId){
        return new Select().from(User.class).where("UserId = ?", userId)
                .limit(1).executeSingle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.userId);
        dest.writeString(this.profile_image_url);
        dest.writeString(this.screenName);
        dest.writeString(this.description);
        dest.writeInt(this.tweetCount);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.friendsCount);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.userId = in.readLong();
        this.profile_image_url = in.readString();
        this.screenName = in.readString();
        this.description = in.readString();
        this.tweetCount = in.readInt();
        this.followersCount = in.readInt();
        this.friendsCount = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
