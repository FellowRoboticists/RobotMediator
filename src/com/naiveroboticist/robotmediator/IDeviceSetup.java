package com.naiveroboticist.robotmediator;

import android.hardware.usb.UsbDevice;

public interface IDeviceSetup {
    
    void setUpTheDevice(UsbDevice device);
    void deviceSetupError(String message);

}
