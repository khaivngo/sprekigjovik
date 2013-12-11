package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays the team with teamname from intent. 
 * Displays buttons for team members and join/leave as well as team highscores
 * @author Chris
 *
 */

public class DisplayTeam extends Activity {
	
	private SQLiteDatabase db;
	private String teamName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_team);
		
		Bundle extras = getIntent().getExtras(); 
	    teamName = extras.getString("team");
	    
	    if(!checkLogin()){		//Hides join button if user isn't logged in, enters if if it didn't hide
	    	if(isMember()){		//Hides join button if user is member of team
	    		hideJoin();
	    	}
	    }
	    
		displayTeamInfo();
	}

	
	//Allows mainactivity to refresh buttons like "Your team"
	@Override
	public void onBackPressed() {
	   Intent intent = new Intent(this, MainActivity.class);
	   startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return true;
	}
	
	//Action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
	    switch (item.getItemId()) {
	    case R.id.action_profile:
	    	intent = new Intent(this, Profile.class);
	    	startActivity(intent);
	    	break;
	    default:
	      break;
	    }

	    return true;
	  } 
	
	/**
	 * Fills team-info in the layout.
	 * Team name and team description.
	 */
	public void displayTeamInfo() {
		
		//Gets current team
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT * FROM teams WHERE name='" + teamName + "';" , null);
		cursor.moveToFirst();
		
		//Sets the teamdata on the textviews 
		if((cursor!=null && cursor.getCount()>0)){
			TextView tName = (TextView) findViewById(R.id.team_name);
			tName.setText(cursor.getString(1));
			
			TextView tDescription = (TextView) findViewById(R.id.team_description);
			tDescription.setText(cursor.getString(2));
			cursor.close();
		}
	}
	/**
	 * Opens a popup containing a list with the team members
	 * @param v Current view
	 */
	public void viewMembers(View v){
		
		//Gets team members
		String[] values = getMembers();		
        
		//Creates and populates pupop and list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                DisplayTeam.this,
                android.R.layout.simple_list_item_1, values);
		Dialog dialog = new Dialog(DisplayTeam.this);
		dialog.setContentView(R.layout.team_members);
		ListView lv = (ListView ) dialog.findViewById(R.id.user_list);
		lv.setAdapter(arrayAdapter);
		
		//Displays dialog
		dialog.setCancelable(true);
		dialog.setTitle("Members");
		dialog.show();
	}
	
	/**
	 * Joins the current team with the logged in user. Requires user to be logged in. 
	 * @param v Current view
	 */
	public void joinTeam(View v){
		
		//Gets username
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		String teamId = null;
		
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE, null);
		
		//Gets team id
		Cursor cursor = db.rawQuery("SELECT id FROM teams WHERE name LIKE '" + teamName + "';", null);
		cursor.moveToFirst();
		teamId = cursor.getString(0);
		
		//Updates team id on user, joins team
		db.execSQL("UPDATE peeps SET teamId='" + teamId + "' WHERE username LIKE '" + username + "';");
		
		//Makes sure user joined team
		cursor = db.rawQuery("SELECT teamId FROM peeps WHERE teamId='" + teamId + "' AND username LIKE '" + username + "';", null);
		cursor.moveToFirst();
		
		//Displays feedback to the user if user joined team
		if(cursor != null && cursor.getCount() > 0){
			hideJoin();
			 Toast.makeText(DisplayTeam.this, getString(R.string.join_successful),
					     Toast.LENGTH_SHORT).show();
		}
		
		//Adds "leave team" button
		TextView tv = (TextView) findViewById(R.id.leaveTeam);
		tv.setVisibility(View.VISIBLE);
		
		cursor.close();
	}
	
	/**
	 * Gets all team members usernames of the current team. 
	 * @return Array with members
	 */
	public String[] getMembers() {	    	
		List<String> strings = new ArrayList<String>();
		
    	db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT username FROM peeps WHERE teamId IN " +
										"(SELECT id FROM teams WHERE name LIKE '" + teamName + "');", null);
    	
		//Populates the list with usernames
		while(cursor.moveToNext()){
			strings.add(cursor.getString(0));
		}
    	String[] array = strings.toArray(new String[0]);
    	
    	cursor.close();
    	
    	return array;
	}
	
	/**
	 * Checks if user is member of the current team. If user is member of the team, it adds a 
	 * "leave team" button.
	 * @return Boolean true if member, else false
	 */
	public Boolean isMember(){
		
		//Gets username of logged in user
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		
		//Gets teamname from logged in user
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT name FROM teams WHERE id = (SELECT teamId FROM peeps WHERE username LIKE '" + username + "');", null);
		cursor.moveToFirst();
		
		//Adds a "leave team" button if user is a member of the team and returns true
		if(cursor!=null && cursor.getCount()>0){
			if(cursor.getString(0).equals(teamName)){
				TextView tv = (TextView) findViewById(R.id.leaveTeam);
				tv.setVisibility(View.VISIBLE);
				cursor.close();
				return true;
			}
		}
		cursor.close();
		
		//Returns false if user is not member of the team
		return false;
	}
	
	/**
	 * Hides join button
	 */
	public void hideJoin() {
		Button button = (Button) findViewById(R.id.join_team);
    	button.setVisibility(View.GONE);
	}
	
	/**
	 * Opens highscore activity with teamname set as extra.
	 * Will only display team highscores
	 * @param v
	 */
	public void teamHighscore(View v){
		
		Intent intent = new Intent(this, TeamHighscore.class);
		intent.putExtra("teamName", teamName);
		startActivity(intent);
	}
	
	/**
	 * Hides the join button if user isn't logged in
	 * @return True if user wasn't logged in and button was hidden. False if button wasn't hidden and
	 * user logged in
	 */
	public Boolean checkLogin() {
		//Gets username
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String textValue = sharedPreferences.getString("UserName", "");
		
		//Checks if username is set
		if(textValue == "" || textValue == null){
			//Hides join button
			Button button = (Button) findViewById(R.id.join_team);
			button.setVisibility(View.GONE);
			return true;
		}
		return false;
	}
	
	/**
	 * Sets teamId on the user to null, hides leave button and displays join button
	 * @param v Current view
	 */
	public void leaveTeam(View v){
		
		//Gets username
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		String teamId = null;
		
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE, null);
		
		//Gets teamId
		Cursor cursor = db.rawQuery("SELECT id FROM teams WHERE name LIKE '" + teamName + "';", null);
		cursor.moveToFirst();
		teamId = cursor.getString(0);
		
		//Sets teamId to null on logged in user
		db.execSQL("UPDATE peeps SET teamId=NULL WHERE username LIKE '" + username + "';");
		
		//Makes sure user left team
		cursor = db.rawQuery("SELECT teamId FROM peeps WHERE teamId IS NULL AND username LIKE '" + username + "';", null);
		cursor.moveToFirst();
		//If user left team
		if(cursor != null && cursor.getCount() > 0){
			hideJoin();
			//Displays feedback to user
			Toast.makeText(DisplayTeam.this, getString(R.string.leave_successful),
					     Toast.LENGTH_SHORT).show();
		}
		
		//Displays join button
		TextView tv = (TextView) findViewById(R.id.join_team);
		tv.setVisibility(View.VISIBLE);
		//Hides leave button
		tv = (TextView) findViewById(R.id.leaveTeam);
		tv.setVisibility(View.GONE);
		
		cursor.close();
	}
}
