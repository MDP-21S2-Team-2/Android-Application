package com.example.mdpapplication.ui.maze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;
import com.google.android.material.snackbar.Snackbar;

public class MazeFragment extends Fragment {

    private MazeViewModel mazeViewModel;

    private static MazeFragment instance;

    private static final String N_A_COORDINATES = "N/A";
    private static final String INITIAL_START_COORDINATES = "(1, 1)";
    private static final String IDLE_ROBOT_STATUS = "Idle";
    private static final String RUNNING_ROBOT_STATUS = "Running";
    private static final String CALIBRATING_ROBOT_STATUS = "Calibrating";
    private static final String REACHED_GOAL_ROBOT_STATUS = "Reached Goal";
    private static final String AUTO_MAZE_UPDATE_IS_SWITCHED_OFF = "Auto maze update mode is switched OFF";
    private static final String AUTO_MAZE_UPDATE_IS_SWITCHED_ON = "Auto maze update mode is switched ON";
    private static final String TILT_SENSING_MODE_IS_SWITCHED_OFF = "Tilt sensing mode is switched OFF";
    private static final String TILT_SENSING_MODE_IS_SWITCHED_ON = "Tilt sensing mode is switched ON";

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
    private ToggleButton autoUpdateModeToggleButton;
    private ToggleButton tiltSensingToggleButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mazeViewModel =
                new ViewModelProvider(this).get(MazeViewModel.class);

        instance = this;

        robotStatus = RobotStatus.IDLE;
        mazeUpdateMode = MazeUpdateMode.MANUAL;
        tiltSensingMode = false;

        View root = inflater.inflate(R.layout.fragment_maze, container, false);
        mazeView = root.findViewById(R.id.mazeView);
        textViewRobotStatus = root.findViewById(R.id.robotStatusTextView); // TODO: Update robot status based on data received
        textViewWaypoint = root.findViewById(R.id.waypointTextView);
        textViewStartPostion = root.findViewById(R.id.startPositionTextView);
        textViewSelectedGrid = root.findViewById(R.id.selectedGridTextView);
        updateWaypointButton = root.findViewById(R.id.updateWaypointButton);
        updateStartPositionButton = root.findViewById(R.id.updateStartPositionButton);
        manualUpdateButton = root.findViewById(R.id.manualUpdateButton);
        moveForwardButton = root.findViewById(R.id.moveForwardButton);
        turnLeftButton = root.findViewById(R.id.turnLeftButton);
        turnRightButton = root.findViewById(R.id.turnRightButton);
        autoUpdateModeToggleButton = root.findViewById(R.id.autoUpdateModeToggleButton); // TODO: Automatically query for maze update when auto update mode is on
        tiltSensingToggleButton = root.findViewById(R.id.tiltSensingToggleButton); // TODO: Implement tilt sensing control

        updateRobotStatusTextView();
        textViewWaypoint.setText(N_A_COORDINATES);
        textViewStartPostion.setText(INITIAL_START_COORDINATES);
        textViewSelectedGrid.setText(N_A_COORDINATES);

        updateWaypointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mazeView.updateWaypointCoordinates();
            }
        });

        updateStartPositionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mazeView.updateStartCoordinates();
            }
        });

        manualUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Query for maze update and update maze display when button is clicked
            }
        });

        moveForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send move forward signal via Bluetooth
            }
        });

        turnLeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send turn left signal via Bluetooth
            }
        });

        turnRightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: Send turn right signal via Bluetooth
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

    public static MazeFragment getInstance() {
        return instance;
    }

    private void updateRobotStatusTextView() {
        switch (robotStatus) {
            case RUNNING:
                textViewRobotStatus.setText(RUNNING_ROBOT_STATUS);
            case CALIBRATING:
                textViewRobotStatus.setText(CALIBRATING_ROBOT_STATUS);
            case REACHED_GOAL:
                textViewRobotStatus.setText(REACHED_GOAL_ROBOT_STATUS);
            default:
                textViewRobotStatus.setText(IDLE_ROBOT_STATUS);
        }
    }

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
        // TODO: Send waypoint position via Bluetooth
    }

    protected void updateStartPositionTextView(int[] startCoordinates) {
        if (startCoordinates[0] < 0 || startCoordinates[1] < 0) {
            textViewStartPostion.setText(N_A_COORDINATES);
        } else {
            textViewStartPostion.setText(String.format("(%d, %d)", startCoordinates[0], startCoordinates[1]));
        }
        // TODO: Send start position via Bluetooth
    }

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
}