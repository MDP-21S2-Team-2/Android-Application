package com.example.mdpapplication.ui.maze;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mdpapplication.MainActivity;
import com.example.mdpapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.SENSOR_SERVICE;

public class MazeFragment extends Fragment implements SensorEventListener {

    private static final String MAZE_FRAGMENT_TAG = "MazeFragment";

    private MazeViewModel mazeViewModel;

    private static MazeFragment instance;

    private static final String N_A_COORDINATES = "N/A";
    private static final String INITIAL_START_COORDINATES = "(1, 1)";

    // Robot status texts
    private static final String IDLE_ROBOT_STATUS = "Idle";
    private static final String RUNNING_ROBOT_STATUS = "Running";
    private static final String CALIBRATING_ROBOT_STATUS = "Calibrating";
    private static final String REACHED_GOAL_ROBOT_STATUS = "Reached Goal";

    // Snackbar messages
    private static final String AUTO_MAZE_UPDATE_IS_SWITCHED_OFF = "Auto maze update mode is switched OFF";
    private static final String AUTO_MAZE_UPDATE_IS_SWITCHED_ON = "Auto maze update mode is switched ON";
    private static final String TILT_SENSING_MODE_IS_SWITCHED_OFF = "Tilt sensing mode is switched OFF";
    private static final String TILT_SENSING_MODE_IS_SWITCHED_ON = "Tilt sensing mode is switched ON";
    private static final String FASTEST_PATH_TASK_STARTED = "Fastest path task started";
    private static final String EXPLORATION_TASK_STARTED = "Exploration task started";
    private static final String MAZE_DISPLAY_UPDATED = "Maze display updated";
    private static final String WAYPOINT_POSITION_UPDATED_TO = "Waypoint position updated to ";
    private static final String ROBOT_START_POSITION_UPDATED_TO = "Robot start position updated to";

    // Save maze status keys
    private static final String ROBOT_COORDINATE_X = "Robot Coordinate X";
    private static final String ROBOT_COORDINATE_Y = "Robot Coordinate Y";
    private static final String ROBOT_DIRECTION = "Robot Direction";
    private static final String START_COORDINATE_X = "Start Coordinate X";
    private static final String START_COORDINATE_Y = "Start Coordinate Y";
    private static final String WAYPOINT_COORDINATE_X = "Waypoint Coordinate X";
    private static final String WAYPOINT_COORDINATE_Y = "Waypoint Coordinate Y";
    private static final String MDF_STRING = "MDF String";

    // Maze auto update
    private MazeUpdateMode mazeUpdateMode;
    private Timer timer;
    private SendMazeUpdateRequestTask sendMazeUpdateRequestTask;
    private static final int MAZE_UPDATE_DELAY = 0;
    private static final int MAZE_UPDATE_INTERVAL = 5000;

    // Tilt sensing
    private boolean tiltSensingMode;
    private SensorManager sensorManager;
    private static final int TILT_SENSOR_DELAY_MILLISECONDS = 1500;
    private boolean delayingTiltSensing;

    private RobotStatus robotStatus;

    private MazeView mazeView;
    private TextView textViewRobotStatus;
    private TextView textViewWaypoint;
    private TextView textViewStartPostion;
    private TextView textViewSelectedGrid;
    private Button updateWaypointButton;
    private Button updateStartPositionButton;
    private Button manualUpdateButton;
    private Button moveForwardButton;
    private Button turnLeftButton;
    private Button turnRightButton;
    private Button startFastestPathButton;
    private Button startExplorationButton;
    private ToggleButton autoUpdateModeToggleButton;
    private ToggleButton tiltSensingToggleButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mazeViewModel =
                new ViewModelProvider(this).get(MazeViewModel.class);

        instance = this;

        // Maze auto update
        mazeUpdateMode = MazeUpdateMode.MANUAL;
        timer = new Timer();
        sendMazeUpdateRequestTask = new SendMazeUpdateRequestTask();

        // Tilt sensing
        tiltSensingMode = false;
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        delayingTiltSensing = false;

        robotStatus = RobotStatus.IDLE;

        View root = inflater.inflate(R.layout.fragment_maze, container, false);
        mazeView = root.findViewById(R.id.mazeView);
        textViewRobotStatus = root.findViewById(R.id.robotStatusTextView);
        textViewWaypoint = root.findViewById(R.id.waypointTextView);
        textViewStartPostion = root.findViewById(R.id.startPositionTextView);
        textViewSelectedGrid = root.findViewById(R.id.selectedGridTextView);
        updateWaypointButton = root.findViewById(R.id.updateWaypointButton);
        updateStartPositionButton = root.findViewById(R.id.updateStartPositionButton);
        manualUpdateButton = root.findViewById(R.id.manualUpdateButton);
        moveForwardButton = root.findViewById(R.id.moveForwardButton);
        turnLeftButton = root.findViewById(R.id.turnLeftButton);
        turnRightButton = root.findViewById(R.id.turnRightButton);
        startFastestPathButton = root.findViewById(R.id.startFastestPathButton);
        startExplorationButton = root.findViewById(R.id.startExplorationButton);
        autoUpdateModeToggleButton = root.findViewById(R.id.autoUpdateModeToggleButton);
        tiltSensingToggleButton = root.findViewById(R.id.tiltSensingToggleButton);

