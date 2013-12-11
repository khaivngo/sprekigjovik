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
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Displays a higscore list for the user, containing all score from all users unless teamname is set.
 * If teamname is set, it will only display highscores from team members. 
 * On back button the activity will return to the challenge the user is currently viewing highscores
 * from, if the user came from challenges.
 * 
 * Challengenames is displayed in a navigation drawer, and will display the current highscores of the 
 * challenge if pressed
 */
public class TeamHighscore extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mChallengeTitles;
    
    private SQLiteDatabase db;
    private static Context context;
    private static String teamName;
    private static String challengeName;
    
//    private Boolean isTeam = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        
        //Checks if intent extras is set
        Intent intent = getIntent();
        teamName = intent.getStringExtra("teamName");
        challengeName = intent.getStringExtra("challengeName");

        
        setContentView(R.layout.activity_highscore);
        
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
        
        //Opens the right highscore if challengename is set
        if (savedInstanceState == null) {
            List<String> list = Arrays.asList(mChallengeTitles);
        	if(challengeName != null && list.contains(challengeName)) {
            	selectItem(Arrays.asList(mChallengeTitles).indexOf(challengeName));
            } else {
            	selectItem(0);
            }
        }
        
        //Opens drawer on startup if no challengename is set
        if(challengeName == null){
	        mDrawerLayout.openDrawer( findViewById(R.id.left_drawer));
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return true;
	}
	
	/**
	 * Makes sure the selected challenge in highscore is the same when returning
	 */
	@Override
	public void onBackPressed() {
		Log.d("hei11", mTitle.toString());
		Intent intent = new Intent();
        intent.putExtra("title", mTitle.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
	    super.onBackPressed();
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
	    //Opens action bar if app icon is pressed
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
            View rootView = inflater.inflate(R.layout.highscore_fragment, container, false);
            int i = getArguments().getInt(ARG_HIGHSCORE_NUMBER);
            String title = getChallengeNames()[i];

            getActivity().setTitle(title);
            
            //Gets highscores
            String[] highscores = getHighscores(title);
            TextView tv;
            
            //----------------------------- SETS FIRST ROW WITH METADATA ----------------------------//
            final TableLayout t = (TableLayout) rootView.findViewById(R.id.my_table);
            final TableRow row = (TableRow) inflater.inflate(R.layout.highscore_row_item, null);
            
            //Cell 1 "#"
            tv = (TextView) row.findViewById(R.id.cell_1);
            tv.setText("#");
            
            //Cell 2 "NAME"
            tv = (TextView) row.findViewById(R.id.cell_2);
            tv.setText(getString(R.string.highscore_name));
            
            //Cell 3 "SCORE"
            tv = (TextView) row.findViewById(R.id.cell_3);
            tv.setText(getString(R.string.score));
            t.addView(row);
            
            tv = new TextView(context);
            tv.setBackgroundColor(Color.parseColor("#80808080"));
            tv.setHeight(2);
            t.addView(tv);
            registerForContextMenu(row);
            
          //----------------------------- SETS HIGHSCORE DATA ----------------------------//
            for(int j = 0; j < highscores.length; j++){
            	String[] s = highscores[j].split(":");
            	final TableLayout table = (TableLayout) rootView.findViewById(R.id.my_table);
                final TableRow tr = (TableRow) inflater.inflate(R.layout.highscore_row_item, null);
            	
                //Cell 1 "NUMBER"
            	tv = (TextView) tr.findViewById(R.id.cell_1);
                tv.setText(Integer.toString(j+1));
                
                //Cell 2 "NAME"
                tv = (TextView) tr.findViewById(R.id.cell_2);
                tv.setText(s[0]);
                
                //Cell 3 "SCORE"
                tv = (TextView) tr.findViewById(R.id.cell_3);
                tv.setText(mod(Integer.parseInt(s[1])));
                table.addView(tr);
                
                tv = new TextView(context);
                tv.setBackgroundColor(Color.parseColor("#80808080"));
                tv.setHeight(2);
                table.addView(tv);

                //Register row
                registerForContextMenu(tr);
            }

            return rootView;
        }
        /**
         * Converts seconds in integer, to format "xm ys" to display minutes and seconds as a string
         * @param x seconds
         * @return String displaying minutes and seconds
         */
        private String mod(int x){
        	int result = x % 60;
        	int mins = (result < 0) ? result + 60 : result;

        	return x/60 + "m " + mins + "s";
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
    	
    	//Populates list with challengenames
    	while(cursor.moveToNext()){
    		names.add(cursor.getString(0));
    	}
    	cursor.close();
    	
		return names.toArray(new String[names.size()]);
	}
    /**
     * Static function to return highscores as a string array
     * Has to be static for fragment-implementations.
     * Will get team data if team is set.
     * @param i name of the highscore
     * @return
     */
    public static String[] getHighscores(String title){
    	List<String> highscores = new ArrayList<String>();
    	String[] queryTitle = {title};
    	String[] queryData = new String[2]; 	// 0 = challengeId, 1 = teamName	
    	String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
    	
    	SQLiteDatabase sdb = SQLiteDatabase.openDatabase(path + "PoleDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    	Cursor cursor = sdb.rawQuery("SELECT id FROM challenges WHERE name LIKE ?", queryTitle);
    	cursor.moveToFirst();
    	queryData[0] = cursor.getString(0);
    	queryData[1] = teamName;
    	
    	sdb = SQLiteDatabase.openDatabase(path + "sprekIGjovik", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    	
    	//Starts query with a join
    	String query = "SELECT p.username, h.score FROM highscores h " +
    			"INNER JOIN peeps p ON h.userId=p.id ";
    	//!teamName.equals("") && 
    	if(teamName != null){		//Adds join on team if team is set
    		if(!teamName.equals("")){
    				//Adds teamname to bind value
    			query += "INNER JOIN teams t ON p.teamId=t.id ";
    		}
    	}		
    	query += "WHERE h.challengeId LIKE ? ";
    	
    	if(teamName != null){		//Adds team check if team is set
    		if(!teamName.equals("")){
    			query += "AND t.name LIKE ? ";
    		}
    	}
    	query += "ORDER BY h.score ASC ";
    	
    	//Performs the query with the right data based on if team is set or
    	// not
    	if(teamName == null){
    		cursor = sdb.rawQuery(query, new String[] {queryData[0]});
    	} else {
    		cursor = sdb.rawQuery(query, queryData);
    	}
    	
    	//Populates list with highscore data
    	while(cursor.moveToNext()){
    		highscores.add(cursor.getString(0) + ":" + cursor.getString(1));
    	}
    	cursor.close();
    	
		return highscores.toArray(new String[highscores.size()]);
    }

    /**
     * Static way to get context
     * @return TeamHighscore.context
     */
    public static Context getContext() {
    	return context;
    }
}