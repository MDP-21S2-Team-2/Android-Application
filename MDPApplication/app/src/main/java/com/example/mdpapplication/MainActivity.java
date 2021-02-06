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
    private static final String TO_ARDUINO = "AN,AR;";
    private static final String TO_ALGORITHM = "AN,AL;";
    private static final String ROBOT_MOVE_FORWARD = "FORWARD";
    private static final String ROBOT_TURN_LEFT = "LEFT";
    private static final String ROBOT_TURN_RIGHT = "RIGHT";
    private static final String WAYPOINT = "WAYPOINT";
    private static final String START_POSITION = "START";
    private static final String MAZE_UPDATE = "UPDATE";
    private static final String START_FASTEST_PATH = "FP";
    private static final String START_EXPLORATION = "EXP";

    private AppBarConfiguration mAppBarConfiguration;

    private static FloatingActionButton bluetoothStatusFloatingActionButton;

    private static BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothService = new BluetoothService();

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
}