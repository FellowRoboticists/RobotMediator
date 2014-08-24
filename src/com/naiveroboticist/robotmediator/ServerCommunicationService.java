package com.naiveroboticist.robotmediator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerCommunicationService extends Service {

    private static final String TAG = ServerCommunicationService.class.getSimpleName();
    
    private Timer timer;
    private boolean connected = false;
    private Socket socket = null;
    
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
            try {
                InetAddress serverAddr = MediatorSettings.telepHost(ServerCommunicationService.this);
                socket = new Socket(serverAddr, (int) MediatorSettings.telepPort(ServerCommunicationService.this));
                connected = true;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = null;
                String serverChallenge = reader.readLine();
                Log.i(TAG, "Message from Server: " + serverChallenge);
                // Verify that the message came from daneel...
                String message = Dsigner.verifyServerMessage(ServerCommunicationService.this, serverChallenge);
                if (message == null) {
                    Log.i(TAG, "This was not a valid message");
                } else {
                    Log.i(TAG, "This was a valid message");
                    String robotMessage = "robot|" + MediatorSettings.robotName(ServerCommunicationService.this);
                    String signedRobotMessage = Dsigner.signRobotMessage(ServerCommunicationService.this, robotMessage);
                    Log.i(TAG, "The signed robot message: " + signedRobotMessage);
                    pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                    pw.println(signedRobotMessage);
                }
                String nextMessage = reader.readLine();
                Log.i(TAG, "Response from server: " + nextMessage);
                socket.close();
                connected = false;
            } catch (Exception ex) {
                Log.e(TAG, "Error connecting to server", ex);
                connected = false;
            }
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
