package com.naiveroboticist.robotmediator;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

/**
 * An Android Activity that supports the single page of the
 * IRobotMediator application.
 * 
 * @author dsieh
 *
 */
public class MediationActivity extends ActionBarActivity {
	@SuppressWarnings("unused")
    private static final String TAG = MediationActivity.class.getSimpleName();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediation);
		
	}

	/**
	 * Called when the toggle button on the view is tapped. Start/stop the
	 * services.
	 * 
	 * @param view
	 */
	public void onToggleActivation(View view) {
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
			startService(new Intent(ServerCommunicationService.class.getName()));
	        startService(new Intent(IRobotCommunicationService.class.getName()));
	    } else {
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
