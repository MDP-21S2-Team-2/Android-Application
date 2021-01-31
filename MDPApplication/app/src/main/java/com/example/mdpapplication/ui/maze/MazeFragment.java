package com.example.mdpapplication.ui.maze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpapplication.R;

public class MazeFragment extends Fragment {

    private MazeViewModel mazeViewModel;

    private MazeView mazeView;
    private TextView textViewRobotStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mazeViewModel =
                new ViewModelProvider(this).get(MazeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_maze, container, false);
        mazeView = root.findViewById(R.id.mazeView);
        textViewRobotStatus = root.findViewById(R.id.robotStatusTextView); // TODO: Update robot status based on data received

        return root;
    }
}