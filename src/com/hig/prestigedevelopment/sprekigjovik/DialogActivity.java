package com.hig.prestigedevelopment.sprekigjovik;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity {
	
	private Button poleSubmit;
	TextView mEdit;
	private SQLiteDatabase myDB = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
	
		addListenerOnButton();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dynamic_tour, menu);
		return true;
	}
	
	/**
	 * Listen for users pole code input, if submit pole is 
	 * checked as visited and returning to host activity.					
	 */
	
	public void addListenerOnButton() {
		
		poleSubmit = (Button) findViewById(R.id.submit);
	    mEdit   = (TextView)findViewById(R.id.pole_code);
	    myDB = this.openOrCreateDatabase("PoleSession", MODE_PRIVATE, null);
	    
	    
		poleSubmit.setOnClickListener(new OnClickListener() {
			Intent i = getIntent(); 
			int visited = 1;
			Cursor cursor = null;
			
			@Override
			  public void onClick(View v) {
			    Toast.makeText(DialogActivity.this, "\nTextView: "+ mEdit.getText().toString(),
			    Toast.LENGTH_SHORT).show();
			    		
			    String markerId = i.getStringExtra("markerId");		//getting data from intent
			    													//query updating visited pole
			    myDB.execSQL("UPDATE sessionPole SET isVisited = "+visited+" WHERE poleID = "+markerId);
			    					
//--------------------TESTING------------------------------------------------------------------------------
		 		cursor = myDB.rawQuery("SELECT * FROM sessionPole", null);	
			    while(cursor.moveToNext())	{
			    	Log.d("ID: 			",cursor.getString(0));
			    	Log.d("PoleID: 		",cursor.getString(1));
			    	if(cursor.getString(2) != null)
			    	Log.d("isVisited: 	",cursor.getString(2));
			    }			    
			    
			    Toast.makeText(DialogActivity.this,
				"OnClickListener : " + 
		                "\nMarkerId: "+ markerId,
					Toast.LENGTH_SHORT).show();
//--------------------TESTING-----------------------------------------------------------------------------
			  }
		 
			});
//		
//      	 Intent openHostActivity= new Intent(DialogActivity.this, Maps.class);
//         openHostActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//         startActivity(openHostActivity);
	
		}

	
	
	
	
	
	
	
	
	
}