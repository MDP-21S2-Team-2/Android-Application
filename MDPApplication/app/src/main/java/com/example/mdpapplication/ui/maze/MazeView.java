package com.example.mdpapplication.ui.maze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mdpapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MazeView extends View {

    protected static final String DEFAULT_MDF_STRING = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    protected static final int[] DEFAULT_ROBOT_COORDINATES = new int[]{1, 1};
    protected static final int DEFAULT_ROBOT_DIRECTION = 0;
    protected static final int[] DEFAULT_START_COORDINATES = new int[]{1, 1};
    protected static final int[] DEFAULT_WAYPOINT_COORDINATES = new int[]{-1, -1};
    private static final int[] DEFAULT_SELECTED_COORDINATES = new int[]{-1, -1};

    private static final int COLUMN_NUM = 15;
    private static final int ROW_NUM = 20;
    private static final int START_ZONE_SIZE = 3;
    private static final int GOAL_ZONE_SIZE = 3;

    private static Grid[][] grids;
    private RobotPosition robotPosition;
    private static boolean[][] obstacles;
    private static int[] selectedCoordinates;
    private static int[] waypointCoordinates;
    private static int[] startCoordinates;
    private static List<int[]> imageInfoList; // Format: [x, y, ID]

    private static int gridSize;

    private final Paint gridLinePaint;
    private final Paint emptyGridPaint;
    private final Paint goalPaint;
    private final Paint startPaint;
    private final Paint obstaclePaint;
    private final Paint selectedGridPaint;
    private final Paint waypointPaint;
    private final Paint numberIdPaint;

    private final MazeFragment mazeFragment;

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        gridLinePaint = new Paint();
        emptyGridPaint = new Paint();
        goalPaint = new Paint();
        startPaint = new Paint();
        obstaclePaint = new Paint();
        selectedGridPaint = new Paint();
        waypointPaint = new Paint();
        numberIdPaint = new Paint();

        gridLinePaint.setColor(Color.WHITE);
        emptyGridPaint.setColor(Color.LTGRAY);
        goalPaint.setColor(Color.CYAN);
        startPaint.setColor(Color.CYAN);
        obstaclePaint.setColor(Color.BLACK);
        selectedGridPaint.setColor(Color.BLUE);
        waypointPaint.setColor(Color.GREEN);
        numberIdPaint.setColor(Color.WHITE);
        numberIdPaint.setTextSize(30f);

        robotPosition = new RobotPosition(DEFAULT_ROBOT_COORDINATES, DEFAULT_ROBOT_DIRECTION);

        obstacles = new boolean[COLUMN_NUM][ROW_NUM];

        selectedCoordinates = new int[2];
        selectedCoordinates[0] = DEFAULT_SELECTED_COORDINATES[0];
        selectedCoordinates[1] = DEFAULT_SELECTED_COORDINATES[1];

        startCoordinates = new int[2];
        startCoordinates[0] = DEFAULT_START_COORDINATES[0];
        startCoordinates[1] = DEFAULT_START_COORDINATES[1];

        waypointCoordinates = new int[2];
        waypointCoordinates[0] = DEFAULT_WAYPOINT_COORDINATES[0];
        waypointCoordinates[1] = DEFAULT_WAYPOINT_COORDINATES[1];

        imageInfoList = new ArrayList<>();

        createMaze();

        mazeFragment = MazeFragment.getInstance();
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Calculate grid size based on canvas dimensions
        gridSize = Math.min(getWidth() / COLUMN_NUM, getHeight() / ROW_NUM);
        // TODO: Fix the layout params
//        this.setLayoutParams(new ConstraintLayout.LayoutParams(gridSize * COLUMN_NUM, gridSize * ROW_NUM));

        super.invalidate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Drawing Methods             ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGrids(canvas);
        drawGridLines(canvas);
        drawStartZone(canvas);
        drawGoalZone(canvas);
        drawRobot(canvas);
        drawObstacles(canvas);
        drawSelectedPoint(canvas);
        drawWaypoint(canvas);
        drawImageNumberIds(canvas);
    }

    private void drawGrids(Canvas canvas) {
        for (int i = 0; i <= COLUMN_NUM - 1; i++) {
            for (int j = 0; j <= ROW_NUM - 1; j++) {
                canvas.drawRect(i * gridSize, (ROW_NUM - 1 - j) * gridSize,
                        (i + 1) * gridSize, (ROW_NUM - j) * gridSize, emptyGridPaint);
            }
        }
    }

    private void drawGridLines(Canvas canvas) {
        for (int col = 0; col < COLUMN_NUM + 1; col++) {
            canvas.drawLine(col * gridSize, 0, col * gridSize, ROW_NUM * gridSize, gridLinePaint);
        }

        for (int row = 0; row < ROW_NUM + 1; row++) {
            canvas.drawLine(0, row * gridSize, COLUMN_NUM * gridSize, row * gridSize, gridLinePaint);
        }
    }

    private void drawStartZone(Canvas canvas) {
        for (int i = 0; i < START_ZONE_SIZE; i++) {
            for (int j = 0; j < START_ZONE_SIZE; j++) {
                canvas.drawRect(i * gridSize, (ROW_NUM - 1 - j) * gridSize,
                        (i + 1) * gridSize, (ROW_NUM - j) * gridSize, startPaint);
            }
        }
    }

    private void drawGoalZone(Canvas canvas) {
        for (int i = COLUMN_NUM - GOAL_ZONE_SIZE; i < COLUMN_NUM; i++) {
            for (int j = ROW_NUM - GOAL_ZONE_SIZE; j < ROW_NUM; j++) {
                canvas.drawRect(i * gridSize, (ROW_NUM - 1 - j) * gridSize,
                        (i + 1) * gridSize, (ROW_NUM - j) * gridSize, goalPaint);
            }
        }
    }

    private void drawRobot(Canvas canvas) {
        // Do not draw robot if coordinates are invalid
        if (robotPosition.robotCoordinates[0] < 0
                || robotPosition.robotCoordinates[0] >= COLUMN_NUM
                || robotPosition.robotCoordinates[1] < 0
                || robotPosition.robotCoordinates[1] >= ROW_NUM) {
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot);
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, gridSize * 3, gridSize * 3, false);
        Matrix matrix = new Matrix();
        matrix.setRotate(robotPosition.robotDirection, resizeBitmap.getWidth(), resizeBitmap.getHeight());
        switch (robotPosition.robotDirection) {
            case 90:
                matrix.postTranslate((robotPosition.robotCoordinates[0] - 4) * gridSize, (ROW_NUM - robotPosition.robotCoordinates[1] - 2) * gridSize);
                break;
            case 180:
                matrix.postTranslate((robotPosition.robotCoordinates[0] - 4) * gridSize, (ROW_NUM - robotPosition.robotCoordinates[1] - 5) * gridSize);
                break;
            case 270:
                matrix.postTranslate((robotPosition.robotCoordinates[0] - 1) * gridSize, (ROW_NUM - robotPosition.robotCoordinates[1] - 5) * gridSize);
                break;
            default:
                matrix.postTranslate((robotPosition.robotCoordinates[0] - 1) * gridSize, (ROW_NUM - robotPosition.robotCoordinates[1] - 2) * gridSize);
                break;
        }
        canvas.drawBitmap(resizeBitmap, matrix, emptyGridPaint);
    }

    private void drawObstacles(Canvas canvas) {
        for (int i = 0; i < COLUMN_NUM; i++) {
            for (int j = 0; j < ROW_NUM; j++) {
                if (obstacles[i][j]) {
                    canvas.drawRect(i * gridSize, (ROW_NUM - 1 - j) * gridSize,
                            (i + 1) * gridSize, (ROW_NUM - j) * gridSize, obstaclePaint);
                }
            }
        }
    }

    private void drawSelectedPoint(Canvas canvas) {
        // Do not draw robot if coordinates are invalid
        if (selectedCoordinates[0] < 0
                || selectedCoordinates[0] >= COLUMN_NUM
                || selectedCoordinates[1] < 0
                || selectedCoordinates[1] >= ROW_NUM) {
            return;
        }

        canvas.drawRect(selectedCoordinates[0] * gridSize, (ROW_NUM - 1 - selectedCoordinates[1]) * gridSize,
                (selectedCoordinates[0] + 1) * gridSize, (ROW_NUM - selectedCoordinates[1]) * gridSize, selectedGridPaint);
    }

    private void drawWaypoint(Canvas canvas) {
        // Do not draw robot if coordinates are invalid
        if (waypointCoordinates[0] < 0
                || waypointCoordinates[0] >= COLUMN_NUM
                || waypointCoordinates[1] < 0
                || waypointCoordinates[1] >= ROW_NUM) {
            return;
        }

        canvas.drawRect(waypointCoordinates[0] * gridSize, (ROW_NUM - 1 - waypointCoordinates[1]) * gridSize,
                (waypointCoordinates[0] + 1) * gridSize, (ROW_NUM - waypointCoordinates[1]) * gridSize, waypointPaint);
    }

    private void drawImageNumberIds(Canvas canvas) {
        // Do not draw if there is no identified image
        if (imageInfoList.isEmpty()) {
            return;
        }

        for (int[] imageInfo : imageInfoList) {
            int imageX = imageInfo[0];
            int imageY = imageInfo[1];
            int imageNumberId = imageInfo[2];

            // Draw the obstacle block
            canvas.drawRect(imageX * gridSize, (ROW_NUM - 1 - imageY) * gridSize,
                    (imageX + 1) * gridSize, (ROW_NUM - imageY) * gridSize, obstaclePaint);

            // Draw the image number ID
            int pixelY = (ROW_NUM - imageY) * gridSize - 12;
            if (imageNumberId < 10 && imageNumberId > 0) {
                canvas.drawText(String.valueOf(imageNumberId), imageX * gridSize + 10, pixelY, numberIdPaint);
            } else if (imageNumberId > 9 && imageNumberId < 16) {
                canvas.drawText(String.valueOf(imageNumberId), imageX * gridSize + 3, pixelY, numberIdPaint);
            }
        }
    }

    private void createMaze() {
        grids = new Grid[COLUMN_NUM][ROW_NUM];
        for (int i = 0; i < COLUMN_NUM; i++) {
            for (int j = 0; j < ROW_NUM; j++) {
                grids[i][j] = new Grid(i, j);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        int x = (int) (event.getX() / gridSize);
        int y = ROW_NUM - 1 - (int) (event.getY() / gridSize);

        selectedCoordinates[0] = (x == selectedCoordinates[0] && y == selectedCoordinates[1]) ? DEFAULT_SELECTED_COORDINATES[0] : x;
        selectedCoordinates[1] = (x == selectedCoordinates[0] && y == selectedCoordinates[1]) ? DEFAULT_SELECTED_COORDINATES[1] : y;

        // Redraw the canvas
        invalidate();

        mazeFragment.updateSelectedGridTextView(selectedCoordinates);

        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////         Parameters Update Methods        ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected void updateRobotCoordinatesAndDirection(int[] robotCoordinates, int robotDirection) {
        robotPosition.robotCoordinates = robotCoordinates;
        robotPosition.robotDirection = robotDirection;

        // Redraw the canvas
        invalidate();
    }

    protected int[] getRobotCoordinates() {
        return robotPosition.robotCoordinates;
    }

    protected int getRobotDirection() {
        return robotPosition.robotDirection;
    }

    protected void reloadRobotCoordinates(int[] coordinates) {
        robotPosition.robotCoordinates[0] = coordinates[0];
        robotPosition.robotCoordinates[1] = coordinates[1];

        // Redraw the canvas
        invalidate();
    }

    protected void reloadRobotDirection(int robotDirection) {
        robotPosition.robotDirection = robotDirection;

        // Redraw the canvas
        invalidate();
    }

    protected void updateStartCoordinates() {
        // Update start coordinates
        startCoordinates[0] = selectedCoordinates[0];
        startCoordinates[1] = selectedCoordinates[1];

        // Update robot position
        robotPosition.robotCoordinates[0] = startCoordinates[0];
        robotPosition.robotCoordinates[1] = startCoordinates[1];

        // Clear selected grid
        selectedCoordinates[0] = DEFAULT_SELECTED_COORDINATES[0];
        selectedCoordinates[1] = DEFAULT_SELECTED_COORDINATES[1];

        // Redraw the canvas
        invalidate();

        mazeFragment.updateStartPositionTextView(startCoordinates);
        mazeFragment.updateSelectedGridTextView(selectedCoordinates);
    }

    protected int[] getStartCoordinates() {
        return startCoordinates;
    }

    protected void reloadStartCoordinates(int[] coordinates) {
        startCoordinates[0] = coordinates[0];
        startCoordinates[1] = coordinates[1];

        mazeFragment.updateStartPositionTextView(coordinates);
    }

    protected void updateWaypointCoordinates() {
        // Update waypoint coordinates
        waypointCoordinates[0] = selectedCoordinates[0];
        waypointCoordinates[1] = selectedCoordinates[1];

        // Clear selected grid
        selectedCoordinates[0] = DEFAULT_SELECTED_COORDINATES[0];
        selectedCoordinates[1] = DEFAULT_SELECTED_COORDINATES[1];

        // Redraw the canvas
        invalidate();

        mazeFragment.updateWaypointTextView(waypointCoordinates);
        mazeFragment.updateSelectedGridTextView(selectedCoordinates);
    }

    protected int[] getWaypointCoordinates() {
        return waypointCoordinates;
    }

    protected void reloadWaypointCoordinates(int[] coordinates) {
        waypointCoordinates[0] = coordinates[0];
        waypointCoordinates[1] = coordinates[1];

        mazeFragment.updateWaypointTextView(waypointCoordinates);

        // Redraw the canvas
        invalidate();
    }

    protected void updateObstacles(String mdfString) {
        for (int i = 0; i < COLUMN_NUM; i++) {
            for (int j = 0; j < ROW_NUM; j++) {
                obstacles[i][j] = mdfString.charAt(j * COLUMN_NUM + i) == '1';
            }
        }

        // Redraw the canvas
        invalidate();
    }

    protected String getMdfString() {
        StringBuilder mdfStringBuilder = new StringBuilder();
        for (int j = 0; j < ROW_NUM; j++) {
            for (int i = 0; i < COLUMN_NUM; i++) {
                mdfStringBuilder.append(obstacles[i][j] ? "1" : "0");
            }
        }
        return mdfStringBuilder.toString();
    }

    protected void updateImageInfoList(List<int[]> newImageInfoList) {
        imageInfoList = newImageInfoList;

        // Redraw the canvas
        invalidate();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////               Inner Classes              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    Class representing one grid in the maze.
     */
    private static class Grid {
        int col, row;

        public Grid(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    /*
    Class representing current robot position in the maze.
     */
    private static class RobotPosition {
        int[] robotCoordinates;
        int robotDirection; // 0, 90, 180, 270

        public RobotPosition(int[] robotCoordinates, int robotDirection) {
            this.robotCoordinates = robotCoordinates;
            this.robotDirection = robotDirection;
        }
    }
}
