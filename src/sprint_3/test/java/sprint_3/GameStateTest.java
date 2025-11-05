package sprint_3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest_Sprint3 {

    private GameState simple3x3;
    private GameState general5x5;

    @BeforeEach
    void setUp() {
        simple3x3 = new GameState();
        simple3x3.startNewGame(
                3,
                GameState.Mode.SIMPLE,
                new HumanPlayer(Player.PlayerColor.RED, "Red"),
                new HumanPlayer(Player.PlayerColor.BLUE, "Blue")
        );

        general5x5 = new GameState();
        general5x5.startNewGame(
                5,
                GameState.Mode.GENERAL,
                new HumanPlayer(Player.PlayerColor.RED, "Red"),
                new HumanPlayer(Player.PlayerColor.BLUE, "Blue")
        );
    }

    // SIMPLE: valid move, no SOS -> letter appears & turn switches (AC 4.1)
    @Test
    void simple_validMove_noSOS_switchesTurn() {
        var start = simple3x3.getCurrentPlayer().getColor();
        assertTrue(simple3x3.tryMove(0, 0, Move.S));
        assertEquals(Move.S, simple3x3.getBoard().getCell(0, 0));
        assertNotEquals(start, simple3x3.getCurrentPlayer().getColor());
    }

    // SIMPLE: making SOS ends game immediately (AC 4.2, 5.1)
    @Test
    void simple_moveCompletesSOS_gameEnds() {
        simple3x3.tryMove(0, 0, Move.S);
        simple3x3.tryMove(1, 1, Move.S);
        simple3x3.tryMove(0, 1, Move.O);
        simple3x3.tryMove(2, 2, Move.S);

        assertTrue(simple3x3.tryMove(0, 2, Move.S)); // completes SOS
        assertTrue(simple3x3.isGameOver());
    }

    // SIMPLE: full board with no SOS => draw (AC 5.2/5.3)
    @Test
    void simple_fullBoardNoSOS_drawAndGameOver() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertFalse(simple3x3.isGameOver(), "Game should not be over until the last cell.");
                assertTrue(simple3x3.tryMove(r, c, Move.S), "Every placement of S should succeed.");
            }
        }
        assertTrue(simple3x3.getBoard().isGridFull());
        assertTrue(simple3x3.isGameOver(), "SIMPLE should end as a draw on a full board with no SOS.");
    }

    // GENERAL: valid move, no SOS -> turn switches (AC 6.1)
    @Test
    void general_validMove_noSOS_switchesTurn() {
        var start = general5x5.getCurrentPlayer().getColor();
        assertTrue(general5x5.tryMove(2, 2, Move.O));
        assertEquals(Move.O, general5x5.getBoard().getCell(2, 2));
        assertNotEquals(start, general5x5.getCurrentPlayer().getColor());
    }

    // GENERAL: SOS -> +score and extra turn; chain ends when no SOS (AC 6.2, 6.4, 6.7)
    @Test
    void general_SOS_scoresAndGrantsExtraTurn_thenEndsOnNoSOS() {
        general5x5.tryMove(2, 1, Move.S);
        general5x5.tryMove(4, 4, Move.S);
        var scorer = general5x5.getCurrentPlayer().getColor();
        general5x5.tryMove(2, 3, Move.S);
        general5x5.tryMove(0, 0, Move.S);

        assertTrue(general5x5.tryMove(2, 2, Move.O));
        int red = general5x5.getRedScore();
        int blue = general5x5.getBlueScore();
        if (scorer == Player.PlayerColor.RED) assertTrue(red >= 1);
        else assertTrue(blue >= 1);
        assertEquals(scorer, general5x5.getCurrentPlayer().getColor(), "extra turn keeps playing");

        assertTrue(general5x5.tryMove(1, 1, Move.S)); // no SOS now
        assertNotEquals(scorer, general5x5.getCurrentPlayer().getColor(), "turn ended");
    }

    // GENERAL: multi-SOS in one move all counted (AC 6.3)
    @Test
    void general_multipleSOSInOneMove_allCounted() {
        GameState g = new GameState();
        g.startNewGame(5, GameState.Mode.GENERAL,
                new HumanPlayer(Player.PlayerColor.RED, "Red"),
                new HumanPlayer(Player.PlayerColor.BLUE, "Blue"));


        g.tryMove(2,1, Move.S);
        g.tryMove(0,0, Move.S);
        g.tryMove(2,3, Move.S);
        g.tryMove(0,1, Move.O);
        g.tryMove(1,2, Move.S);
        g.tryMove(0,2, Move.O);
        g.tryMove(3,2, Move.S);

        int before = g.getRedScore() + g.getBlueScore();
        assertTrue(g.tryMove(2,2, Move.O));
        int after  = g.getRedScore() + g.getBlueScore();

        assertTrue(after - before >= 2, "Center O should produce more than or equal 2 SOS total.");
    }

    // GENERAL: ends when full; winner by score; reject post-game moves (AC 7.1–7.5)
    @Test
    void general_fullBoard_ends_winnerByScore_noFurtherMoves() {
        GameState g = new GameState();
        g.startNewGame(3, GameState.Mode.GENERAL,
                new HumanPlayer(Player.PlayerColor.RED, "Red"),
                new HumanPlayer(Player.PlayerColor.BLUE, "Blue"));

        g.tryMove(0,0, Move.S);
        g.tryMove(1,1, Move.S);
        g.tryMove(0,2, Move.S);
        g.tryMove(2,2, Move.S);

        int redBefore  = g.getRedScore();
        int blueBefore = g.getBlueScore();

        assertTrue(g.tryMove(0,1, Move.O)); // someone scores exactly 1

        // who scored
        int redDelta  = g.getRedScore()  - redBefore;
        int blueDelta = g.getBlueScore() - blueBefore;
        assertEquals(1, redDelta + blueDelta, "Exactly one SOS should be counted.");

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (g.getBoard().getCell(r,c) == Move.EMPTY && !g.isGameOver()) {
                    assertTrue(g.tryMove(r, c, Move.S));
                }
            }
        }

        assertTrue(g.getBoard().isGridFull());
        assertTrue(g.isGameOver());

        if (redDelta == 1) {
            assertTrue(g.getRedScore() > g.getBlueScore(), "Scoring side should lead.");
        } else {
            assertTrue(g.getBlueScore() > g.getRedScore(), "Scoring side should lead.");
        }

        int rBeforeEnd = g.getRedScore(), bBeforeEnd = g.getBlueScore();
        assertFalse(g.tryMove(2,2, Move.S), "No moves after game over.");
        assertEquals(rBeforeEnd, g.getRedScore());
        assertEquals(bBeforeEnd, g.getBlueScore());
    }

}
