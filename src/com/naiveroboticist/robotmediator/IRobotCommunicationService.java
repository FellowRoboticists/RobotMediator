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
    
    
    private static final int STARTING_SPEED = 100; // mm/s
    private static final int SPEED_INCREMENT = 10; // mm/s

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
                            Log.d(TAG, "We should now have permission to write to the device");
                            setUpTheDevice(device);
                        }
                    } else {
                        Log.d(TAG, "Permission denied for device" + device);
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
        
        switch (command) {
        case "forward":
            Log.i(TAG, "Robot should go forward");
            commander.drive(mSpeed, 0);
            mLastMoveCommand = command;
            break;
        case "backward":
            Log.i(TAG, "Robot should go backward");
            commander.drive(-mSpeed, 0);
            break;
        case "rotate_cw":
            Log.i(TAG, "Robot should rotate clockwise");
            commander.rotate(-mSpeed);
            break;
        case "rotate_ccw":
            Log.i(TAG, "Robot should rotate counter-clockwise");
            commander.rotate(mSpeed);
            break;
        case "speed_up":
            Log.i(TAG, "Robot should speed up");
            mSpeed += SPEED_INCREMENT;
            if (mLastMoveCommand != null) {
                robotCommand(mLastMoveCommand);
            }
            break;
        case "slow_down":
            Log.i(TAG, "Robot should slow down");
            mSpeed -= SPEED_INCREMENT;
            if (mSpeed < 0) {
                mSpeed = SPEED_INCREMENT;
            }
            if (mLastMoveCommand != null) {
                robotCommand(mLastMoveCommand);
            }
            break;
        case "stop":
            Log.i(TAG, "Robot should stop");
            commander.stop();
            mSpeed = STARTING_SPEED;
            mLastMoveCommand = null;
            break;
        default:
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
            } finally {
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
        // TODO Auto-generated method stub
        return null;
    }
    
    // For iRobotListener interface

    @Override
    public void hitBump(String name) {
        Log.i(TAG, "Hit a bump on bumper: " + name);
        new Thread(new BackupFromBump()).start();
    }

    @Override
    public void bumpEnd(String name) {
        Log.i(TAG, "Done with bump on bumper: " + name);
    }

    @Override
    public void wheelDrop() {
        Log.i(TAG, "Holy crap. The Wheels dropped out from under me!!");
    }

}
