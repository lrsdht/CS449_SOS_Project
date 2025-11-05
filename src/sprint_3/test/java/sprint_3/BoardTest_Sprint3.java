package sprint_3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest_Sprint3 {

    private Board b3;

    @BeforeEach
    void init() {
        b3 = new Board(3);
    }

    @Test
    void placingOnOccupiedCellRejected() {
        assertTrue(b3.place(0,0, Move.S));
        assertFalse(b3.place(0,0, Move.O));
        assertEquals(Move.S, b3.getCell(0,0));
    }

    @Test
    void outOfBoundsPlacementRejected() {
        assertFalse(b3.place(-1,0, Move.S));
        assertFalse(b3.place(0,-1, Move.O));
        assertFalse(b3.place(3,0, Move.S));
        assertFalse(b3.place(0,3, Move.O));
    }

    @Test
    void gridFullDetection() {
        for (int r = 0; r < b3.getSize(); r++) {
            for (int c = 0; c < b3.getSize(); c++) {
                assertTrue(b3.place(r, c, Move.S));
            }
        }
        assertTrue(b3.isGridFull());
    }

    @Test
    void inBoundsAPI_existsAndWorks() {
        assertTrue(b3.isMoveInBounds(0,0));
        assertTrue(b3.isMoveInBounds(2,2));
        assertFalse(b3.isMoveInBounds(3,0));
        assertFalse(b3.isMoveInBounds(-1,0));
    }
}
