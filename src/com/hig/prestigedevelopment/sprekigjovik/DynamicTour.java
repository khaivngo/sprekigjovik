package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class DynamicTour extends Activity {

	private Spinner polesSpinner;
	private Button btnSubmit;
	private SQLiteDatabase myDB = null ;
	private static Context context;
		 

	
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
	  
	  
		public void addListenerOnButton() {
			
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			 
			btnSubmit.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
			    Toast.makeText(DynamicTour.this,
				"OnClickListener : " + 
		                "\nSpinner: "+ String.valueOf(polesSpinner.getSelectedItem()),
					Toast.LENGTH_SHORT).show();
			    
			    String selection = String.valueOf(polesSpinner.getSelectedItem());
			    
			    ArrayList<String> selectedPoles = new ArrayList<String>();
				
					//path to DB
			    String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
			    		//opening DB via SQLITE's own method
			    myDB = SQLiteDatabase.openDatabase(path + "PoleDB", null,SQLiteDatabase.CREATE_IF_NECESSARY);

			    Cursor cursor = myDB.rawQuery("SELECT name, longitude, latitude FROM pole ORDER BY random() LIMIT "+selection, null);

			    while(cursor.moveToNext())	{
//			    	Log.d("Pole ID: ",cursor.getString(0));
//			    	Log.d("LONGITUDE: ",cursor.getString(1));
//			    	Log.d("LATITUDE",cursor.getString(2));

			    	selectedPoles.add(cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
			    }

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
		
//		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//		
//			String selection = parent.getItemAtPosition(pos).toString();		//getting spinner selection
//			ArrayList<String> selectedPoles = new ArrayList<String>();
//			
//																	//path to DB
//			String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
//															//opening DB via SQLITE's own method
//			myDB = SQLiteDatabase.openDatabase(path + "PoleDB", null,SQLiteDatabase.CREATE_IF_NECESSARY);
//		
//			
//			Cursor cursor = myDB.rawQuery("SELECT name, longitude, latitude FROM pole ORDER BY random() LIMIT "+selection, null);
//
//
//			while(cursor.moveToNext())	{
//				Log.d("Pole ID: ",cursor.getString(0));
//				Log.d("LONGITUDE: ",cursor.getString(1));
//				Log.d("LATITUDE",cursor.getString(2));
//				
//				selectedPoles.add(cursor.getString(0)+"?"+cursor.getString(1)+"?"+cursor.getString(2));
//			}
//			
//			
//			Intent intent = new Intent(this, Maps.class);
//			intent.putStringArrayListExtra("selected", (ArrayList<String>) selectedPoles);
//			startActivity(intent);
//	  
//			}

}
