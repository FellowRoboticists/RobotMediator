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
    
    private static final String SERVER_MESSAGE = "And You Are?|tqxoQAYYDGtCqOMiVo/xyX10q1n+jwV8f5VdqDvOcIj8aDr2sJEtM4nk6NX4GgfBcl8AUA+xAXtBNwQT3+7pPUgX3QBMUBJmaGQkXNPgg6xvRssrboEYEAG01zktAp4af21iSS1M1r6ya3ChdSCZHXbaNH/dfuBwAQYBNv0BuLeJ3CtyPhvYxIt3j6jzVGUTIgvvIies5kSEuJ0m5OxhTslzR/Cjy7Jisy/++cqWkDkwGaCKc96Sq56GA6c7gORKBPPstsiembWxVO0LLnnJF191qjV4yJ/GcyWZywTT7VLlOmxAL8f+LhTTCmKmmrpGeHrvkmJQBouMQFdiLENpuw==";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediation);
		
	}
	
	public void onCreateSignature(View view) {
	    String robotName = MediatorSettings.robotName(this);
	    
	    String registrationMessage = "robot|" + robotName;
	    
	    try {
            String signedMessage = Dsigner.signRobotMessage(this, registrationMessage);
            Log.i(TAG, "The signed message is: " + signedMessage);
        } catch (Exception e) {
            Log.e(TAG, "Error creating robot message", e);
        }
	}
	
	public void onVerifySignature(View view) {
	    String serverMessage;
        try {
            serverMessage = Dsigner.verifyServerMessage(this, SERVER_MESSAGE);
            Log.i(TAG, "The server message was: " + serverMessage);
        } catch (Exception e) {
            Log.e(TAG, "Error verifying server message", e);
        }
	}
	
	public void onClickStartUSB(View view) {
	    // Try to start the USB stuff
	    startService(new Intent(IRobotCommunicationService.class.getName()));
	}
		
	public void onToggleActivation(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
			Log.i(TAG, "Activation button clicked and is now ON");
			startService(new Intent(ServerCommunicationService.class.getName()));
	    } else {
			Log.i(TAG, "Activation button clicked and is now OFF");
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
