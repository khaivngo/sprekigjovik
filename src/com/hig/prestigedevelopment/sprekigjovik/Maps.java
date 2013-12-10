package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Maps extends FragmentActivity {
	
	private SQLiteDatabase poleDB = null;
	private SQLiteDatabase sessionDB = null;
	private SQLiteDatabase challengeDB = null;


	String currentMarker = null;
	Cursor cursorResume = null;
	Marker testMarker = null;
	Marker marker;
	ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

	private long start = 0;

	
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
      groundOverlay();
      setStartTime();
      
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        nextPole();
        
//        
//       sessionDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
//       cursorResume = sessionDB.rawQuery("SELECT poleId, isVisited FROM sessionPole WHERE poleId = "+currentMarker, null);
//       cursorResume.moveToFirst(); 
//       
//       if(cursorResume != null && cursorResume.getCount() > 0){
//    	   Log.d("Inside first resume if", "heihew");
//    	 
//    	   if(cursorResume.getString(1) == null)
//    		   Log.d("Pole visited", "pole null");
//
//    	   if()	{
//        	   Log.d("Inside second resume if", "heihew");
//

//            
//    		   nextPole();
//    	   }
//       }
     
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
       // if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
         //   if (mMap != null) {
                setUpMap();
          //  }
       // }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        
    	ArrayList<String> selection = new ArrayList<String>();		//storing retrieved strings about DB data.
    	Intent i = getIntent();										
    	selection = i.getStringArrayListExtra("selected");			//getting intent data

        sessionDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
		Cursor loopCursor = sessionDB.rawQuery("SELECT * FROM sessionPole", null);	//checking if database contains data
        
    	for(String s : selection)	{						//loops through all fetched poles
    		String[] parts = s.split(":");					//splits/explodes each string on :
    		String ID = parts[0];							//saving 1/3 of the string into variable
    		
//    		String lon = parts[1];							//saving 2/3 of string
//    		double doubleLon = Double.parseDouble(lon);		//converting string to double for LatLng constructor
//    		
//    		String lat = parts[2];							//saving 3/3 of string
//    		double doubleLat = Double.parseDouble(lat);		//converting string to double for LatLng constructor
    							
    		if(loopCursor == null || loopCursor.getCount() == 0)	{	//checks if cursor contains any data -> table empty -> populate
    			sessionDB.execSQL("INSERT INTO sessionPole(poleId) VALUES ("+ID+");");
    			//TESTING
    			Log.d("Each list item ID: ", ID);
//    			Log.d("Each list item LON: ",lon);
//    			Log.d("Each list item LAT: ",lat);
    		}
    		
    	}
    	
        Cursor polesCursor = sessionDB.rawQuery("SELECT * FROM sessionPole WHERE isVisited is NULL LIMIT 1", null);
        polesCursor.moveToFirst();
    									//all poles have been visited
    	if(polesCursor == null || polesCursor.getCount() == 0){
    			Log.d("Is done", "setUpMap line 155");
    			isFinished();
    	}
    	else	{			//there is unvisited poles left
			Log.d("Not done", "setUpMap line 159");

    	nextPole();
				
    	}

    	mMap.setMyLocationEnabled(true);		//changed out authors implementation for showing location.
        										// is now able to locate users position.    
    }
    
    /**
     * Displaying next pole if there is any unvisited for the session
     */
    
    public void nextPole(){
    	//ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
		poleDB = openOrCreateDatabase("PoleDB", MODE_PRIVATE, null);		//database for poles
        sessionDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
        
		if (marker != null) {
            marker.remove();
        }
        
        Cursor visitedCursor = sessionDB.rawQuery("SELECT * FROM sessionPole WHERE isVisited is NULL LIMIT 1", null);
      	visitedCursor.moveToFirst();
      									//if cursor contains returned data
    	if(visitedCursor != null && visitedCursor.getCount() > 0)	{
    		
    		String ID 	= visitedCursor.getString(1);		//fetching id of pole
    		currentMarker = ID;								//for knowing which pole is current/to be visited
	    	    	
	        Cursor poleCursor = poleDB.rawQuery("SELECT longitude, latitude FROM pole WHERE name LIKE "+ID, null);
	        poleCursor.moveToFirst();
									//if cursor contains returned data
	        if(poleCursor != null || poleCursor.getCount() > 0)	{

	        	String LAN 	= poleCursor.getString(1);
	    		double doubleLat = Double.parseDouble(LAN);		//converting string to double for LatLng constructor

		    	String LON 	= poleCursor.getString(0);
	    		double doubleLon = Double.parseDouble(LON);		//converting string to double for LatLng constructor
	    											
	    		
	    									//creating new marker
	    		marker = mMap.addMarker(new MarkerOptions().position(new LatLng(doubleLat, doubleLon))
	        			.title(ID)
	        			.snippet("Stolpe infoWindow"));
	        	mMarkerArray.add(marker);			//adding marker to a list array
	        	
	        	moveToCurrentLocation(new LatLng(doubleLat, doubleLon));
	        	
	        	Log.d("Current pole: ", ID);
	        	
	        	
	        							//listen for when a user clicks on a markers infowindow
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
    }
    
    /**
     * Display image over Google maps as ground overlay
     * Image resource is fetched as resource from drawable
     */
    public void groundOverlay()	{
    	
    	LatLng northeast = new LatLng(60.798367, 10.70415);
    	LatLng southwest = new LatLng(60.786517, 10.66005);
    	
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);


    	GroundOverlayOptions newarkMap = new GroundOverlayOptions()
    	        .image(BitmapDescriptorFactory.fromResource(R.drawable.kart))
    	        .positionFromBounds(bounds);
    	mMap.addGroundOverlay(newarkMap);	
    }
    
    /**
     * If current session have all poles visited end time is computed
     * and showed to user as popup, if current session is a challenge
     * time is posted as highscore
     */
    
    public void isFinished()	{
    	SharedPreferences spTime = getSharedPreferences("time", Context.MODE_PRIVATE);
    	String initialTime = spTime.getString("StartTime", "" );
    	String challengeId = spTime.getString("challengeName", "");
    	
    	SharedPreferences spName = getSharedPreferences("Login", Context.MODE_PRIVATE);
    	String userName = spName.getString("UserName", "");
    	

    	
    	Log.d("InitialTime", initialTime);
    	
    	long startTime = Long.parseLong(initialTime);
    	long currentTime = System.currentTimeMillis();
    	long currentTimeToSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTime); 	
    	long totalTime = currentTimeToSeconds - startTime;
   	 
    	String s = String.valueOf(totalTime);
        	
    	if(challengeId.equals(""))	{

    	}
    	else	{
    		Log.d("Found challenge name: ", challengeId);
    		
    		poleDB = openOrCreateDatabase("PoleDB", MODE_PRIVATE, null);		//database for poles
    		challengeDB = openOrCreateDatabase("spekIGjovik", MODE_PRIVATE, null);		//database for poles

		    Cursor cId = poleDB.rawQuery("SELECT id FROM challenges WHERE name ="+challengeId, null);
		    cId.moveToFirst();
		    
		    if(cId != null && cId.getCount() > 0)	{
		    	if(!userName.equals(""))	{
		    		challengeDB.execSQL("INSERT INTO highscores(userId,challengeId,score) VALUES("+userName+","+challengeId+","+totalTime+")");
		    		Log.d("Found username", "Maps: Line 285");
		    	}
		    }
    		
    		
			//sessionDB.execSQL("INSERT INTO sessionPole(poleId) VALUES ("+ID+");");



    	}
    	
    	

    	
    	Log.d("Total time used: ", s);
    	Toast.makeText(getApplicationContext(), "Hurray, you are done!!", Toast.LENGTH_SHORT).show();
    	showDialog();
    	clearSessionDB();
		

    	
    	
    	
    	
    }
    /**
     * Zooming screen to current visible marker.
     * @param currentLocation containts coordinates to current marker
     */
    private void moveToCurrentLocation(LatLng currentLocation)	{   
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
    
    /**
     * If current session have all poles visited, table holding poles
     * is cleared.
     */
    
    public void clearSessionDB()	{
        sessionDB = openOrCreateDatabase("PoleSession", MODE_PRIVATE,null);		//opening database for saving poles for current session
	    sessionDB.execSQL("DELETE FROM sessionPole");
	    sessionDB.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='sessionPole'");
	    
	    Log.d("Clearing session table", "Maps: line 274");

    }
    
    @SuppressWarnings("deprecation")
	public void showDialog()	{
        AlertDialog alertDialog1 = new AlertDialog.Builder(Maps.this).create();

        // Setting Dialog Title
        String window = getResources().getString(R.string.dialog_time);
        alertDialog1.setTitle(window);

        // Setting Dialog Message
        String message = getResources().getString(R.string.message_time);
        alertDialog1.setMessage(message);

        // Setting Icon to Dialog
        //alertDialog1.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                // closed
                Toast.makeText(getApplicationContext(),
                        "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        // Showing Alert Message
        alertDialog1.show();
    	
    }
    
	/**
	 * Setting starting time for tour, the saved time is later
	 * used for finding total used time for tour.
	 * Then used for highscore.
	 */
	public void setStartTime()	{
		
		long time = System.currentTimeMillis();	
		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
	    
		String startTime = Long.toString(timeSeconds);
		
	    SharedPreferences sharedTime = getSharedPreferences("time", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedTime.edit();
			
		editor.putString("StartTime", startTime);
		editor.commit();
	    	
	}

}