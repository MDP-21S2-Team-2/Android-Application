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

- The maze interface provides interactive control of the robot movement (move forward, turn left, turn right) in the 'Robot Movement Control' panel.
- The maze interface indicates the current status of the robot (idle, running, calibrating, etc) in the 'Robot Status' text box.
- User may enter waypoint & robot start coordinates using Touch-the-Map based interaction. The user may select a grid in the map, and then tap the 'UPDATE' button for waypoint or start coordinates to update the values.
- The maze interface provides 2D display of the maze environment and the robot’s location.
- The maze interface provides the selection of Manual or Auto
updating of graphical display of the maze environment. Auto update mode can be turned on/off by toggling the ON/OFF button in the 'Update Mode' panel. When Auto update mode is turned off, tapping on the 'UPDATE' button triggers maze environment display update.
- The maze interface is able to display Number ID Blocks in the maze environment.
- User may start fastest path and exploration, and initiate calibration by tapping the buttons under the 'Start Robot' panel.

## Communication Protocols

## Implementation

## References