package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author KhaiVanNgo
 * 
 * Prompts user with menu for choosing number of poles
 * and the desired level of them.
 *
 */

public class DynamicTour extends Activity implements LocationListener {

	private Spinner polesSpinner;
	private Spinner levelSpinner;

	private Button btnSubmit;
	private SQLiteDatabase poleDB = null ;
	private SQLiteDatabase sessionDB = null ;
	private SQLiteDatabase db = null ;

	private static Context context;
	private Location location;
	private LocationManager locationManager; 
	private float currentLat = 0;
	private float currentLon = 0;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_tour);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                1, this);
    	locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
		
    	provider = locationManager.getBestProvider(new Criteria(), true);
    	location = locationManager.getLastKnownLocation(provider);
    	
    	if(location != null) {
    		currentLat = (float) location.getLatitude();
    		currentLon = (float) location.getLongitude();
    		Log.d("heihei", "heihei");
    	}
    	Log.d("heihei2", "heihei2");
    	
		context = this;
		
		//addItemsOnSpinner2();
		addListenerOnButton();
		addListenerOnPoleSpinnerItemSelection();
	}

	@Override 
	public void onPause()
	{
	    super.onPause();
	    locationManager.removeUpdates(this);
	}
	
	@Override 
	public void onResume()
	{
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 2000, 10, this);
	}
	
	public void addListenerOnPoleSpinnerItemSelection() {
		 polesSpinner = (Spinner) findViewById(R.id.poles_spinner);			//spinner for number of poles
		 levelSpinner = (Spinner) findViewById(R.id.level_spinner);			//spinner for level on poles
	}

	  /**
	   * Listening for user choosing difficulty level and number of poles
	   */
		public void addListenerOnButton() {
			
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			 
			btnSubmit.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {		//displays spinner
												//path to DB
				String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
				
				final int MAXDISTANCE = 10000;
				final int MINDISTANCE = 50;
				Float f1 = null;
				Float f2 = null;
				List<String> selectedPoles = new ArrayList<String>();
		    	List<String> selectedIds = new ArrayList<String>();
		    	
		    	 String poles = String.valueOf(polesSpinner.getSelectedItem());
		    	 
		    	 int numberOfSelectedPoles = Integer.parseInt(poles);
		    	 String level	= String.valueOf(levelSpinner.getSelectedItem());
		    	 
		    	 String[] parts = level.split("\\:");
		    	 int selectedLevel = Integer.parseInt(parts[0]);

		    	
		    	//------------------------ GETS FIRST POLE BASED ON YOUR LOCATION -----------------------------------------//
		    	db = openOrCreateDatabase("PoleDB", MODE_PRIVATE,null);
		    	Cursor cursor = db.rawQuery("SELECT name, longitude, latitude FROM pole ORDER BY random() LIMIT 1;", null);
		    	
		    	while(cursor.moveToNext()){
		    		
		    		int distance = Math.round(distance(currentLat, currentLon, cursor.getFloat(2), cursor.getFloat(1)));
		    		
		    		if(distance < MAXDISTANCE && distance > MINDISTANCE){
		    			Log.d("Distance", distance(currentLat, currentLon, cursor.getFloat(2), cursor.getFloat(1)) + "");
		    			selectedPoles.add(cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
		    			selectedIds.add(cursor.getString(0));
		    			f1 = cursor.getFloat(2);
		    	    	f2 = cursor.getFloat(1);
		    			break;
		    		}
		    	}
		    	
		    	if(cursor == null || cursor.getCount() == 0 || location == null){
		    		 Toast.makeText(DynamicTour.this,
		    				    "Could not find any poles on this location! Please try again to refresh coordinates.",
		    				     Toast.LENGTH_SHORT).show();
		    	} else {
		    		//------------------------ GETS THE REST OF POLES BASED ON LAST POLE LOCATION ------------------------------//
			    	for(int i = 0; i < numberOfSelectedPoles-1; i++){
				    													//selects poles from highest chosen diffiulty and lower
			    														// and den orders from highest to lowest
			    		cursor = db.rawQuery("SELECT name, longitude, latitude FROM pole WHERE level <= "+selectedLevel+" ORDER BY level;", null);
				   
				    	while(cursor.moveToNext()){ 		
				    		int distance = Math.round(distance(f1, f2, cursor.getFloat(2), cursor.getFloat(1)));
				    		
				    		if(distance < MAXDISTANCE && distance > MINDISTANCE && !selectedIds.contains(cursor.getString(0))){
				    			Log.d("Distance", distance(f1, f2, cursor.getFloat(2), cursor.getFloat(1)) + "");
				    			selectedPoles.add(cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
				    			selectedIds.add(cursor.getString(0));
				    			
				    			f1 = cursor.getFloat(2);
				    			f2 = cursor.getFloat(1);
				    			break;
				    		}
				    	}		
			    	}
			
				    cursor.close();
				    
				    Intent intent = new Intent(DynamicTour.getContext(), Maps.class);
				    intent.putStringArrayListExtra("selected", (ArrayList<String>) selectedPoles);
				    startActivity(intent);
			    }
			  }
			});	
		}
										//a way to get a static context
		public static Context getContext() {
			  return context;
		}
		
		// Source http://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
		public Float distance (float lat_a, float lng_a, float lat_b, float lng_b ) {
		    double earthRadius = 3958.75;
		    double latDiff = Math.toRadians(lat_b-lat_a);
		    double lngDiff = Math.toRadians(lng_b-lng_a);
		    double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
		    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
		    Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double distance = earthRadius * c;

		    int meterConversion = 1609;

		    return new Float(distance * meterConversion).floatValue();
		}


		@Override
		public void onLocationChanged(Location mLocation) {
			
    		currentLat = (float) mLocation.getLatitude();
    		currentLon = (float) mLocation.getLongitude();
			
		}


		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
			

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
}