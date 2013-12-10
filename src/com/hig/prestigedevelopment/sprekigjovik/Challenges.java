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
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
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
        
        Intent intent = getIntent();
        
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
        
        this.findViewById(R.id.left_drawer);
    }
    
    @Override
    protected void onDestroy() {

        db.close();

        super.onDestroy();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == Activity.RESULT_OK){
//        	if(resultCode == RESULT_OK){
        
	        Log.d("hei2", data.getStringExtra("title"));
	        selectItem(Arrays.asList(mChallengeTitles).indexOf(data.getStringExtra("title")));
//        	}	
        } else {
        	Log.d("Fail", resultCode + "");
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
        
        public static void setUpChallengeData(View view, String name) {
        	
        	String [] queryCName = { name };
        	
            String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
        	
        	SQLiteDatabase db = SQLiteDatabase.openDatabase(path + "PoleDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        	Cursor cursor = db.rawQuery("SELECT Count(*) FROM challengePoles WHERE challengeId = (SELECT id FROM challenges WHERE name = ?);)", queryCName);
        	cursor.moveToFirst();
        	
        	TextView poles = (TextView) view.findViewById(R.id.challengePoles);
        	poles.setText(cursor.getString(0) + " total poles.");
        	
        	cursor = db.rawQuery("SELECT * FROM challenges WHERE name LIKE ? ;)", queryCName);
        	cursor.moveToFirst();
        	Log.d(cursor.getString(2), cursor.getString(3));
        	TextView description = (TextView) view.findViewById(R.id.challengeDescription);
        	description.setText(cursor.getString(2));
        	
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
    
    public void showHighscores(View v){
    	Intent intent = new Intent(this, TeamHighscore.class);
    	intent.putExtra("challengeName", mTitle.toString());
    	startActivityForResult(intent, 500);
    }
    
    public void startChallenge(View v){
    	
    	List<String> selectedPoles = new ArrayList<String>();
    	
    	db = openOrCreateDatabase("PoleDB", MODE_PRIVATE,null);
    	Log.d("heiheiheiehie", mTitle.toString());
	    Cursor cursor = db.rawQuery("SELECT name, longitude, latitude FROM pole p " +
	    							"JOIN challengePoles c ON c.poleId = p.name " +
	    							"WHERE c.challengeId = (SELECT id FROM challenges WHERE name LIKE ?);", new String[] {mTitle.toString()});
	    
	    
	    while(cursor.moveToNext())	{
	    	selectedPoles.add(cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
	    }
	    
	    cursor.close();
	    
	    SharedPreferences sharedTime = getSharedPreferences("time", Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedTime.edit();
	     
	    editor.putString("ChallengeName", mTitle.toString());
	    editor.commit();
	    
	    Intent intent = new Intent(this, Maps.class);
	    intent.putStringArrayListExtra("selected", (ArrayList<String>) selectedPoles);
	    startActivity(intent);
    }
}