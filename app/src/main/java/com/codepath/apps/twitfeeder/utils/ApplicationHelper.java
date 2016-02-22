package com.codepath.apps.twitfeeder.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.codepath.apps.twitfeeder.R;
import com.codepath.apps.twitfeeder.models.Tweet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ApplicationHelper {

    public static Context context;
    public static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public interface AlertDialogListener {
        void onTryAgain();
        void onCancel();
    }


/*
    Resources res = context.getResources();
    Bitmap src = BitmapFactory.decodeResource(res, iconResource);
    RoundedBitmapDrawable dr =
            RoundedBitmapDrawableFactory.create(res, src);
    dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
    imageView.setImageDrawable(dr);
*/


    public static void setContext(Context c){
        context = c;
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void showWarning(final Context context) {

        String message = "";

        if(!isOnline()){
            message += "Internet disconnected.";
        }
        if(!isNetworkAvailable(context)){
            message+= "Network not available";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setTitle(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialogListener listener = (AlertDialogListener) context;
                        listener.onTryAgain();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        //SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        //sf.setLenient(true);

        String relativeDate = "";

        long dateMillis = convertToDate(rawJsonDate).getTime();

        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    public static Date convertToDate(String date)  {

        Date convertedDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(
                TWITTER_DATE_FORMAT, Locale.US);
        sf.setLenient(true);
        try {
            convertedDate = sf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static boolean persistData(ArrayList<Tweet> twts){

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < twts.size(); i++) {
                twts.get(i).save();
                twts.get(i).getUser().save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
        Log.i("info","Data persisted");
        return true;
    }

}
