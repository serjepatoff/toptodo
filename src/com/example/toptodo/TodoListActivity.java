package com.example.toptodo;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;

import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

public class TodoListActivity extends Activity implements OnItemClickListener {
	
	private static final String PARSE_EXTRA_DATA = "com.parse.Data";
	private static final int ITEM_CREATE_ACTIVITY_REQ=100;
	private static final int ITEM_EDIT_ACTIVITY_REQ=101;
	
	private SwipeListView listView;
	private TodoListAdapter todoListAdapter;
	private String tokenSaved;
	private String sortBy = "";
	private boolean sortOrder = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ParseOps.setupParseSDK(this);
		ParseOps.trackAppOpened(this);
		
		boolean loggedIn = ParseOps.isUserLoggedIn(); 
		if ( !loggedIn ) startLoginActivity();
		else 
		{
			setupListView();
			ParseOps.fetchItemsInBackground(todoListAdapter, sortBy, sortOrder);
			setupPushReceiver();
		}
		
	}
	
	private void startLoginActivity() {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}
	
	private void setupPushReceiver() {
		tokenSaved = UUID.randomUUID().toString();
		IntentFilter intentFilter = new IntentFilter( getString(R.string.push_intent_key) );
		BroadcastReceiver pushReceiver;
		pushReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
            	String tokenReceived="";
            	try {
            		JSONObject json = new JSONObject( intent.getExtras().getString(PARSE_EXTRA_DATA));
            		tokenReceived = (String)json.get( getString(R.string.push_token_key) );
            	}
            	catch (Exception ex) {
            		ex.printStackTrace();
            	}
            	
            	if ( !tokenReceived.equals(tokenSaved) ) {
            		ParseOps.fetchItemsInBackground(todoListAdapter, sortBy, sortOrder);
            	}
            }
		};
		registerReceiver(pushReceiver, intentFilter);
	}
	
	private void setupListView() {
		todoListAdapter = new TodoListAdapter(this, new ArrayList<TodoItem>());
		listView = (SwipeListView)findViewById(R.id.todoListView);
		listView.setAdapter(todoListAdapter);
		listView.setOnItemClickListener(this);
		listView.setSwipeListener(new SwipeListView.OnHorizontalSwipeListener() {
			@Override
			public void OnSwipe(int deltaX, int position) {
				TodoItem it = todoListAdapter.getItem(position);
				it.setClosed( !it.getClosed() );
				ParseOps.updateItem(TodoListActivity.this, it, todoListAdapter, null, tokenSaved, true, sortBy, sortOrder);
				Log.d("TOPTODO", "SWIPE: "+deltaX);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        switch (item.getItemId()) 
        {
        	case R.id.action_logout:
	            ParseUser.logOut();
	            startLoginActivity();
	            break;
        	case R.id.action_sort_by_alphabet:
        		sortBy = getString(R.string.header_key);
        		sortOrder = true;
        		refresh();
        		break;
        	case R.id.action_sort_by_due_date:
        		sortBy = getString(R.string.due_date_key);
        		sortOrder = false;
        		refresh();
        		break;
        	case R.id.action_sort_by_urgency:
        		sortBy = getString(R.string.urgency_key);
        		sortOrder = false;
        		refresh();
        		break;
        	case R.id.action_add_item:
        		createNewTodoItem( item );
        		break;
        	case R.id.action_refresh:
        		refresh();
        		break;
        }
        return true;
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( resultCode==RESULT_OK ) {
			Bundle extras = data.getExtras();
			TodoItem it = (TodoItem)extras.getSerializable(getString(R.string.attachment_key));
			switch ( requestCode ) {
			case ITEM_CREATE_ACTIVITY_REQ:
				ParseOps.updateItem(this, it, todoListAdapter, null, tokenSaved, false, sortBy, sortOrder);
				break;
			case ITEM_EDIT_ACTIVITY_REQ:
				ParseOps.updateItem(this, it, todoListAdapter, null, tokenSaved, true, sortBy, sortOrder);
				break;
			}
		}
	}
	
	public void createNewTodoItem( MenuItem item ) {
		Intent intent = new Intent( this, ItemEditActivity.class );
		startActivityForResult(intent, ITEM_CREATE_ACTIVITY_REQ);
	}
	
	public void refresh() {
		ParseOps.fetchItemsInBackground(todoListAdapter, sortBy, sortOrder);
	}
	
	public void onItemDeleteClicked( View v ) {
		TodoItem it = (TodoItem) v.getTag();
		ParseOps.deleteItem(this, it, todoListAdapter, v, tokenSaved);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if ( !listView.getSwipeOccured() ) {
			TodoItem it = todoListAdapter.getItem(position);
			Intent intent = new Intent( this, ItemEditActivity.class );
			intent.putExtra(getString(R.string.attachment_key), it);
			intent.putExtra( getString(R.string.position_key), position);
			startActivityForResult(intent, ITEM_EDIT_ACTIVITY_REQ);
		}
	}
}