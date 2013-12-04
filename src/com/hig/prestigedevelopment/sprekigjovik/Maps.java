package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Maps extends FragmentActivity {
	
	private SQLiteDatabase myDB = null;
	String lastMarker = null;
	Cursor cursorResume = null;
	
	/**
     * author: http://stackoverflow.com/questions/15098243/android-app-keeps-crashing-when-using-googlemap-v2
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
 
      setUpMapIfNeeded();
      
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        
//       cursorResume = myDB.rawQuery("SELECT poleId, isVisited FROM sessionPole WHERE poleId = "+lastMarker, null);
//        
//        if(cursorResume.getString(1) == "1")	{
//        	addMarker(lastMarker+1);
//        	
//        }
//        
        
        
    }
    

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        
    	ArrayList<String> selection = new ArrayList<String>();		//storing retrieved strings about DB data.
    	ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    	Intent i = getIntent();										
    	selection = i.getStringArrayListExtra("selected");			//getting intent data

        myDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
        Cursor cursor = myDB.rawQuery("SELECT * FROM sessionPole", null);	//checking if database contains data
        
    	for(String s : selection)	{						//loops through all fetched poles
    		String[] parts = s.split(":");					//splits/explodes each string on :
    		String ID = parts[0];							//saving 1/3 of the string into variable
    		
    		String lon = parts[1];							//saving 2/3 of string
    		double doubleLon = Double.parseDouble(lon);		//converting string to double for LatLng constructor
    		
    		String lat = parts[2];							//saving 3/3 of string
    		double doubleLat = Double.parseDouble(lat);		//converting string to double for LatLng constructor
    							
    		if(cursor == null || cursor.getCount() == 0)	{	//checks if cursor contains any data -> table empty -> populate
    			myDB.execSQL("INSERT INTO sessionPole(poleId) VALUES ("+ID+");");
    			
    			//TESTING
    			Log.d("Each list item ID: ", ID);
    			Log.d("Each list item LON: ",lon);
    			Log.d("Each list item LAT: ",lat);
    		}
    											//for each item in array it add a marker on map
        	Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(doubleLat, doubleLon))
        				.title(ID)
        				.snippet("Stolpe infoWindow"));
        	mMarkerArray.add(marker);
    	}
    	
    	mMap.setMyLocationEnabled(true);		//changed out authors implementation for showing location.
        										// is now able to locate users position.    
    											
    											//listens for a infowindow click by user and redirects user
    											// to DialogActivity for pole code prompt, also passing
    											// marker title -> ID of pole
    	 mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
             @Override
             public void onInfoWindowClick(Marker marker) {
 			    Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
 			    intent.putExtra("markerId", marker.getTitle());
 			    startActivity(intent);
             }
         });
    }
    
    
}