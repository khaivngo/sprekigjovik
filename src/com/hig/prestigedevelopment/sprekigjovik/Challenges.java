/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays all challenges through a navigation drawer, displays challenge
 * information like description, amount of poles, difficulty.
 * Displays buttons for highscore and start challenge 
 * @author Chris
 * 
 */
public class Challenges extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mChallengeTitles;
    
    private SQLiteDatabase db;
    
    private static Context context;
    
//    private Boolean isTeam = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        
        setContentView(R.layout.activity_challenges);
        
        mTitle = mDrawerTitle = getTitle();
        mChallengeTitles = getChallengeNames();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.highscore_list_item, mChallengeTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        //Starts with drawer open
        mDrawerLayout.openDrawer(findViewById(R.id.left_drawer));
        this.findViewById(R.id.left_drawer);
    }
  
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        //Displays the right challenge if user presses back on a different highscore
        if(resultCode == Activity.RESULT_OK){
	        selectItem(Arrays.asList(mChallengeTitles).indexOf(data.getStringExtra("title")));
        }
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
		//Profile button on action bar
	    switch (item.getItemId()) {
	    case R.id.action_profile:
	    	intent = new Intent(this, Profile.class);
	    	startActivity(intent);
	    	break;
	    default:
	      break;
	    }
	    //Opens drawer if user presses app icon
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	         return true;
	    }
	    
	    return true;
	}	    

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putInt(ChallengeFragment.ARG_HIGHSCORE_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mChallengeTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ChallengeFragment extends Fragment {
        public static final String ARG_HIGHSCORE_NUMBER = "highscore_number";
        public ChallengeFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.challenges_fragment, container, false);
            int i = getArguments().getInt(ARG_HIGHSCORE_NUMBER);
            String title = getChallengeNames()[i];

            getActivity().setTitle(title);
            
            setUpChallengeData(rootView, title);       	

            return rootView;
        }
        
        /**
         * Displays challenge data on the layout. Displays total poles, description and difficulty
         * @param view Current view
         * @param name Name of the challenge
         */
        public static void setUpChallengeData(View view, String name) {
        	
        	String [] queryCName = { name };
        	
            String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
        	
        	SQLiteDatabase db = SQLiteDatabase.openDatabase(path + "PoleDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        	Cursor cursor = db.rawQuery("SELECT Count(*) FROM challengePoles WHERE challengeId = (SELECT id FROM challenges WHERE name = ?);)", queryCName);
        	cursor.moveToFirst();
        	
        	//Displays amount of poles
        	TextView poles = (TextView) view.findViewById(R.id.challengePoles);
        	poles.setText(cursor.getString(0) + " total poles.");
        	
        	cursor = db.rawQuery("SELECT * FROM challenges WHERE name LIKE ? ;)", queryCName);
        	cursor.moveToFirst();
        	
        	//Displays description
        	TextView description = (TextView) view.findViewById(R.id.challengeDescription);
        	description.setText(cursor.getString(2));
        	
        	//Displays difficulty level
        	TextView difficulty = (TextView) view.findViewById(R.id.challengeDifficulty);
        	difficulty.setText("Difficulty level: " + cursor.getString(3));
        	
        	cursor.close();
        	
        }
    }
    
    /**
     * Static function to return highscorenames as a string-array.
     * Has to be static for fragment-implementations
     * @return string-array of highscore-names
     */
    public static String[] getChallengeNames() {
    	List<String> names = new ArrayList<String>();
    	
    	String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
    	
    	SQLiteDatabase sdb = SQLiteDatabase.openDatabase(path + "PoleDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    	Cursor cursor = sdb.rawQuery("SELECT name FROM challenges", null);
    	
    	//Populates list with challenge names
    	while(cursor.moveToNext()){
    		names.add(cursor.getString(0));
    	}
    	
    	cursor.close();
    	
		return names.toArray(new String[names.size()]);
	}

    /**
     * Static way to get context
     * @return TeamHighscore.context
     */
    public static Context getContext() {
    	return context;
    }
    
    /**
     * Starts a highscore activity on the challenge name
     * @param v Current view
     */
    public void showHighscores(View v){
    	Intent intent = new Intent(this, TeamHighscore.class);
    	intent.putExtra("challengeName", mTitle.toString());
    	startActivityForResult(intent, 500);
    }
    
    /**
     * Starts the current challenge on the map activity.
     * @param v Current view
     */
    public void startChallenge(View v){
    	
    	List<String> selectedPoles = new ArrayList<String>();
    	
    	db = openOrCreateDatabase("PoleDB", MODE_PRIVATE,null);

	    Cursor cursor = db.rawQuery("SELECT name, longitude, latitude FROM pole p " +
	    							"JOIN challengePoles c ON c.poleId = p.name " +
	    							"WHERE c.challengeId = (SELECT id FROM challenges WHERE name LIKE ?);", new String[] {mTitle.toString()});
	    
	    //Populates the list with poles
	    while(cursor.moveToNext())	{
	    	selectedPoles.add(cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
	    }
	    
	    cursor.close();
	    
	    //Adds challenge name to sharedpreferences - for Maps activity to know this is a challenge
	    // and not a dynamic tour.
	    SharedPreferences sharedTime = getSharedPreferences("time", Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedTime.edit();
	     
	    editor.putString("ChallengeName", mTitle.toString());
	    editor.commit();
	    
	    Intent intent = new Intent(this, Maps.class);
	    intent.putStringArrayListExtra("selected", (ArrayList<String>) selectedPoles);
	    startActivity(intent);
    }
}