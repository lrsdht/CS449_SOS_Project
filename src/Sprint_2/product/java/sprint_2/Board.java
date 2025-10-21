package sprint_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class Board {
    public enum Move { EMPTY, S, O }

    private final int size;
    private final Move[][] grid;

    public Board(int size) {
        this.size = size;
        grid = new Move[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Move.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Move getCell(int row, int column) {
        return grid[row][column];
    }

    // Place move in grid
    public boolean place(int row, int column, Move m) {
        if ((!whereIn(row, column)) || m == Move.EMPTY) return false;
        if (grid[row][column] != Move.EMPTY) return false;
        grid[row][column] = m;
        return true;
    }

    public boolean whereIn(int row, int column) {
        return (row >= 0) && (row < size) && (column >= 0) && (column < size);
    }
}