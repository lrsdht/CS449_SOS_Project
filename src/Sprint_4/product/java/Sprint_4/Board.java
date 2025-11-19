package Sprint_4;

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

    // AI needs deep copy of board for minimax algo
    public static Board AIBoardCopy(Board AIBoard) {
        Board copyForAI = new Board(AIBoard.size);
        for (int i= 0; i < AIBoard.size; i++) {
            System.arraycopy(AIBoard.grid[i], 0, copyForAI.grid[i], 0, AIBoard.size);
        }
        return copyForAI;
    }

    void setCellForAIAlgorithm(int row, int column, Move m) {
        grid[row][column] = m;
    }

    // Count how many SOS would be created IF we place `m` at (r,c) on the CURRENT board.
// Does not mutate the board.
    public int countSOSFromPlacement(int r, int c, Move m) {
        if (!isMoveInBounds(r, c) || getCell(r, c) != Move.EMPTY || m == Move.EMPTY) return 0;

        int total = 0;

        // directions (dr,dc) for a line; we’ll reuse for symmetric checks
        int[][] dirs = {
                {0, 1},   // horizontal →
                {1, 0},   // vertical ↓
                {1, 1},   // diag down-right
                {1, -1}   // diag down-left
        };

        if (m == Move.O) {
            // O in the middle: check S-O-S along each line where (r,c) is the center
            for (int[] d : dirs) {
                int r1 = r - d[0], c1 = c - d[1];
                int r2 = r + d[0], c2 = c + d[1];
                if (isMoveInBounds(r1, c1) && isMoveInBounds(r2, c2)) {
                    if (getCell(r1, c1) == Move.S && getCell(r2, c2) == Move.S) {
                        total++;
                    }
                }
            }
        } else { // m == Move.S
            // S at one end of S-O-S (two orientations per direction)
            for (int[] d : dirs) {
                int rA = r + d[0],     cA = c + d[1];     // forward neighbor
                int rB = r + 2*d[0],   cB = c + 2*d[1];   // two steps forward
                // Pattern: S (here) O (next) S (two steps)
                if (isMoveInBounds(rA, cA) && isMoveInBounds(rB, cB)) {
                    if (getCell(rA, cA) == Move.O && getCell(rB, cB) == Move.S) total++;
                }

                int rA2 = r - d[0],    cA2 = c - d[1];    // backward neighbor
                int rB2 = r - 2*d[0],  cB2 = c - 2*d[1];  // two steps backward
                // Pattern: S (two back) O (back) S (here)
                if (isMoveInBounds(rA2, cA2) && isMoveInBounds(rB2, cB2)) {
                    if (getCell(rA2, cA2) == Move.O && getCell(rB2, cB2) == Move.S) total++;
                }
            }
        }

        return total;
    }

    // Convenience
    public boolean formsSOSIfPlaced(int r, int c, Move m) {
        return countSOSFromPlacement(r, c, m) > 0;
    }


}
