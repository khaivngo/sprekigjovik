package com.hig.prestigedevelopment.sprekigjovik;

import java.util.Locale;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * @author KhaiVanNgo
 * 
 * Main responsible for iniating creation for databases, views, menues
 *
 */

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
	private SQLiteDatabase myDB= null;
	private SQLiteDatabase SessionDB= null;	
	final String tableNamePole			= 	"pole";
	final String tableNameArea			=	"area";
	final String tableNameChallengePole	=	"challengePole";
	final String tableNameChallenge		=	"challenge";
	
	
//------------------------onCREATE--------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        //CheckEnableGPS();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
		
		createDB();
		
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
				case 2: return new TeamMenu();
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
	
	/**
	 * Creating or opening DB for creation and populating
	 */

	public void createDB()	{
		try	{
			String tableNamePole			= 	"pole";				//contains all poles
			String tableNameArea			=	"area";				//area for poles
			String tableNameSessionPole		= 	"sessionPole";		//table for poles for each session
			String tableNameChallengePole	=	"challengePole";	//challenges and belonging poles
			String tableNameChallenge		=	"challenge";		//all available challenges
			
			myDB = this.openOrCreateDatabase("PoleDB", MODE_PRIVATE, null);		//database for poles
			
			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
								+ tableNamePole
								+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT, longitude TEXT, " +
								"latitude TEXT, areaId INTEGER);");
			
			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
								+ tableNameArea
								+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT);");
			
			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
								+ tableNameChallengePole
								+"(poleId INTEGER, challengeId INTEGER);");
			
			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
								+ tableNameChallenge
								+"(id INTEGER PRIMARY KEY  AUTOINCREMENT, name TEXT);");
			
			SessionDB = this.openOrCreateDatabase("PoleSession", MODE_PRIVATE, null);	//database for session poles
			SessionDB.execSQL("CREATE TABLE IF NOT EXISTS "
									+ tableNameSessionPole
									+"(id INTEGER PRIMARY KEY AUTOINCREMENT, poleId TEXT, isVisited TEXT);");

			Cursor c = myDB.rawQuery("SELECT * FROM "+tableNamePole, null);
			c.moveToFirst();

								//checks if the table is containing any data, if not, populates them
			if(c == null || c.getCount()==0)	{
				insertDataDB();
			}

		}finally {			 	//TEST PURPOSE
			
			Context context = getApplicationContext();
			CharSequence text = "DB creation successfull!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
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
           Intent intent = new Intent(this, DynamicTour.class);
           startActivity(intent);
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
	
}
