# Android Application

This repository contains the Android application implemented for Multi-Disciplinary Design Project AY2020/2021 Team 2.

## Overview

The Acer tablet with the Android application installed acts as a wireless remote controller for the team's MDP robotic system.
The application sends commands to other components of the system to start various tasks for assessment, initiate calibration, update waypoint position, control robot movement, and many more.
The current maze environment and robot status is visualized on the application interface.

The application is compiled on version Android 10 (API level 29) of Android SDK Platform.
It runs on a physical device with Bluetooth support.

## UI Design & Functionalities

**Navigation Menu**: The users may navigate different pages within application using the navigation menu:

![NavigationMenu](images/NavigationMenu.png)

**Maze Interface**: The maze interface displays the current maze environment:

![InitialMazeFragment](images/InitialMazeFragment.png)

The functionalities of the maze interface are listed as follows:

- The maze interface provides interactive control of the robot movement (move forward, turn left, turn right) in the 'Robot Movement Control' panel. User may tap on the buttons to control robot movement, or enable Tilt Sensing to control the robot by tilting the Android tablet.
- The maze interface indicates the current status of the robot (idle, running, calibrating, etc) in the 'Robot Status' text box.
- User may enter waypoint & robot start coordinates using Touch-the-Map based interaction. The user may select a grid in the map, and then tap the 'UPDATE' button for waypoint or start coordinates to update the values.
- The maze interface provides 2D display of the maze environment and the robot’s location.
- The maze interface provides the selection of Manual or Auto
updating of graphical display of the maze environment. Auto update mode can be turned on/off by toggling the ON/OFF button in the 'Update Mode' panel. When Auto update mode is turned off, tapping on the 'UPDATE' button triggers maze environment display update.
- The maze interface is able to display Number ID Blocks in the maze environment.
- User may start fastest path and exploration, and initiate calibration by tapping the buttons under the 'Start Robot' panel.

**Bluetooth Interface**: The Bluetooth interface is as below:

![BluetoothFragment](images/BluetoothFragment.png)

The functionalities of the Bluetooth interface are listed as follows:

- The application does not hang up if connectivity with the Bluetooth device is temporarily lost, and is able to re-establish connection automatically once the Bluetooth device initiates connection again.
- The floating action button at the bottom right corner indicates the current Bluetooth connection status. It's gray when there is no Bluetooth connection and cyan when a Bluetooth device is connected.
- User may enable the Android tablet to be discoverable via Bluetooth by other Bluetooth devices by tapping the 'ENABLE DISCOVERY VIA BLUETOOTH' button.
- The Bluetooth interface displays a list of devices previously paired with the Android tablet under 'My Devices'. User may refresh the list by tapping the 'REFRESH MY DEVICES' button.
- The Bluetooth interface scans nearby Bluetooth devices and displays the list of devices under 'Other Available Devices'.

**Communication Interface**: The communication interface is as below:

![CommunicationFragment](images/CommunicationFragment.png)

The functionalities of the communication interface are listed as follows:

- The communication interface transmits and receives text strings via Bluetooth. User may send text strings out and the received text strings will be displayed in the text box under 'Receive Data'.
- The communication interface supports sending persistent user reconfigurable string commands to the robot. Both text strings under 'Persistent Text Strings' are non-volatile and configurable by user.

## Communication Protocols

## Implementation

## References