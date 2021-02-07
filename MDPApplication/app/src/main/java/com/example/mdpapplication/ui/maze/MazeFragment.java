package com.example.mdpapplication.ui.maze;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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

import com.example.mdpapplication.MainActivity;
import com.example.mdpapplication.R;
import com.google.android.material.snackbar.Snackbar;

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

    private Timer timer;
    private SendMazeUpdateRequestTask sendMazeUpdateRequestTask;
    private static final int MAZE_UPDATE_DELAY = 0;
    private static final int MAZE_UPDATE_INTERVAL = 5000;

    private SensorManager sensorManager;
    private static final int TILT_SENSOR_DELAY = (int) 5e+6; // TODO: Confirm tilt sensing delay interval

    private RobotStatus robotStatus;
    private MazeUpdateMode mazeUpdateMode;
    private boolean tiltSensingMode;

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

        timer = new Timer();
        sendMazeUpdateRequestTask = new SendMazeUpdateRequestTask();

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), TILT_SENSOR_DELAY);

        robotStatus = RobotStatus.IDLE;
        mazeUpdateMode = MazeUpdateMode.MANUAL;
        tiltSensingMode = false;

        View root = inflater.inflate(R.layout.fragment_maze, container, false);
        mazeView = root.findViewById(R.id.mazeView); // TODO: Update maze view based on data received
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
                MainActivity.sendMazeUpdateRequest(); // TODO: Update maze display when response is received
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Public Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static MazeFragment getInstance() {
        return instance;
    }

    public void updateRobotDisplay(int[] robotCoordinates, int robotDirection) {
        Log.d(MAZE_FRAGMENT_TAG, "Updating robot coordinates: " + robotCoordinates[0] + ", " + robotCoordinates[1]);
        Log.d(MAZE_FRAGMENT_TAG, "Updating robot direction: " + robotDirection);

        mazeView.updateRobotCoordinates(robotCoordinates, robotDirection);
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
        if (tiltSensingToggleButton.isChecked()) { // move robot only if tilt has been enabled
            if (y < -5) { // device has been tilted forward
                MainActivity.sendRobotMoveForwardCommand();
            } else if (x < -5) { // device has been tilted to the right
                MainActivity.sendRobotTurnRightCommand();
            } else if (x > 5) { // device has been tilted to the left
                MainActivity.sendRobotTurnLeftCommand();
            }
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