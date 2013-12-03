package com.hig.prestigedevelopment.sprekigjovik;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Profile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String textValue = sharedPreferences.getString("UserName", "");
//		EditText text = (EditText)findViewById(R.id.edit_message);
//		text.setText(textValue);
		
		Toast.makeText(getApplicationContext(), textValue, 
				   Toast.LENGTH_LONG).show();
		
		
		if(!checkLogin()){
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    return true;
	}
	
	@Override
    public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
        super.onBackPressed();   
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	  intent = new Intent(this, MainActivity.class);
		    	  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	  startActivity(intent);
		    	  break; 
	    default:
	      break;
	    }

	    return true;
	  } 
	
	
	public Boolean checkLogin(){
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		String userName = sharedPreferences.getString("UserName", "");
		
		return (userName != "") ? true : false;
	}
	
	public void logOut(View view) {
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