        timer.schedule(sendMazeUpdateRequestTask, MAZE_UPDATE_DELAY, MAZE_UPDATE_INTERVAL);

        updateRobotStatusTextView();
        textViewWaypoint.setText(N_A_COORDINATES);
        textViewStartPostion.setText(INITIAL_START_COORDINATES);
        textViewSelectedGrid.setText(N_A_COORDINATES);

        updateWaypointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mazeView.updateWaypointCoordinates();
                MainActivity.sendWaypointPosition(mazeView.getWaypointCoordinates());
                Snackbar.make(view, WAYPOINT_POSITION_UPDATED_TO + textViewWaypoint.getText(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        updateStartPositionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mazeView.updateStartCoordinates();
                MainActivity.sendRobotStartPosition(mazeView.getStartCoordinates());
                Snackbar.make(view, ROBOT_START_POSITION_UPDATED_TO + textViewStartPostion.getText(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        manualUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendMazeUpdateRequest();
                Snackbar.make(view, MAZE_DISPLAY_UPDATED, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        moveForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendRobotMoveForwardCommand();
            }
        });
        turnLeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendRobotTurnLeftCommand();
            }
        });
        turnRightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendRobotTurnRightCommand();
            }
        });

        startFastestPathButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendStartFastestPathCommand();
                Snackbar.make(view, FASTEST_PATH_TASK_STARTED, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        startExplorationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.sendStartExplorationCommand();
                Snackbar.make(view, EXPLORATION_TASK_STARTED, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        autoUpdateModeToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mazeUpdateMode == MazeUpdateMode.AUTO) {
                    mazeUpdateMode = MazeUpdateMode.MANUAL;
                    Snackbar.make(view, AUTO_MAZE_UPDATE_IS_SWITCHED_OFF, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    mazeUpdateMode = MazeUpdateMode.AUTO;
                    Snackbar.make(view, AUTO_MAZE_UPDATE_IS_SWITCHED_ON, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        tiltSensingToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (tiltSensingMode) {
                    tiltSensingMode = false;
                    Snackbar.make(view, TILT_SENSING_MODE_IS_SWITCHED_OFF, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    tiltSensingMode = true;
                    Snackbar.make(view, TILT_SENSING_MODE_IS_SWITCHED_ON, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save robot coordinates and direction
        editor.putInt(ROBOT_COORDINATE_X, mazeView.getRobotCoordinates()[0]);
        editor.putInt(ROBOT_COORDINATE_Y, mazeView.getRobotCoordinates()[1]);
        editor.putInt(ROBOT_DIRECTION, mazeView.getRobotDirection());

        // Save start coordinates
        editor.putInt(START_COORDINATE_X, mazeView.getStartCoordinates()[0]);
        editor.putInt(START_COORDINATE_Y, mazeView.getStartCoordinates()[1]);

        // Save waypoint coordinates
        editor.putInt(WAYPOINT_COORDINATE_X, mazeView.getWaypointCoordinates()[0]);
        editor.putInt(WAYPOINT_COORDINATE_Y, mazeView.getWaypointCoordinates()[1]);

        // Save obstacles
        editor.putString(MDF_STRING, mazeView.getMdfString());

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Reload robot coordinates and direction
        int[] robotCoordinates = new int[2];
        robotCoordinates[0] = sharedPreferences.getInt(ROBOT_COORDINATE_X, 1);
        robotCoordinates[1] = sharedPreferences.getInt(ROBOT_COORDINATE_Y, 1);
        mazeView.reloadRobotCoordinates(robotCoordinates);
        int robotDirection = sharedPreferences.getInt(ROBOT_DIRECTION, 0);
        mazeView.reloadRobotDirection(robotDirection);

        // Reload start coordinates
        int[] startCoordinates = new int[2];
        startCoordinates[0] = sharedPreferences.getInt(START_COORDINATE_X, 1);
        startCoordinates[1] = sharedPreferences.getInt(START_COORDINATE_Y, 1);
        mazeView.reloadStartCoordinates(startCoordinates);

        // Reload waypoint coordinates
        int[] waypointCoordinates = new int[2];
        waypointCoordinates[0] = sharedPreferences.getInt(WAYPOINT_COORDINATE_X, -1);
        waypointCoordinates[1] = sharedPreferences.getInt(WAYPOINT_COORDINATE_Y, -1);
        mazeView.reloadWaypointCoordinates(waypointCoordinates);

        // Reload obstacles
        mazeView.updateObstacles(sharedPreferences.getString(MDF_STRING, MazeView.DEFAULT_MDF_STRING));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Public Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static MazeFragment getInstance() {
        return instance;
    }

    public void updateRobotDisplay(int[] robotCoordinates, int robotDirection) {
        Log.d(MAZE_FRAGMENT_TAG, "Updating robot coordinates: " + robotCoordinates[0] + ", " + robotCoordinates[1]);
        Log.d(MAZE_FRAGMENT_TAG, "Updating robot direction: " + robotDirection);

        mazeView.updateRobotCoordinatesAndDirection(robotCoordinates, robotDirection);
    }

    public void updateRobotStatus(String robotStatusString) {
        Log.d(MAZE_FRAGMENT_TAG, "Updating robot status: " + robotStatusString);

        if (robotStatusString.equalsIgnoreCase("IDLE")) {
            robotStatus = RobotStatus.IDLE;
        } else if (robotStatusString.equalsIgnoreCase("RUNNING")) {
            robotStatus = RobotStatus.RUNNING;
        } else if (robotStatusString.equalsIgnoreCase("CALIBRATING")) {
            robotStatus = RobotStatus.CALIBRATING;
        } else if (robotStatusString.equalsIgnoreCase("REACHED_GOAL")) {
            robotStatus = RobotStatus.REACHED_GOAL;
        }

        updateRobotStatusTextView();
    }

    public void updateObstacles(String mdfString) {
        Log.d(MAZE_FRAGMENT_TAG, "Updating obstacles in maze: " + mdfString);

        mazeView.updateObstacles(mdfString);
    }

    public void updateImageInfoList(List<int[]> imageInfoList) {
        Log.d(MAZE_FRAGMENT_TAG, "Updating image info list: " + Arrays.deepToString(imageInfoList.toArray()));

        mazeView.updateImageInfoList(imageInfoList);
    }

    public void sendMessageToCommunicationFragment(String newReceivedString) {
        Log.d(MAZE_FRAGMENT_TAG, "Sending getTextFromDevice intent with new string: " + newReceivedString);
        Intent intent = new Intent("getTextFromDevice");
        intent.putExtra("text", newReceivedString);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////          TextView Update Methods         ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void updateSelectedGridTextView(int[] selectedPosition) {
        if (selectedPosition[0] < 0 || selectedPosition[1] < 0) {
            textViewSelectedGrid.setText(N_A_COORDINATES);
        } else {
            textViewSelectedGrid.setText(String.format("(%d, %d)", selectedPosition[0], selectedPosition[1]));
        }
    }

    protected void updateWaypointTextView(int[] waypointCoordinates) {
        if (waypointCoordinates[0] < 0 || waypointCoordinates[1] < 0) {
            textViewWaypoint.setText(N_A_COORDINATES);
        } else {
            textViewWaypoint.setText(String.format("(%d, %d)", waypointCoordinates[0], waypointCoordinates[1]));
        }
    }

    protected void updateStartPositionTextView(int[] startCoordinates) {
        if (startCoordinates[0] < 0 || startCoordinates[1] < 0) {
            textViewStartPostion.setText(N_A_COORDINATES);
        } else {
            textViewStartPostion.setText(String.format("(%d, %d)", startCoordinates[0], startCoordinates[1]));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////            Tilt sensing Methods          ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        if (!delayingTiltSensing) {
            if (tiltSensingToggleButton.isChecked()) { // move robot only if tilt has been enabled
                if (y < -5) { // device has been tilted forward
                    MainActivity.sendRobotMoveForwardCommand();
                } else if (x < -5) { // device has been tilted to the right
                    MainActivity.sendRobotTurnRightCommand();
                } else if (x > 5) { // device has been tilted to the left
                    MainActivity.sendRobotTurnLeftCommand();
                }
            }

            delayingTiltSensing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    delayingTiltSensing = false;
                }
            }, TILT_SENSOR_DELAY_MILLISECONDS);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Helper Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateRobotStatusTextView() {
        if (robotStatus.equals(RobotStatus.IDLE)) {
            textViewRobotStatus.setText(IDLE_ROBOT_STATUS);
        } else if (robotStatus.equals(RobotStatus.RUNNING)) {
            textViewRobotStatus.setText(RUNNING_ROBOT_STATUS);
        } else if (robotStatus.equals(RobotStatus.CALIBRATING)) {
            textViewRobotStatus.setText(CALIBRATING_ROBOT_STATUS);
        } else if (robotStatus.equals(RobotStatus.REACHED_GOAL)) {
            textViewRobotStatus.setText(REACHED_GOAL_ROBOT_STATUS);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////           Enums & Inner Classes          ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    enum RobotStatus {
        IDLE,
        RUNNING,
        CALIBRATING,
        REACHED_GOAL,
    }

    enum MazeUpdateMode {
        AUTO,
        MANUAL,
    }

    class SendMazeUpdateRequestTask extends TimerTask {
        public void run() {
            if (autoUpdateModeToggleButton.isChecked()) {
                MainActivity.sendMazeUpdateRequest();
            }
        }
    }
}
