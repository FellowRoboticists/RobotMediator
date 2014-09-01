package com.naiveroboticist.robotmediator;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServerCommunicationService extends Service {

    private static final String TAG = ServerCommunicationService.class.getSimpleName();
    
    private Timer timer;
    private Thread commThread = null;
    private boolean continueRunning = true;
    
    class CommunicationThread implements Runnable {
        
        private InetAddress address;
        private int port;
        
        public CommunicationThread(InetAddress serverAddr, int serverPort) {
            address = serverAddr;
            port = serverPort;
        }
        
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(address, port);
                InputStream input = socket.getInputStream();
                PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

                byte[] byteBuffer = new byte[500];
                int numBytes = input.read(byteBuffer);
                String serverChallenge = new String(byteBuffer, 0, numBytes);
                String message = Dsigner.verifyServerMessage(ServerCommunicationService.this, serverChallenge);
                
                if (message == null) {
                    Log.e(TAG, "Server challenge had invalid signature");
                    return;
                }
                
                // Tell the server who we are
                String robotMessage = "robot|" + MediatorSettings.robotName(ServerCommunicationService.this);
                String signedRobotMessage = Dsigner.signRobotMessage(ServerCommunicationService.this, robotMessage);
                output.print(signedRobotMessage);
                output.flush();
                
                // Now, wait until we stop the thread
                while (continueRunning) {
                    numBytes = input.read(byteBuffer);
                    String commandMessage = new String(byteBuffer, 0, numBytes);
                    if (commandMessage != null) {
                        String command = Dsigner.verifyServerMessage(ServerCommunicationService.this, commandMessage);
                        if (command != null) {
                            Intent intent = new Intent(IRobotCommunicationService.ACTION_COMMAND_TO_ROBOT);
                            intent.putExtra(IRobotCommunicationService.COMMAND_NAME, command);
                            sendBroadcast(intent);
                        } else {
                            Log.e(TAG, "Invalid command from the server: " + command);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error doing socket junk", e);
            } finally {
                try { 
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error closing socket", ex);
                }
            }
            Log.i(TAG, "Client socket thread done.");
        }
        
    }
    
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (commThread == null) {
                try {
                    commThread = new Thread(new CommunicationThread(MediatorSettings.telepHost(ServerCommunicationService.this),
                                                         (int)MediatorSettings.telepPort(ServerCommunicationService.this)));
                    commThread.start();
                } catch (UnknownHostException e) {
                    Log.e(TAG, "Error Firing up client socket thread", e);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer("ServerCommunicationTimer");
        timer.schedule(updateTask, 1000L, 60 * 1000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        continueRunning = false;
        
        timer.cancel();
        timer = null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Nothing to see here. Move along.
        return null;
    }

}
