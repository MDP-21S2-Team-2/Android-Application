package com.example.mdpapplication.ui.maze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;

public class MazeFragment extends Fragment {

    private MazeViewModel mazeViewModel;

    private static final String N_A_COORDINATES = "N/A";

    private MazeView mazeView;
    private TextView textViewRobotStatus;
    private TextView textViewWaypoint;
    private TextView textViewStartPostion;
    private TextView textViewSelectedGrid;
    private Button updateWaypointButton;
    private Button updateStartPositionButton;

    private RobotStatus robotStatus;

    private static MazeFragment instance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mazeViewModel =
                new ViewModelProvider(this).get(MazeViewModel.class);

        instance = this;

        View root = inflater.inflate(R.layout.fragment_maze, container, false);
        mazeView = root.findViewById(R.id.mazeView);
        textViewRobotStatus = root.findViewById(R.id.robotStatusTextView); // TODO: Update robot status based on data received
        textViewWaypoint = root.findViewById(R.id.waypointTextView); // TODO: Update waypoint text based on user action
        textViewStartPostion = root.findViewById(R.id.startPositionTextView); // TODO: Update start position text based on user action
        textViewSelectedGrid = root.findViewById(R.id.selectedGridTextView); // TODO: Update selected grid text based on user action
        updateWaypointButton = root.findViewById(R.id.updateWaypointButton);
        updateStartPositionButton = root.findViewById(R.id.updateStartPositionButton);

        textViewSelectedGrid.setText(N_A_COORDINATES);

        updateWaypointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mazeView.updateWaypointCoordinates();
            }
        });

        updateStartPositionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mazeView.updateStartCoordinates();
            }
        });

        return root;
    }

    public static MazeFragment getInstance() {
        return instance;
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
    }

    protected void updateStartPositionTextView(int[] startCoordinates) {
        if (startCoordinates[0] < 0 || startCoordinates[1] < 0) {
            textViewStartPostion.setText(N_A_COORDINATES);
        } else {
            textViewStartPostion.setText(String.format("(%d, %d)", startCoordinates[0], startCoordinates[1]));
        }
    }

    enum RobotStatus {
        IDLE,
        RUNNING,
        CALIBRATING,
        REACHED_GOAL,
    }
}