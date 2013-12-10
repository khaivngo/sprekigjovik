package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

public class DynamicTour extends Activity {

	private Spinner polesSpinner;
	private Button btnSubmit;
	private SQLiteDatabase poleDB = null ;
	private SQLiteDatabase sessionDB = null ;

	private static Context context;
	private Cursor poleCursor;
	private Cursor sessionCursor;
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_tour);
		
		context = this;
		
		//addItemsOnSpinner2();
		addListenerOnButton();
		addListenerOnSpinnerItemSelection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dynamic_tour, menu);
		return true;
	}

	
	  public void addListenerOnSpinnerItemSelection() {
			polesSpinner = (Spinner) findViewById(R.id.poles_spinner);
	  }
	  
	  /**
	   * Listeing
	   */
		public void addListenerOnButton() {
			
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			 
			btnSubmit.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {		//displays spinner
												//path to DB
				String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
												//opening DB via SQLITE's own method
				poleDB = SQLiteDatabase.openDatabase(path + "PoleDB", null,SQLiteDatabase.CREATE_IF_NECESSARY);
				sessionDB = SQLiteDatabase.openDatabase(path + "PoleSession", null,SQLiteDatabase.CREATE_IF_NECESSARY);  
				  		
			    String selection = String.valueOf(polesSpinner.getSelectedItem());
				
			    poleCursor = poleDB.rawQuery("SELECT name, longitude, latitude FROM pole ORDER BY random() LIMIT "+selection, null);
			    sessionCursor = sessionDB.rawQuery("SELECT * FROM sessionPole", null);

				
			    ArrayList<String> selectedPoles = new ArrayList<String>();
			    
			    						//Query checking if table contains data
		    	sessionCursor.moveToFirst();
		    							//if table is empty
			    if(sessionCursor == null || sessionCursor.getCount()==0) {
			    	Log.d("Setting start time", "File: DynamicTour line 80");
			    }
			    

			    while(poleCursor.moveToNext())	{
//			    	Log.d("Pole ID: ",cursor.getString(0));
//			    	Log.d("LONGITUDE: ",cursor.getString(1));
//			    	Log.d("LATITUDE",cursor.getString(2));

			    	selectedPoles.add(poleCursor.getString(0)+":"+poleCursor.getString(1)+":"+poleCursor.getString(2));
			    }
			    					//sending a list fetched poles
			    Intent intent = new Intent(DynamicTour.getContext(), Maps.class);
			    intent.putStringArrayListExtra("selected", (ArrayList<String>) selectedPoles);
			    startActivity(intent);
			  }
		 
			});
			
		}
									//a way to get a static context
		public static Context getContext() {
			  return context;
		}
		
}
