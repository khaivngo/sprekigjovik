package com.hig.prestigedevelopment.sprekigjovik;

/**
 * @author Chris
 * Displays userinformation and logout button. User needs to be logged in, or he will be sent to the 
 * login activity
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Profile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		//Starts login activity if user isn't logged in
		if(!checkLogin()){
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
		//Displays userinformation if user is logged in
		} else {
			SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
			String textValue = sharedPreferences.getString("UserName", "");
			TextView text = (TextView)findViewById(R.id.username);
			text.setText(getString(R.string.logged_in_as)+ " " + textValue);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    return true;
	}
	
	/**
	 * Starts new mainactivity instance on back press, to refresh buttons 
	 * now that user is logged in
	 */
	@Override
    public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
        super.onBackPressed();   
    }
	
	//Action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	  intent = new Intent(this, MainActivity.class);
		    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	  startActivity(intent);
		    	  break; 
	    default:
	      break;
	    }

	    return true;
	  } 
	
	/**
	 * Checks if user is logged in.
	 * @return boolean if user is logged in or not
	 */
	public Boolean checkLogin(){
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String userName = sharedPreferences.getString("UserName", "");
		
		return (userName != "") ? true : false;
	}
	
	/**
	 * Logs out user by clearing sharedpreferences.
	 * @param view Current view
	 */
	public void logOut(View view) {
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
