package com.naiveroboticist.robotmediator;

import java.io.IOException;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import com.hoho.android.usbserial.driver.UsbSerialProber;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

public class IRobotCommunicationService extends Service implements IRobotListener {
    private static final String TAG = IRobotCommunicationService.class.getSimpleName();
    
    private static final String ACTION_USB_PERMISSION = "com.naiveroboticist.USB_PERMISSION";
    public static final String ACTION_COMMAND_TO_ROBOT = "com.naiveroboticist.COMMAND_TO_ROBOT";
    public static final String COMMAND_NAME = "com.naiveroboticist.COMMAND_NAME";
    
    
    private static int STARTING_SPEED = 100; // mm/s
    private static int SPEED_INCREMENT = 10; // mm/s
    private static int ROTATION_SPEED = 100; // mm/s

    private PendingIntent mPendingIntent = null;
    private UsbSerialDriver mDriver = null;
    private UsbSerialPort mPort = null;
    private RobotCommander commander = null;
    private int mSpeed = STARTING_SPEED;
    private String mLastMoveCommand = null;
    
    class UsbReaderThread implements Runnable {

        @Override
        public void run() {
            while (commander != null) {
                try {
                    Thread.sleep(100); // Sleep for a 1/10's of a second
                    if (commander != null && mPort != null) {
                        commander.read();
                    } else {
                        break; // We're done
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "UsbReaderThread interrupped", e);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading data from USB", e);
                    break;
                }
            }
            
        }
        
    }
    
    class BackupFromBump implements Runnable {

        @Override
        public void run() {
            try {
                robotCommand("stop");
                robotCommand("backward");
                Thread.sleep(1000);
            } catch (IOException e) {
                Log.e(TAG, "Error stopping and going backward on bump", e);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error waiting for a second after bump", e);
            } finally {
                try {
                    robotCommand("stop");
                } catch (IOException e) {
                    Log.e(TAG, "Unable to perform the final stop", e);
                }
            }
        }
        
    }
    
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // Call method to set up device communication
                            setUpTheDevice(device);
                        }
                    } else {
                        Log.i(TAG, "Permission denied for device" + device);
                    }
                }
            } 
        }
        
    };
    
    // Handles receipt of commands from the Server service.
    private final BroadcastReceiver mCommandReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_COMMAND_TO_ROBOT.equals(action)) {
                String command = intent.getStringExtra(COMMAND_NAME);
                try {
                    robotCommand(command);
                } catch (IOException e) {
                    Log.e(TAG, "Error issuing robot command: " + command, e);
                }
            }
        }
        
    };

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Register the broadcast receiver
        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter usbFilter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, usbFilter);
        
        IntentFilter cmdFilter = new IntentFilter(ACTION_COMMAND_TO_ROBOT);
        registerReceiver(mCommandReceiver, cmdFilter);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // When we start the process, make sure we set the 
        // speed from the preferences
        STARTING_SPEED = MediatorSettings.defaultSpeed(this);
        SPEED_INCREMENT = MediatorSettings.speedIncrement(this);
        ROTATION_SPEED = MediatorSettings.rotationSpeed(this);
        
        mSpeed = STARTING_SPEED;
        
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        
        mDriver = availableDrivers.get(0);
        manager.requestPermission(mDriver.getDevice(), mPendingIntent);
        
        return super.onStartCommand(intent, flags, startId);
    }
    
    public void robotCommand(String command) throws IOException {
        if (commander == null) {
            return;
        }
        if (command.equals("forward")) {
            commander.drive(mSpeed, 0);
            mLastMoveCommand = command;
        } else if (command.equals("backward")) {
            commander.drive(-mSpeed, 0);
        } else if (command.equals("rotate_cw")) {
            commander.rotate(-ROTATION_SPEED);
        } else if (command.equals("rotate_ccw")) {
            commander.rotate(ROTATION_SPEED);
        } else if (command.equals("speed_up")) {
            mSpeed += SPEED_INCREMENT;
            if (mLastMoveCommand != null) {
                robotCommand(mLastMoveCommand);
            }
        } else if (command.equals("slow_down")) {
            mSpeed -= SPEED_INCREMENT;
            if (mSpeed < 0) {
                mSpeed = SPEED_INCREMENT;
            }
            if (mLastMoveCommand != null) {
                robotCommand(mLastMoveCommand);
            }
        } else if (command.equals("stop")) {
            commander.stop();
            mSpeed = STARTING_SPEED;
            mLastMoveCommand = null;
        } else if (command.equals("noop")) {
            // This is the heartbeat from the server after a certain
            // amount of inactivity. Just ignore it.
        } else {
            Log.w(TAG, "Unknown command for robot: " + command);
        }
    }

    /**
     * This will be called by the broadcast receiver when permission is granted to 
     * open the USB device. The call sequence is something like:
     *   - onCreate()
     *   - onStartCommand()
     *   - setUpTheDevice()
     */
    public void setUpTheDevice(UsbDevice device) {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        
        UsbDeviceConnection connection = manager.openDevice(mDriver.getDevice());
        if (connection != null) {
            List<UsbSerialPort> ports = mDriver.getPorts();
            mPort = ports.get(0);
            try {
                mPort.open(connection);
                mPort.setParameters(57600, 8, 1, UsbSerialPort.PARITY_NONE);
                commander = new RobotCommander(mPort, this);
                
                // Do the initialization thing with the robot to get
                // us going....
                commander.iRobotInitialize();
                
                // Start listening for stuff from the robot
                new Thread(new UsbReaderThread()).start();
                
            } catch (IOException ex) {
                Log.e(TAG, "Error communicating with port", ex);
            } 
        } else {
            Log.w(TAG, "You probably need to call UsbManager.requestPermission(driver.getDevice(),... )");
        }
    }

    @Override
    public void onDestroy() {
        if (commander != null) {
            commander = null;
        }
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing port");
            }            
        }
        unregisterReceiver(mUsbReceiver);
        unregisterReceiver(mCommandReceiver);
        
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Nothing to see here. Move along.
        return null;
    }
    
    // For iRobotListener interface

    @Override
    public void hitBump(String name) {
        new Thread(new BackupFromBump()).start();
    }

    @Override
    public void bumpEnd(String name) {
        // If you want to do something when the bump sensor
        // has been released, put it here.
    }

    @Override
    public void wheelDrop() {
        // If you want to do something when the wheels drop,
        // put it here.
    }

}
