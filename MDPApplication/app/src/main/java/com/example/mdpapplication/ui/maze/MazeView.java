package com.example.mdpapplication.ui.maze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class MazeView extends View {

    private static final int COLUMN_NUM = 15;
    private static final int ROW_NUM = 20;
    public static final int START_ZONE_SIZE = 3;
    public static final int GOAL_ZONE_SIZE = 3;
    private static Grid[][] grids;
    private static int gridSize;

    private final Paint gridLinePaint;
    private final Paint goalPaint;
    private final Paint startPaint;
    private final Paint emptyGridPaint;

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        gridLinePaint = new Paint();
        goalPaint = new Paint();
        startPaint = new Paint();
        emptyGridPaint = new Paint();

        gridLinePaint.setColor(Color.WHITE);
        goalPaint.setColor(Color.CYAN);
        startPaint.setColor(Color.CYAN);
        emptyGridPaint.setColor(Color.LTGRAY);

        createMaze();
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Calculate grid size based on canvas dimensions
        gridSize = Math.min(getWidth() / COLUMN_NUM, getHeight() / ROW_NUM);
        this.setLayoutParams(new RelativeLayout.LayoutParams(gridSize * COLUMN_NUM, gridSize * ROW_NUM));

        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGrids(canvas);
        drawGridLines(canvas);
        drawStartZone(canvas);
        drawGoalZone(canvas);

        // TODO: Draw Robot
        // TODO: Draw Obstacles
        // TODO: Draw waypoint
        // TODO: Draw image number ID blocks
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
        for (int i = COLUMN_NUM - GOAL_ZONE_SIZE; i < COLUMN_NUM; i++)
            for (int j = ROW_NUM - GOAL_ZONE_SIZE; j < ROW_NUM; j++)
                canvas.drawRect(i * gridSize, (ROW_NUM - 1 - j) * gridSize,
                        (i + 1) * gridSize, (ROW_NUM - j) * gridSize, goalPaint);
    }

    private void createMaze() {
        grids = new Grid[COLUMN_NUM + 1][ROW_NUM + 1];
        for (int i = 0; i < COLUMN_NUM; i++) {
            for (int j = 0; j < ROW_NUM; j++) {
                grids[i][j] = new Grid(i, j);
            }
        }
    }

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
}
