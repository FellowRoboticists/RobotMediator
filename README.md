RobotMediator
=============

RobotMediator is an Android application that allows an iRobot Create
to be controlled over the internet from an Android device that has
an internet connection and a USB connector.

The application activity couldn't be simpler; there is a single 
toggle-button on the screen to connection/disconnect the application
from the server and robot.

When turned on, the application starts two background services:

1. A service that maintains a connection to the telep robot control
server. The server information is specified via the application settings.
You will need to specify the host, port, the name of the robot, the 
private key (PEM) for the robot and the public key for the server. The
name of the robot must match the server's name for the robot because it
matches the public key by name. The server routes commands from the control
front-end (typically a web page) to the robot.

2. A service that establishes and maintains a connection to the iRobot Create
via the USB/Serial cable. On first connection, you may have to allow permission
for the application to access the USB device. This service receives the 
robot commands from the internet (via broadcast from the service above) and 
routes them to the robot. It also has a thread running that reads sensor information
from the robot. In particular, it looks for a 'bump' action and allows the robot
to behave properly when it bumps into something.

Being a novice at Android application development, I used the adt-bundle for Eclipse
so you will notice that this application managed through it.

The USB/Serial driver code (see package com.hoho.android.usbserial) is an extract from
the GitHub Project https://github.com/mik3y/usb-serial-for-android. This project was
implemented using gradle and I had no interest in understanding how to build the 
project using gradle. However, I want to make sure I credit mik3y for that code. That
is the reason my project uses the GNU LESSER GENERAL PUBLIC LICENSE. In the long run
I plan to leverage the original project that mik3y put together; sorry, I was in a 
hurry to get this thing working.

Although the code works as-is, this is a work in progress. There is no telling at this
point where the project will go.

Come along for the ride if you want.

Copyright
=========

Copyright (c) 2014 Dave Sieh

See LICENSE.txt for details.
