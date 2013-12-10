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
	    
	    //Toast.makeText(getApplicationContext(), teamName, Toast.LENGTH_LONG).show();
		displayTeamInfo();
	}

	
	//Allows mainactivity to refresh buttons like "Your team"
	@Override
	public void onBackPressed() {
	   Intent intent = new Intent(this, MainActivity.class);
	   startActivity(intent);
	}
	
    @Override
    protected void onDestroy() {

        db.close();

        super.onDestroy();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
	    switch (item.getItemId()) {
	    case R.id.action_profile:
	    	intent = new Intent(this, Profile.class);
	    	startActivity(intent);
	    	break;
	    case R.id.action_maps:
	    	intent = new Intent(this, Maps.class);
	    	startActivity(intent);
	    	break;
	    default:
	      break;
	    }

	    return true;
	  } 
	
	public void displayTeamInfo() {
		
		
		
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT * FROM teams WHERE name='" + teamName + "';" , null);
		
		cursor.moveToFirst();
		if((cursor!=null && cursor.getCount()>0)){
			TextView tName = (TextView) findViewById(R.id.team_name);
			tName.setText(cursor.getString(1));
			
			TextView tDescription = (TextView) findViewById(R.id.team_description);
			tDescription.setText(cursor.getString(2));
			cursor.close();
		}
		
	}
	
	public void viewMembers(View v){
		
		String[] values = getMembers();		
        
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                DisplayTeam.this,
                android.R.layout.simple_list_item_1, values);
		Dialog dialog = new Dialog(DisplayTeam.this);
		dialog.setContentView(R.layout.team_members);
		ListView lv = (ListView ) dialog.findViewById(R.id.user_list);
		lv.setAdapter(arrayAdapter);
		
		dialog.setCancelable(true);
		dialog.setTitle("Members");
		dialog.show();
	}
	
	public void joinTeam(View v){
		
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		String teamId = null;
		
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT id FROM teams WHERE name LIKE '" + teamName + "';", null);
		cursor.moveToFirst();
		teamId = cursor.getString(0);
		
		db.execSQL("UPDATE peeps SET teamId='" + teamId + "' WHERE username LIKE '" + username + "';");
		
		cursor = db.rawQuery("SELECT teamId FROM peeps WHERE teamId='" + teamId + "' AND username LIKE '" + username + "';", null);
		cursor.moveToFirst();
		if(cursor != null && cursor.getCount() > 0){
			hideJoin();
			 Toast.makeText(DisplayTeam.this, getString(R.string.join_successful),
					     Toast.LENGTH_SHORT).show();
		}
		TextView tv = (TextView) findViewById(R.id.leaveTeam);
		tv.setVisibility(View.VISIBLE);
		
		cursor.close();
		
	}
	
	public String[] getMembers() {	    	
	    	db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
			Cursor cursor = db.rawQuery("SELECT username FROM peeps WHERE teamId IN " +
											"(SELECT id FROM teams WHERE name LIKE '" + teamName + "');", null);
	    	
			List<String> strings = new ArrayList<String>();
			while(cursor.moveToNext()){
				strings.add(cursor.getString(0));
			}
	    	String[] array = strings.toArray(new String[0]);
	    	
	    	cursor.close();
	    	
	    	return array;
	}
	
	public Boolean isMember(){
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT name FROM teams WHERE id = (SELECT teamId FROM peeps WHERE username LIKE '" + username + "');", null);
		cursor.moveToFirst();
		
		if(cursor!=null && cursor.getCount()>0){
			if(cursor.getString(0).equals(teamName)){
				TextView tv = (TextView) findViewById(R.id.leaveTeam);
				tv.setVisibility(View.VISIBLE);
				cursor.close();
				return true;
			}
		}
		cursor.close();
		
		return false;
	}
	
	public void hideJoin() {
		Button button = (Button) findViewById(R.id.join_team);
    	button.setVisibility(View.GONE);
	}
	
	public void teamHighscore(View v){
		
		Intent intent = new Intent(this, TeamHighscore.class);
		intent.putExtra("teamName", teamName);
		startActivity(intent);
	}
	
	/**
	 * Hides the join button if user isn't logged in
	 * @return True if button was hidden
	 */
	public Boolean checkLogin() {
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String textValue = sharedPreferences.getString("UserName", "");
		
		if(textValue == "" || textValue == null){
			Button button = (Button) findViewById(R.id.join_team);
			button.setVisibility(View.GONE);
			return true;
		}
		return false;
	}
	
	public void leaveTeam(View v){
		
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		String teamId = null;
		
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE, null);
		
		Cursor cursor = db.rawQuery("SELECT id FROM teams WHERE name LIKE '" + teamName + "';", null);
		cursor.moveToFirst();
		teamId = cursor.getString(0);
		
		db.execSQL("UPDATE peeps SET teamId=NULL WHERE username LIKE '" + username + "';");
		
		cursor = db.rawQuery("SELECT teamId FROM peeps WHERE teamId IS NULL AND username LIKE '" + username + "';", null);
		cursor.moveToFirst();
		if(cursor != null && cursor.getCount() > 0){
			hideJoin();
			 Toast.makeText(DisplayTeam.this, getString(R.string.leave_successful),
					     Toast.LENGTH_SHORT).show();
		}
		
		TextView tv = (TextView) findViewById(R.id.join_team);
		tv.setVisibility(View.VISIBLE);
		tv = (TextView) findViewById(R.id.leaveTeam);
		tv.setVisibility(View.GONE);
		
		cursor.close();
	}
}