package com.hig.prestigedevelopment.sprekigjovik;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * 
 * @author KhaiVanNgo
 *
 *Lets and checks whether user have input pole code (it is not checked if valid for our version)
 *
 */
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
	
	/**
	 * Listen for users pole code input, if submit pole is 
	 * checked as visited and returning to host activity.					
	 */
	
	public void addListenerOnButton() {
		
		poleSubmit = (Button) findViewById(R.id.submit);
	    myDB = this.openOrCreateDatabase("PoleSession", MODE_PRIVATE, null);
	    
	    
		poleSubmit.setOnClickListener(new OnClickListener() {
			Intent i = getIntent(); 
			int visited = 1;
			
			@Override
			  public void onClick(View v) {
			    		
			    String markerId = i.getStringExtra("markerId");		//getting data from intent

			    	myDB.execSQL("UPDATE sessionPole SET isVisited = "+visited+" WHERE poleID = "+markerId);

			    	DialogActivity.this.finish();
			   
			}
		 
			});
		}

	
}