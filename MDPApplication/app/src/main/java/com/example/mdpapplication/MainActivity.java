package com.example.mdpapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mdpapplication.service.BluetoothService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_TAG = "MainActivity";

    private static final String NO_BLUETOOTH_DEVICE_CONNECTED = "There is currently no Bluetooth device connected";
    private static final String DEVICE_IS_CONNECTED_TO = "Your device is connected to ";

    // Message strings
    private static final String TO_ARDUINO = "";
    private static final String TO_ALGORITHM = "";
    private static final String TO_RASPBERRY_PI = "";
    private static final String ROBOT_MOVE_FORWARD = "M0";
    private static final String ROBOT_TURN_LEFT = "L";
    private static final String ROBOT_TURN_RIGHT = "R";
    private static final String WAYPOINT = "WAYPOINT";
    private static final String START_POSITION = "START";
    private static final String MAZE_UPDATE = "UPDATE";
    private static final String START_FASTEST_PATH = "FP";
    private static final String START_EXPLORATION = "EXP";
    private static final String INITIATE_CALIBRATION = "C";
    private static final String ENABLE_ALIGNMENT = "a";
    private static final String DISABLE_ALIGNMENT = "b";
    private static final String ENABLE_EMERGENCY_BRAKE = "e";
    private static final String DISABLE_EMERGENCY_BRAKE = "f";
    private static final String RESET = "RESET";

    private AppBarConfiguration mAppBarConfiguration;

    private static FloatingActionButton bluetoothStatusFloatingActionButton;

    private static BluetoothService bluetoothService;

    private static String receivedTextStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothService = new BluetoothService();

        receivedTextStrings = "";

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothStatusFloatingActionButton = findViewById(R.id.bluetoothStatusFloatingActionButton);
        bluetoothStatusFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothService.isConnectedToBluetoothDevice()) {
                    Snackbar.make(view, DEVICE_IS_CONNECTED_TO + bluetoothService.getConnectedDeviceName(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, NO_BLUETOOTH_DEVICE_CONNECTED, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        updateBluetoothStatusFloatingActionButtonDisplay();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_maze, R.id.nav_bluetooth, R.id.nav_communication)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Public Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public static void updateBluetoothStatusFloatingActionButtonDisplay() {
        if (bluetoothService.isConnectedToBluetoothDevice()) {
            bluetoothStatusFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        } else {
            bluetoothStatusFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
    }

    public static void updateReceivedTextStrings(String newReceivedString) {
        receivedTextStrings = newReceivedString + "\n" + receivedTextStrings;
        Log.d(MAIN_ACTIVITY_TAG, "Updated received string text view: " + receivedTextStrings);
    }

    public static void resetReceivedTextStrings() {
        receivedTextStrings = "";
    }

    public static String getReceivedTextStrings() {
        return receivedTextStrings;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////             Send Out Messages            ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void sendRobotMoveForwardCommand() {
        String command = TO_ARDUINO + ROBOT_MOVE_FORWARD;
        Log.d(MAIN_ACTIVITY_TAG, "Sending robot move forward command: " + command);
        bluetoothService.sendOutMessage(command);
    }

    public static void sendRobotTurnLeftCommand() {
        String command = TO_ARDUINO + ROBOT_TURN_LEFT;
        Log.d(MAIN_ACTIVITY_TAG, "Sending robot turn left command: " + command);
        bluetoothService.sendOutMessage(command);
    }

    public static void sendRobotTurnRightCommand() {
        String command = TO_ARDUINO + ROBOT_TURN_RIGHT;
        Log.d(MAIN_ACTIVITY_TAG, "Sending robot turn right command: " + command);
        bluetoothService.sendOutMessage(command);
    }

    public static void sendWaypointPosition(int[] waypointCoordinates) {
        String waypointMessage = TO_ALGORITHM + WAYPOINT + "," + waypointCoordinates[0] + ":" + waypointCoordinates[1];
        Log.d(MAIN_ACTIVITY_TAG, "Sending waypoint message: " + waypointMessage);
        bluetoothService.sendOutMessage(waypointMessage);
    }

    public static void sendRobotStartPosition(int[] startCoordinates) {
        String startPositionMessage = TO_ALGORITHM + START_POSITION + "," + startCoordinates[0] + ":" + startCoordinates[1];
        Log.d(MAIN_ACTIVITY_TAG, "Sending robot start position message: " + startPositionMessage);
        bluetoothService.sendOutMessage(startPositionMessage);
    }

    public static void sendMazeUpdateRequest() {
        String mazeUpdateRequestMessage = TO_ALGORITHM + MAZE_UPDATE;
        Log.d(MAIN_ACTIVITY_TAG, "Sending maze update request message: " + mazeUpdateRequestMessage);
        bluetoothService.sendOutMessage(mazeUpdateRequestMessage);
    }

    public static void sendStartFastestPathCommand() {
        String startFastestPathCommand = TO_ALGORITHM + START_FASTEST_PATH;
        Log.d(MAIN_ACTIVITY_TAG, "Sending start fastest path command: " + startFastestPathCommand);
        bluetoothService.sendOutMessage(startFastestPathCommand);
    }

    public static void sendStartExplorationCommand() {
        String startExplorationCommand = TO_ALGORITHM + START_EXPLORATION;
        Log.d(MAIN_ACTIVITY_TAG, "Sending start exploration command: " + startExplorationCommand);
        bluetoothService.sendOutMessage(startExplorationCommand);
    }

    public static void sendInitiateCalibrationCommand() {
        String initiateCalibrationCommand = TO_ALGORITHM + INITIATE_CALIBRATION;
        Log.d(MAIN_ACTIVITY_TAG, "Sending initiate calibration command: " + initiateCalibrationCommand);
        bluetoothService.sendOutMessage(initiateCalibrationCommand);
    }

    public static void sendResetCommand() {
        String resetCommand = TO_ALGORITHM + RESET;
        Log.d(MAIN_ACTIVITY_TAG, "Sending reset command: " + resetCommand);
        bluetoothService.sendOutMessage(resetCommand);
    }

    public static void sendEnableAlignmentCheckAfterMoveCommand() {
        String enableAlignmentCommand = TO_ALGORITHM + ENABLE_ALIGNMENT;
        Log.d(MAIN_ACTIVITY_TAG, "Sending enable alignment check after move command: " + enableAlignmentCommand);
        bluetoothService.sendOutMessage(enableAlignmentCommand);
    }

    public static void sendDisableAlignmentCheckAfterMoveCommand() {
        String disableAlignmentCommand = TO_ALGORITHM + DISABLE_ALIGNMENT;
        Log.d(MAIN_ACTIVITY_TAG, "Sending disable alignment check after move command: " + disableAlignmentCommand);
        bluetoothService.sendOutMessage(disableAlignmentCommand);
    }

    public static void sendEnableEmergencyBrakeCommand() {
        String enableEmergencyBrakeCommand = TO_ALGORITHM + ENABLE_EMERGENCY_BRAKE;
        Log.d(MAIN_ACTIVITY_TAG, "Sending enable emergency brake command: " + enableEmergencyBrakeCommand);
        bluetoothService.sendOutMessage(enableEmergencyBrakeCommand);
    }

    public static void sendDisableEmergencyBrakeCommand() {
        String disableEmergencyBrakeCommand = TO_ALGORITHM + DISABLE_EMERGENCY_BRAKE;
        Log.d(MAIN_ACTIVITY_TAG, "Sending disable emergency brake command: " + disableEmergencyBrakeCommand);
        bluetoothService.sendOutMessage(disableEmergencyBrakeCommand);
    }

    public static void sendCommunicationMessage(String message) {
        String communicationMessage = TO_RASPBERRY_PI + message;
        Log.d(MAIN_ACTIVITY_TAG, "Sending communication message: " + communicationMessage);
        bluetoothService.sendOutMessage(communicationMessage);
    }
}
