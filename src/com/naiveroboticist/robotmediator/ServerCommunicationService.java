package com.naiveroboticist.robotmediator;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerCommunicationService extends Service {

    private static final String TAG = ServerCommunicationService.class.getSimpleName();
    private Timer timer;
    
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Attempting to communicate with the server");
        timer = new Timer("ServerCommunicationTimer");
        timer.schedule(updateTask, 1000L, 60 * 1000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service shutting down connection to server");
        
        timer.cancel();
        timer = null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
