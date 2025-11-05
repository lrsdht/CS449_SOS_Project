package sprint_2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(3);
    }

    @AfterEach
    public void tearDown() {
        board = null;
    }

    // AC#1.1
    // CHATGPT
    @Test
    public void testNewBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(Board.Move.EMPTY, board.getCell(row, column),
                        "Cell at (" + row + "," + column + ") should be EMPTY");
            }
        }
    }

    // AC 1.1, 3.2, 5.5
    // CHATGPT
    @Test
    public void testInvalidRow() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            board.getCell(3, 0);
        }, "Accessing invalid row should throw exception");
    }

    // AC 1.2, 3.3, 5.2
    @Test
    public void testInvalidColumn() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            board.getCell(0, 3);
        }, "Accessing invalid column should throw exception");
    }

    // AC 3.1
    @Test
    public void testPlaceSOnEmptyCell() {
        boolean result = board.place(0, 0, Board.Move.S);
        assertTrue(result, "Should successfully place S on empty cell");
        assertEquals(Board.Move.S, board.getCell(0, 0), "Cell should contain S");
    }

    // AC 3.2
    @Test
    public void testPlaceOOnEmptyCell() {
        boolean result = board.place(1, 1, Board.Move.O);
        assertTrue(result, "Should successfully place O on empty cell");
        assertEquals(Board.Move.O, board.getCell(1, 1), "Cell should contain O");
    }

    // AC 2.2
    @Test
    public void testPlaceOnOccupiedCell() {
        board.place(0, 0, Board.Move.S);
        boolean result = board.place(0, 0, Board.Move.O);
        assertFalse(result, "Should not be able to place mark on occupied cell");
        assertEquals(Board.Move.S, board.getCell(0, 0), "Cell should still contain original S");
    }

    // AC 2.3
    @Test
    public void testPlaceOutOfBounds() {
        boolean result = board.place(5, 5, Board.Move.S);
        assertFalse(result, "Should not be able to place mark out of bounds");
    }

    // Other Tests (not AC)
    @Test
    public void testPlaceEmptyMark() {
        boolean result = board.place(0, 0, Board.Move.EMPTY);
        assertFalse(result, "Should not be able to place EMPTY mark");
        assertEquals(Board.Move.EMPTY, board.getCell(0, 0), "Cell should remain EMPTY");
    }

    // AC 1.2
    @Test
    public void testBoardSize() {
        assertEquals(3, board.getSize(), "Board size should be 3");
    }

    // AC 1.3
    @Test
    public void testDifferentBoardSizes() {
        Board board6 = new Board(6);
        assertEquals(6, board6.getSize(), "Board size should be 6");

        Board board9 = new Board(9);
        assertEquals(9, board9.getSize(), "Board size should be 9");
    }

    // AC 3.3
    @Test
    public void testInBoundsValidPositions() {
        assertTrue(board.whereIn(0, 0), "Position (0,0) should be in bounds");
        assertTrue(board.whereIn(2, 2), "Position (2,2) should be in bounds");
        assertTrue(board.whereIn(1, 1), "Position (1,1) should be in bounds");
    }

    // AC 3.4
    @Test
    public void testInBoundsInvalidPositions() {
        assertFalse(board.whereIn(-1, 0), "Negative row should be out of bounds");
        assertFalse(board.whereIn(0, -1), "Negative column should be out of bounds");
        assertFalse(board.whereIn(3, 0), "Row 3 should be out of bounds for 3x3 board");
        assertFalse(board.whereIn(0, 3), "Column 3 should be out of bounds for 3x3 board");
    }

    // AC 4.3
    @Test
    public void testMultiplePlacements() {
        assertTrue(board.place(0, 0, Board.Move.S), "First placement should succeed");
        assertTrue(board.place(0, 1, Board.Move.O), "Second placement should succeed");
        assertTrue(board.place(1, 0, Board.Move.S), "Third placement should succeed");

        assertEquals(Board.Move.S, board.getCell(0, 0), "Cell (0,0) should have S");
        assertEquals(Board.Move.O, board.getCell(0, 1), "Cell (0,1) should have O");
        assertEquals(Board.Move.S, board.getCell(1, 0), "Cell (1,0) should have S");
    }
}