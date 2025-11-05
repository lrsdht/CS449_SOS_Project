package sprint_3;

enum Move { EMPTY, S, O};

public class Board {

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
    } // constructor

    public int getSize() {
        return size;
    }
    public Move getCell(int row, int column) {
        return grid[row][column];
    }
    public boolean isMoveInBounds(int row, int column) {
        return row >= 0 && row < size && column >= 0 && column < size;
    }

    // place move in grid
    public boolean place(int row, int column, Move m) {
        if ((!isMoveInBounds(row, column)) || m == Move.EMPTY) return false;
        if (grid[row][column] != Move.EMPTY) return false;
        grid[row][column] = m;
        return true;
    }

    public int isThereAnSOSFrom(int row, int column) {
        int count = 0;

        // direction from move: sideways, up/down, diagonal, diagonal reverse
        int[][] directions = {
                {0, 1}, // sideways
                {1, 0}, // up/down
                {1, 1}, // diagonal
                {1, -1} // diagonal reverse
        };

        for (int[] direction : directions) {
            int directionsRow = direction[0];
            int directionsColumn = direction[1];

            // move is in center of SOS
            int row1 = row - directionsRow;
            int column1 = column - directionsColumn;
            int row2 = row + directionsRow;
            int column2 = column + directionsColumn;

            if (isMoveInBounds(row1, column1) && isMoveInBounds(row2, column2)) {
                if (getCell(row1, column1) == Move.S && getCell(row, column) == Move.O && getCell(row2, column2) == Move.S) {
                    count++;
                }
            }

            // move is left and up of SOS
            int rCenter = row + directionsRow;
            int cCenter = column + directionsColumn;
            int rEnd = row + 2 * directionsRow;
            int cEnd = column + 2 * directionsColumn;

            if (isMoveInBounds(rCenter, cCenter) && isMoveInBounds(rEnd, cEnd)) {
                if (getCell(row, column) == Move.S && getCell(rCenter, cCenter) == Move.O && getCell(rEnd, cEnd) == Move.S) {
                    count++;
                }
            }

            int rBeginning = row - 2 * directionsRow;
            int cBeginning = column - 2 * directionsColumn;
            int rMiddle = row - directionsRow;
            int cMiddle = column - directionsColumn;

            if (isMoveInBounds(rBeginning, cBeginning) && isMoveInBounds(rMiddle, cMiddle)) {
                if (getCell(rBeginning, cBeginning) == Move.S && getCell(rMiddle, cMiddle) == Move.O && getCell(row, column) == Move.S) {
                    count++;
                }
            }
        }
        return count;
    } // gotta adjust this to return list of which way the SOS formed so can attach to GUI

    public boolean isGridFull(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == Move.EMPTY)
                    return false;
            }
        }
        return true;
    }

}
