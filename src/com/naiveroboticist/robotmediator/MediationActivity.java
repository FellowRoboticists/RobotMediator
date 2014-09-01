package com.naiveroboticist.robotmediator;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class MediationActivity extends ActionBarActivity {
	private static final String TAG = MediationActivity.class.getSimpleName();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediation);
		
	}
	
	public void onToggleActivation(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
			Log.i(TAG, "Activation button clicked and is now ON");
			startService(new Intent(ServerCommunicationService.class.getName()));
	        startService(new Intent(IRobotCommunicationService.class.getName()));
	    } else {
			Log.i(TAG, "Activation button clicked and is now OFF");
            stopService(new Intent(IRobotCommunicationService.class.getName()));
			stopService(new Intent(ServerCommunicationService.class.getName()));
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mediation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
		    Intent i = new Intent(this, SettingsActivity.class);
		    startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
