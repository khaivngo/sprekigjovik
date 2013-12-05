package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
	private static Context context;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_main);
		
		setUpDatabase();
		
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
	 * Sets up and fills database with dummy data in teams table. For testing purposes.
	 */
	public void setUpDatabase() {
		
		//CHECK DATABASE FIRST
		
		List<String[]> dummyTeams = new ArrayList<String[]>();
		dummyTeams.add(new String[]{"Prestige Development", "Bedriftlaget til Prestige Development"});
		dummyTeams.add(new String[]{"Gjøvik Orientering", "Orienteringsklubben i Gjøvik og omegn"});
		
		//---------------- ADDS TEMP PEOPLE -------------------------------------//
		List<String[]> dummyPeeps = new ArrayList<String[]>();
		//Adds members to Prestige Development team
		dummyPeeps.add(new String[]{"Christopher", "1"});
		dummyPeeps.add(new String[]{"Khai", "1"});
		//Adds members to Gjøvik Orientering team
		dummyPeeps.add(new String[]{"Jehans", "2"});
		dummyPeeps.add(new String[]{"Røise", "2"});
		dummyPeeps.add(new String[]{"Espen", "2"});
		
		//---------------- ADDS TEMP HIGHSCORES ----------------------------------//
		List<String[]> dummyScores = new ArrayList<String[]>();
		dummyScores.add(new String[]{"1", "1", "406"});
		dummyScores.add(new String[]{"2", "1", "253"});
		dummyScores.add(new String[]{"1", "3", "703"});
		dummyScores.add(new String[]{"3", "2", "1504"});
		dummyScores.add(new String[]{"1", "3", "4064"});
		dummyScores.add(new String[]{"3", "2", "391"});
		dummyScores.add(new String[]{"2", "1", "112"});
		
		//---------------- ADDS TEMP CHALLENGES -----------------------------------//
		List<String[]> dummyChallenges = new ArrayList<String[]>();
		dummyChallenges.add(new String[]{"Opp til toppen", "Ta alle postene opp til Gjøviktoppen.", "3"});
		dummyChallenges.add(new String[]{"Langs Mjøsa", "Ta alle postene langs Mjøsa.", "2"});
		dummyChallenges.add(new String[]{"Til sykehuset og tilbake", "Ta postene frem til sykehuset, og de samme postene tilbake.", "1"});
		
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
		
		//---------------- ADDS TEMP POLES-------------------- --------------------//
		List<String[]> dummyPoles = new ArrayList<String[]>();
		dummyPoles.add(new String[]{"1", "1", "ABC"});
		dummyPoles.add(new String[]{"2", "2", "DEF"});
		dummyPoles.add(new String[]{"3", "1", "GHI"});
		dummyPoles.add(new String[]{"4", "3", "JKL"});
		
		
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
			for(String[] row : dummyTeams){
				db.execSQL("INSERT INTO teams(name, description) VALUES('"+ row[0]+ "','" + row[1] +
						"');");
			}
		}
		
		//---------------- CHECKS/ADDS PEEPS --------------------------------------//
		cursor = db.rawQuery("SELECT * FROM peeps;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			for(String[] row : dummyPeeps){
				db.execSQL("INSERT INTO peeps(username, password, teamId) VALUES('"+ row[0]+ "', '123456', '" + row[1] +
						"');");
			}
		}
		
		//---------------- CHECKS/ADDS HIGHSCORES ---------------------------------//
		cursor = db.rawQuery("SELECT * FROM highscores;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			for(String[] row : dummyScores){
				db.execSQL("INSERT INTO highscores(userId, challengeId, score) VALUES('"+ row[0]+ "','" + row[1] + 
						"', '" + row[2] + "');");
			}
		}
		//---------------- OPENS/CREATES POLEDB & TABLES --------------------------//
		db = openOrCreateDatabase("PoleDB", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS challenges(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, difficulty INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS poles(id INTEGER PRIMARY KEY AUTOINCREMENT, poleId INTEGER, difficulty INTEGER, code TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS challengePoles(id INTEGER PRIMARY KEY AUTOINCREMENT, challengeId INTEGER, poleId INTEGER);");
		
		//---------------- CHECKS/ADDS CHALLENGEPOLES -----------------------------//
		cursor = db.rawQuery("SELECT * FROM challenges;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			for(String[] row : dummyChallenges){
				db.execSQL("INSERT INTO challenges(name, description, difficulty) VALUES('"+ row[0]+ "','" + row[1] + 
						"', '" + row[2] + "');");
			}
		}
		
		//---------------- CHECKS/ADDS CHALLENGEPOLES -----------------------------//
		cursor = db.rawQuery("SELECT * FROM poles;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			for(String[] row : dummyPoles){
				db.execSQL("INSERT INTO poles(poleId, difficulty, code) VALUES('"+ row[0]+ "','" + row[1] + 
						"', '" + row[2] + "');");
			}
		}
		
		//---------------- CHECKS/ADDS CHALLENGEPOLES -----------------------------//
		cursor = db.rawQuery("SELECT * FROM challengePoles;" , null);
		cursor.moveToFirst();
		
		if(cursor==null || cursor.getCount()==0){
			for(String[] row : dummyChallengePoles){
				db.execSQL("INSERT INTO poles(challengeId, poleId) VALUES('"+ row[0]+ "','" + row[1] + "');");
			}
		}
		//-------------------------------------------------------------------------//
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
	}
}