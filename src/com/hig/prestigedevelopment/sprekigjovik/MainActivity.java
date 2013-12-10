package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private SQLiteDatabase db;
	private SQLiteDatabase myDB= null;
	private final String tableNamePole			= 	"pole";
	private final String tableNameArea			=	"area";
	private final String tableNameChallengePole	=	"challengePole";
	private final String tableNameChallenge		=	"challenge";
	private static Context context;
	private SQLiteDatabase sessionDB= null;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_main);
		
		//CheckEnableGPS();
		
		setUpDatabase();
		createDB();
		clearSessionDB();			//TESTING, clearing data from session poles
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		if(mViewPager == null) Log.i("hei", "hei");
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		
		//viewScores();
	}

    @Override
    protected void onDestroy() {

        db.close();
        myDB.close();
        sessionDB.close();

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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Fragment fragment = null;
			
			switch(position){
				case 0: return new ChallengeMenu();
				case 1: return new DynamicMenu();
				case 2: if(getUserTeam() == true){
							return new MemberTeamMenu();
						} else {
							return new TeamMenu();
						}	
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
		
		/**
		 * Returns true if user is on a team, false if not on a team, or not logged in.
		 * @return Returns true if user is on a team
		 */
		public Boolean getUserTeam() {
			SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
			String textValue = sharedPreferences.getString("UserName", "");
			
			db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
			db.execSQL("CREATE TABLE IF NOT EXISTS peeps(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, " +
					"password TEXT, teamId INTEGER);");
			Cursor cursor = db.rawQuery("SELECT * FROM peeps WHERE username='" + textValue + "' AND teamId IS NOT NULL;" , null);
			cursor.moveToFirst();
			//Toast.makeText(getApplicationContext(), Boolean.toString((cursor!=null && cursor.getCount()>0)), Toast.LENGTH_LONG).show();
			
			return (cursor!=null && cursor.getCount()>0) ? true : false;
		}
	}
	
	public static class DynamicMenu extends Fragment {
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_dynamicmenu_main,
			        container, false);
	        return view;
	    }
	}
	
	public static class ChallengeMenu extends Fragment {
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_challengemenu_main,
			        container, false);
	        return view;
	    }
	}

	public static class TeamMenu extends Fragment {
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			
			View view = inflater.inflate(R.layout.fragment_teammenu_main,
			        container, false);
	        return view;
	    }
	}
	
	public static class MemberTeamMenu extends Fragment {
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			
			View view = inflater.inflate(R.layout.fragment_memberteammenu_main,
			        container, false);
	        return view;
	    }
	}
	
	/**
	 * Sets up and fills database with dummy data in teams, peeps, highscores tables in sprekIGjovik database, and challenges and challengepoles in 
	 * PoleDB database. For testing purposes, until external server.
	 */
	public void setUpDatabase() {
		
		//CHECK DATABASE FIRST
		
		//---------------- OPENS/CREATES SPREKIGJOVIK & TABLES --------------------//
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS teams(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS peeps(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, " +
				"password TEXT, teamId INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS highscores(id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, " +
				"challengeId INTEGER, score INTEGER);");
		
		//---------------- CHECKS/ADDS TEAMS --------------------------------------//
		Cursor cursor = db.rawQuery("SELECT * FROM teams;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			
			//---------------- ADDS TEMP TEAMS --------------------------------------//
			List<String[]> dummyTeams = new ArrayList<String[]>();
			dummyTeams.add(new String[]{"Prestige Development", "Bedriftlaget til Prestige Development"});
			dummyTeams.add(new String[]{"Gjøvik Orientering", "Orienteringsklubben i Gjøvik og omegn"});
			
			for(String[] row : dummyTeams){
				db.execSQL("INSERT INTO teams(name, description) VALUES('"+ row[0]+ "','" + row[1] +
						"');");
			}
		}
		
		//---------------- CHECKS/ADDS PEEPS --------------------------------------//
		cursor = db.rawQuery("SELECT * FROM peeps;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			
			//---------------- ADDS TEMP PEOPLE -------------------------------------//
			List<String[]> dummyPeeps = new ArrayList<String[]>();
			//Adds members to Prestige Development team
			dummyPeeps.add(new String[]{"Christopher", "1"});
			dummyPeeps.add(new String[]{"Khai", "1"});
			//Adds members to Gjøvik Orientering team
			dummyPeeps.add(new String[]{"Jehans", "2"});
			dummyPeeps.add(new String[]{"Røise", "2"});
			dummyPeeps.add(new String[]{"Espen", "2"});
			
			for(String[] row : dummyPeeps){
				db.execSQL("INSERT INTO peeps(username, password, teamId) VALUES('"+ row[0]+ "', '123456', '" + row[1] +
						"');");
			}
		}
		
		//---------------- CHECKS/ADDS HIGHSCORES ---------------------------------//
		cursor = db.rawQuery("SELECT * FROM highscores;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			
			//---------------- ADDS TEMP HIGHSCORES ----------------------------------//
			List<String[]> dummyScores = new ArrayList<String[]>();
			dummyScores.add(new String[]{"1", "1", "406"});
			dummyScores.add(new String[]{"2", "1", "253"});
			dummyScores.add(new String[]{"1", "3", "703"});
			dummyScores.add(new String[]{"3", "2", "1504"});
			dummyScores.add(new String[]{"1", "3", "4064"});
			dummyScores.add(new String[]{"3", "2", "391"});
			dummyScores.add(new String[]{"2", "1", "112"});
			
			for(String[] row : dummyScores){
				db.execSQL("INSERT INTO highscores(userId, challengeId, score) VALUES('"+ row[0]+ "','" + row[1] + 
						"', '" + row[2] + "');");
			}
		}
		
		//---------------- OPENS/CREATES POLEDB & TABLES --------------------------//
		db = openOrCreateDatabase("PoleDB", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS challenges(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, difficulty INTEGER);");
//				db.execSQL("CREATE TABLE IF NOT EXISTS poles(id INTEGER PRIMARY KEY AUTOINCREMENT, poleId INTEGER, difficulty INTEGER, code TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS challengePoles(id INTEGER PRIMARY KEY AUTOINCREMENT, challengeId INTEGER, poleId INTEGER);");
		
		
		//---------------- CHECKS/ADDS CHALLENGES -----------------------------//
		cursor = db.rawQuery("SELECT * FROM challenges;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			
			//---------------- ADDS TEMP CHALLENGES -----------------------------------//
			List<String[]> dummyChallenges = new ArrayList<String[]>();
			dummyChallenges.add(new String[]{"Opp til toppen", "Ta alle postene opp til Gjøviktoppen.", "3"});
			dummyChallenges.add(new String[]{"Langs Mjøsa", "Ta alle postene langs Mjøsa.", "2"});
			dummyChallenges.add(new String[]{"Til sykehuset og tilbake", "Ta postene frem til sykehuset, og de samme postene tilbake.", "1"});
			
			for(String[] row : dummyChallenges){
				db.execSQL("INSERT INTO challenges(name, description, difficulty) VALUES('"+ row[0]+ "','" + row[1] + 
						"', '" + row[2] + "');");
			}
		}
		
		//---------------- CHECKS/ADDS CHALLENGEPOLES -----------------------------//
		cursor = db.rawQuery("SELECT * FROM challengePoles;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			
			//---------------- ADDS TEMP CHALLENGEPOLES -------------------------------//
			List<String[]> dummyChallengePoles = new ArrayList<String[]>();
			//Adds first challenge
			dummyChallengePoles.add(new String[]{"1", "1"});
			dummyChallengePoles.add(new String[]{"1", "2"});
			dummyChallengePoles.add(new String[]{"1", "4"});
			//Adds second challenge
			dummyChallengePoles.add(new String[]{"2", "1"});
			dummyChallengePoles.add(new String[]{"2", "2"});
			dummyChallengePoles.add(new String[]{"2", "3"});
			//Adds third challenge
			dummyChallengePoles.add(new String[]{"3", "1"});
			dummyChallengePoles.add(new String[]{"3", "2"});
			dummyChallengePoles.add(new String[]{"3", "1"});
			dummyChallengePoles.add(new String[]{"3", "2"});
			
			for(String[] row : dummyChallengePoles){
				db.execSQL("INSERT INTO challengePoles(challengeId, poleId) VALUES('"+ row[0]+ "','" + row[1] + "');");
			}
		}
		cursor.close();
	}
	
	/**
	 * Creating or opening DB for creation and populating
	 */
	public void createDB()	{
		
		String tableNamePole			= 	"pole";				//contains all poles
		String tableNameArea			=	"area";				//area for poles
		String tableNameSessionPole		= 	"sessionPole";		//table for poles for each session
		String tableNameChallengePole	=	"challengePole";	//challenges and belonging poles
		String tableNameChallenge		=	"challenge";		//all available challenges
		
		myDB = this.openOrCreateDatabase("PoleDB", MODE_PRIVATE, null);		//database for poles
		
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
							+ tableNamePole
							+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT, longitude TEXT, " +
							"latitude TEXT, areaId INTEGER, level INTEGER);");
		
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
							+ tableNameArea
							+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT);");
		
		
//			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
//								+ tableNameChallenge
//								+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT);");
		
		sessionDB = this.openOrCreateDatabase("PoleSession", MODE_PRIVATE, null);	//database for session poles
		sessionDB.execSQL("CREATE TABLE IF NOT EXISTS "
								+ tableNameSessionPole
								+"(id INTEGER PRIMARY KEY AUTOINCREMENT, poleId TEXT, isVisited TEXT);");

		Cursor c = myDB.rawQuery("SELECT * FROM "+tableNamePole, null);
		c.moveToFirst();

							//checks if the table is containing any data, if not, populates them
		if(c == null || c.getCount()==0)	{
			insertDataDB();
		}
		c.close();
	}
	
	/**
	 * If database for all poles is empty this functions populates the tables.
	 */
	
	public void insertDataDB()	{
		try	{
			
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('1','10.671766', '60.797149');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('2','10.676271', '60.797796');");
			
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('2','10.675777', '60.794528');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('3','10.671766', '60.797149');");

				
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('4','10.677858', '60.793925');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('5','10.667082', '60.790810');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('6','10.661453', '60.792476');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('7','10.669674', '60.793058');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('11','10.685185', '60.793269');");
			 
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('12','10.690588', '60.792518');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('13','10.689552', '60.794982');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('14','10.689520', '60.796357');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('15','10.686478', '60.797662');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('21','10.666328', '60.795006');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('22','10.667960', '60.795980');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('30','10.691870', '60.794231');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('31','10.687084', '60.794152');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('37','10.680915', '60.793652');");
			 		 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('41','10.680501', '60.798851');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('46','10.698281', '60.798292');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('47','10.664888', '60.794479');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('48','10.672168', '60.795409');");
			 
			 myDB.execSQL("INSERT INTO "
				     + tableNamePole
				     + " (name, longitude, latitude)"
				     + " VALUES ('49','10.674817', '60.793038');");
			 
			 
				Context context = getApplicationContext();
				CharSequence text = "Insertion of data successfull";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			
		}
		catch	(Exception e)	{
			e.printStackTrace();
		}
	}
	
	public void dynamicTour(View view)        {
		if(checkLogin()){
			Intent intent = new Intent(this, DynamicTour.class);
	        startActivity(intent);
		}
	}
	
	
	/**
	 * Static way to get context
	 * @return MainActivity.context
	 */
	public static Context getContext() {
		return context;
	}
	
	public void allTeams(View v){
		new SelectTeamDialogFragment().show(getFragmentManager(), "teams");
	}
	
	public void viewTeam(View v){
		Intent intent = new Intent(this, DisplayTeam.class);
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		db = openOrCreateDatabase("sprekIGjovik", MODE_PRIVATE,null);
		Cursor cursor = db.rawQuery("SELECT name FROM teams WHERE id = (SELECT teamId FROM peeps WHERE username LIKE '" + username + "');", null);
		cursor.moveToFirst();
	   	intent.putExtra("team", cursor.getString(0)); 
   		startActivity(intent);	
   		cursor.close();
	}
	
	public void selectChallenge(View v){
		if(checkLogin()){
			Intent intent = new Intent(this, Challenges.class);
			startActivity(intent);
		}
	}
	
	public void showHighscores(View v){
		Intent intent = new Intent(this, TeamHighscore.class);
		startActivity(intent);
	}
	
	public void showLog(View v){
		if(checkLogin()){
			new SelectTeamDialogFragment().show(getFragmentManager(), "teams");
		}
	}
	
	
	
	//------------------GPS---------------------------------------------------------------------------------------------
	
	//check whether GPS is enabled and if not prompts user to activate it.
	private void CheckEnableGPS()	{
		String provider = Settings.Secure.getString(getContentResolver(),
		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(!provider.equals("")){
		   //GPS Enabled
		Toast.makeText(getApplicationContext(), "GPS Enabled: " + provider,
			Toast.LENGTH_LONG).show();
		}else{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}	
	}

	
    public void clearSessionDB()	{
        sessionDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
	    sessionDB.execSQL("DELETE FROM sessionPole");
	    sessionDB.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='sessionPole'");
	    
	    Log.d("Clearing session table", "MainActivity: line 611");


    }

	
	public Boolean checkLogin(){
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("UserName", "");
		
		if(username.equals("")){
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			return false;
		}
		return true;
	}
}