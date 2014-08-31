package com.naiveroboticist.robotmediator;

import java.io.IOException;
import java.util.HashMap;
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
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

public class IRobotCommunicationService extends Service {
    private static final String TAG = IRobotCommunicationService.class.getSimpleName();
    
    private static final String ACTION_USB_PERMISSION = "com.naiveroboticist.USB_PERMISSION";

    private PendingIntent mPendingIntent = null;
    private UsbSerialDriver mDriver = null;
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

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Register the broadcast receiver
        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        
        mDriver = availableDrivers.get(0);
        manager.requestPermission(mDriver.getDevice(), mPendingIntent);
        
        return super.onStartCommand(intent, flags, startId);
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
            // Write some data. Most have just one port (port 0)
            List<UsbSerialPort> ports = mDriver.getPorts();
            int count = ports.size();
            UsbSerialPort port = ports.get(0);
            byte buffer[] = new byte[1];
            byte buffer4[] = new byte[4];
            byte buffer5[] = new byte[5];
            byte buffer2[] = new byte[2];
            try {
                port.open(connection);
                port.setParameters(57600, 8, 1, UsbSerialPort.PARITY_NONE);
                buffer[0] = (byte) 0x80; // START
                port.write(buffer, 100);
                buffer[0] = (byte) 0x83; // SAFE
                port.write(buffer, 100);
                buffer5[0] = (byte) 0x8c; // Define a song
                buffer5[1] = (byte) 0x00;
                buffer5[2] = (byte) 0x01;
                buffer5[3] = (byte) 72;
                buffer5[4] = (byte) 10;
                port.write(buffer5, 100);
                buffer2[0] = (byte) 0x8d; // Play Song 0
                buffer2[1] = (byte) 0x00;
                port.write(buffer2, 100);
                buffer4[0] = (byte) 0x8b; // Show green LED
                buffer4[1] = (byte) 8;
                buffer4[2] = (byte) 0;
                buffer4[3] = (byte) 255;
                port.write(buffer4, 100);
                
            } catch (IOException ex) {
                Log.e(TAG, "Error communicating with port", ex);
            } finally {
                try {
                    port.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing port");
                }
            }
        } else {
            Log.w(TAG, "You probably need to call UsbManager.requestPermission(driver.getDevice(),... )");
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
