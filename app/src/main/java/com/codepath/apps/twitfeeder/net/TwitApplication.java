package com.codepath.apps.twitfeeder.net;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterRestClient client = TwitApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TwitApplication.context = this;
	}

	public static TwitterRestClient getRestClient() {
		return (TwitterRestClient) TwitterRestClient.getInstance(TwitterRestClient.class, TwitApplication.context);
	}
}