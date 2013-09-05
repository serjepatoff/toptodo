package com.example.toptodo;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class ParseOps {
	
	public static void setupParseSDK( Context c ) {
		String appId = c.getString(R.string.app_id);
		String clientKey = c.getString(R.string.client_key);
		Parse.initialize(c, appId, clientKey);
		ParseObject.registerSubclass(TodoItem.class);
		PushService.subscribe(c, c.getString(R.string.push_channel_key), TodoListActivity.class);
	}
	
	public static void trackAppOpened( Activity ac ) {
		ParseAnalytics.trackAppOpened(ac.getIntent());
	}
	
	public static void fetchItemsInBackground( final ArrayAdapter<TodoItem> a, String sortBy, boolean order ) {
		ParseQuery<TodoItem> query = ParseQuery.getQuery(TodoItem.class);
		if ( sortBy!=null && !sortBy.isEmpty() ) {
			if ( order ) query.addAscendingOrder(sortBy);
			else query.addDescendingOrder(sortBy);
		}
		
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.setLimit(1000);
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<TodoItem>() {
			@Override
			public void done(List<TodoItem> tasks, ParseException error) {
					a.clear();
					if ( tasks!=null ) {
						for (TodoItem todoItem : tasks) {
							a.add(todoItem);
						}
					}
					//a.addAll(tasks != null ? tasks : new ArrayList<TodoItem>());
			}
		});
	}
	
	public static void updateItem( 
			final Context c, 
			TodoItem it, 
			final ArrayAdapter<TodoItem> a, 
			final MenuItem uiItemToToggle,
			final String token,
			final boolean saveExisting,
			final String sortBy,
			final boolean sortOrder
	) 
	{
		it.setACL(new ParseACL(ParseUser.getCurrentUser()));
		it.setUser(ParseUser.getCurrentUser());
		if (!saveExisting) a.add(it);
		if ( uiItemToToggle!=null ) uiItemToToggle.setEnabled(false);
		it.saveEventually( new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if ( uiItemToToggle!=null ) uiItemToToggle.setEnabled(true);
				sendPushNotification(c, token);
				fetchItemsInBackground(a, sortBy, sortOrder);
			}
		});
	}
	
	public static void deleteItem( 
			final Context c, 
			final TodoItem it, 
			final ArrayAdapter<TodoItem> a, 
			final View viewToToggle,
			final String token
	) 
	{
		viewToToggle.setEnabled(false);
		it.deleteInBackground(new DeleteCallback() {
			@Override
			public void done(ParseException e) {
				a.remove(it);
				viewToToggle.setEnabled(true);
				sendPushNotification(c, token);
			}
		});
	}
	
	public static boolean isUserLoggedIn() {
		return ParseUser.getCurrentUser()!=null;
	}
	
	public static void sendPushNotification( final Context c, String token ) {
		final JSONObject data;
		try {
			data = new JSONObject();
			data.put( c.getString(R.string.push_action_key), c.getString(R.string.push_intent_key));
			data.put( c.getString(R.string.push_token_key), token);
					
			Executors.newSingleThreadScheduledExecutor().schedule( new Runnable() {
				@Override
				public void run() {
					ParsePush push = new ParsePush();
					push.setChannel( c.getString(R.string.push_channel_key) );
					push.setData(data);
					push.sendInBackground();
				}
			}, 2, TimeUnit.SECONDS);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
