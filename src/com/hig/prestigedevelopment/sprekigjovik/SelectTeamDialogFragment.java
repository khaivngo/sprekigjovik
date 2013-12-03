/**
 * 
 */
package com.hig.prestigedevelopment.sprekigjovik;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * @author Chris
 *
 */
public class SelectTeamDialogFragment extends DialogFragment{
	
	private SQLiteDatabase db;
	private String[] teams = getTeams();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_team);
        builder.setItems(teams, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                   // The 'which' argument contains the index position
                   // of the selected item
                	   	Intent intent = new Intent(MainActivity.getContext(), DisplayTeam.class);
                	   	intent.putExtra("team", teams[which]); 
           	    		startActivity(intent);
               }
        });
        return builder.create();
    }
    
    public String[] getTeams() {
    	
    	String path = "/data/data/com.hig.prestigedevelopment.sprekigjovik/databases/";
    	
    	db = SQLiteDatabase.openDatabase(path + "sprekIGjovik", null, SQLiteDatabase.CREATE_IF_NECESSARY);
		db.execSQL("CREATE TABLE IF NOT EXISTS teams(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " +
				"description TEXT);");
		Cursor cursor = db.rawQuery("SELECT name FROM teams", null);
    	
		List<String> strings = new ArrayList<String>();
		while(cursor.moveToNext()){
			strings.add(cursor.getString(0));
		}
    	String[] array = strings.toArray(new String[0]);
    	return array;
    }
}