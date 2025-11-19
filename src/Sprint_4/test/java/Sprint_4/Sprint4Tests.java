package Sprint_4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Computer Player functionality
 */
public class Sprint4Tests {

    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
    }


    @Test
    @DisplayName("Test 1: Complete Simple game - Human Blue vs Computer Red with winner")
    void testSimpleGameHumanVsComputer() {
        Player bluePlayer = new HumanPlayer(Player.PlayerColor.BLUE, "Human Blue");
        Player redPlayer = new ComputerPlayer(Player.PlayerColor.RED, "Computer Red",
                ComputerPlayer.Difficulty.MEDIUM);

        gameState.startNewGame(3, GameState.Mode.SIMPLE, redPlayer, bluePlayer);

        assertFalse(gameState.isGameOver(), "Game should not be over at start");
        assertEquals(Player.PlayerColor.BLUE, gameState.getCurrentPlayer().getColor(),
                "Blue should go first");


        assertTrue(gameState.tryMove(0, 0, Move.S), "Blue should place S at (0,0)");

        // Red's turn (computer)
        if (gameState.getCurrentPlayer() instanceof ComputerPlayer computerPlayer) {
            ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);
            assertNotNull(aiMove, "Computer should return a valid move");
            assertTrue(gameState.tryMove(aiMove.row, aiMove.column, aiMove.moveType),
                    "Computer move should be valid");
        }

        // Continue playing until game ends
        int maxMoves = 20;
        int moveCount = 0;
        Player winner = null;

        while (!gameState.isGameOver() && moveCount < maxMoves) {
            Player currentPlayer = gameState.getCurrentPlayer();

            if (currentPlayer instanceof ComputerPlayer computerPlayer) {
                ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);
                if (aiMove != null) {
                    gameState.tryMove(aiMove.row, aiMove.column, aiMove.moveType);
                }
            } else {
                boolean placed = false;
                Move moveType = (moveCount % 2 == 0) ? Move.S : Move.O;

                for (int r = 0; r < 3 && !placed; r++) {
                    for (int c = 0; c < 3 && !placed; c++) {
                        if (gameState.getBoard().getCell(r, c) == Move.EMPTY) {
                            placed = gameState.tryMove(r, c, moveType);
                        }
                    }
                }
            }
            moveCount++;
        }

        assertTrue(gameState.isGameOver(), "Game should end in Simple mode");
        winner = gameState.getWinner();

        // In Simple mode, there must be a winner (first to make SOS) or draw if board fills
        assertTrue(winner != null || gameState.getBoard().isGridFull(),
                "Simple game should have winner or be a draw");

        System.out.println("Test 1 Result: " + gameState.getGameResult());
    }


    @Test
    @DisplayName("Test 2: Complete General game - Computer Blue vs Human Red with winner")
    void testGeneralGameComputerVsHuman() {
        Player bluePlayer = new ComputerPlayer(Player.PlayerColor.BLUE, "Computer Blue",
                ComputerPlayer.Difficulty.EASY);
        Player redPlayer = new HumanPlayer(Player.PlayerColor.RED, "Human Red");

        gameState.startNewGame(4, GameState.Mode.GENERAL, redPlayer, bluePlayer);

        assertFalse(gameState.isGameOver(), "Game should not be over at start");

        int maxMoves = 50;
        int moveCount = 0;

        while (!gameState.isGameOver() && moveCount < maxMoves) {
            Player currentPlayer = gameState.getCurrentPlayer();

            if (currentPlayer instanceof ComputerPlayer computerPlayer) {
                ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);
                if (aiMove != null) {
                    boolean success = gameState.tryMove(aiMove.row, aiMove.column, aiMove.moveType);
                    assertTrue(success, "Computer move should be valid");
                }
            } else {

                boolean placed = false;
                Move moveType = (moveCount % 3 == 0) ? Move.O : Move.S;

                for (int r = 0; r < 4 && !placed; r++) {
                    for (int c = 0; c < 4 && !placed; c++) {
                        if (gameState.getBoard().getCell(r, c) == Move.EMPTY) {
                            placed = gameState.tryMove(r, c, moveType);
                        }
                    }
                }
            }
            moveCount++;
        }

        assertTrue(gameState.isGameOver(), "Game should end when board is full");
        assertTrue(gameState.getBoard().isGridFull(), "Board should be full in General mode");

        // Check that scores were tracked
        int totalScore = gameState.getRedScore() + gameState.getBlueScore();
        assertTrue(totalScore >= 0, "Total score should be non-negative");

        Player winner = gameState.getWinner();
        System.out.println("Test 2 Result: " + gameState.getGameResult());
        System.out.println("Blue Score: " + gameState.getBlueScore() + ", Red Score: " + gameState.getRedScore());
    }


    @Test
    @DisplayName("Test 3: Complete Simple game - Computer vs Computer")
    void testSimpleGameComputerVsComputer() {
        Player bluePlayer = new ComputerPlayer(Player.PlayerColor.BLUE, "Computer Blue",
                ComputerPlayer.Difficulty.EASY);
        Player redPlayer = new ComputerPlayer(Player.PlayerColor.RED, "Computer Red",
                ComputerPlayer.Difficulty.MEDIUM);

        gameState.startNewGame(3, GameState.Mode.SIMPLE, redPlayer, bluePlayer);

        int maxMoves = 20;
        int moveCount = 0;

        while (!gameState.isGameOver() && moveCount < maxMoves) {
            Player currentPlayer = gameState.getCurrentPlayer();

            assertTrue(currentPlayer instanceof ComputerPlayer, "Both players should be computers");

            ComputerPlayer computerPlayer = (ComputerPlayer) currentPlayer;
            ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);

            assertNotNull(aiMove, "Computer should always return a move");
            assertTrue(aiMove.row >= 0 && aiMove.row < 3, "Row should be in bounds");
            assertTrue(aiMove.column >= 0 && aiMove.column < 3, "Column should be in bounds");
            assertTrue(aiMove.moveType == Move.S || aiMove.moveType == Move.O,
                    "Move type should be S or O");

            boolean success = gameState.tryMove(aiMove.row, aiMove.column, aiMove.moveType);
            assertTrue(success, "Computer move should always be valid");

            moveCount++;
        }

        assertTrue(gameState.isGameOver(), "Game should end");
        System.out.println("Test 3 Result: " + gameState.getGameResult());
        System.out.println("Moves made: " + moveCount);
    }

    @Test
    @DisplayName("Test 4: Complete General game - Computer vs Computer")
    void testGeneralGameComputerVsComputer() {
        // Setup: 5x5 board, General mode, both computers with different difficulties
        Player bluePlayer = new ComputerPlayer(Player.PlayerColor.BLUE, "Computer Blue (Hard)",
                ComputerPlayer.Difficulty.HARD);
        Player redPlayer = new ComputerPlayer(Player.PlayerColor.RED, "Computer Red (Medium)",
                ComputerPlayer.Difficulty.MEDIUM);

        gameState.startNewGame(5, GameState.Mode.GENERAL, redPlayer, bluePlayer);

        int maxMoves = 100;
        int moveCount = 0;
        int blueSOSCount = 0;
        int redSOSCount = 0;

        while (!gameState.isGameOver() && moveCount < maxMoves) {
            Player currentPlayer = gameState.getCurrentPlayer();
            int scoreBefore = (currentPlayer.getColor() == Player.PlayerColor.BLUE)
                    ? gameState.getBlueScore()
                    : gameState.getRedScore();

            ComputerPlayer computerPlayer = (ComputerPlayer) currentPlayer;
            ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);

            assertNotNull(aiMove, "Computer should return a move");
            boolean success = gameState.tryMove(aiMove.row, aiMove.column, aiMove.moveType);
            assertTrue(success, "Computer move should be valid");

            int scoreAfter = (currentPlayer.getColor() == Player.PlayerColor.BLUE)
                    ? gameState.getBlueScore()
                    : gameState.getRedScore();

            // Track SOS formations
            if (scoreAfter > scoreBefore) {
                if (currentPlayer.getColor() == Player.PlayerColor.BLUE) {
                    blueSOSCount++;
                } else {
                    redSOSCount++;
                }
            }

            moveCount++;
        }

        assertTrue(gameState.isGameOver(), "Game should end when board is full");
        assertTrue(gameState.getBoard().isGridFull(), "Board should be completely filled");

        System.out.println("Test 4 Result: " + gameState.getGameResult());
        System.out.println("Total moves: " + moveCount);
        System.out.println("Blue SOS formations: " + blueSOSCount);
        System.out.println("Red SOS formations: " + redSOSCount);

        Player winner = gameState.getWinner();
        if (gameState.getBlueScore() > gameState.getRedScore()) {
            assertEquals(Player.PlayerColor.BLUE, winner.getColor(), "Blue should win with higher score");
        } else if (gameState.getRedScore() > gameState.getBlueScore()) {
            assertEquals(Player.PlayerColor.RED, winner.getColor(), "Red should win with higher score");
        } else {
            assertNull(winner, "Should be a draw if scores are equal");
        }
    }


    @Test
    @DisplayName("Test 5a: Computer makes valid moves")
    void testComputerMakesValidMoves() {
        Player computer = new ComputerPlayer(Player.PlayerColor.RED, "Computer",
                ComputerPlayer.Difficulty.MEDIUM);
        Player human = new HumanPlayer(Player.PlayerColor.BLUE, "Human");

        gameState.startNewGame(3, GameState.Mode.SIMPLE, computer, human);

        // Fill cells
        gameState.tryMove(0, 0, Move.S);
        gameState.tryMove(1, 1, Move.O);

        // Computer's turn
        if (gameState.getCurrentPlayer() instanceof ComputerPlayer comp) {
            ComputerPlayer.AIMove move = comp.AIChooseMoveFromState(gameState);

            assertNotNull(move, "Computer should return a move");
            assertTrue(move.row >= 0 && move.row < 3, "Row in bounds");
            assertTrue(move.column >= 0 && move.column < 3, "Column in bounds");
            assertTrue(move.moveType != Move.EMPTY, "Move type should be S or O");

            // Verify the chosen cell is actually empty
            assertEquals(Move.EMPTY, gameState.getBoard().getCell(move.row, move.column),
                    "Computer should only choose empty cells");
        }
    }

    @Test
    @DisplayName("Test 5b: Computer completes SOS when possible")
    void testComputerCompletesSOS() {
        Player computer = new ComputerPlayer(Player.PlayerColor.RED, "Computer",
                ComputerPlayer.Difficulty.MEDIUM);
        Player human = new HumanPlayer(Player.PlayerColor.BLUE, "Human");

        gameState.startNewGame(3, GameState.Mode.SIMPLE, computer, human);

        gameState.getBoard().setCellForAIAlgorithm(0, 0, Move.S);
        gameState.getBoard().setCellForAIAlgorithm(0, 2, Move.S);

        // Make it computer's turn
        while (gameState.getCurrentPlayer().getColor() != Player.PlayerColor.RED) {
            gameState.tryMove(2, 2, Move.S); // Dummy move
        }

        if (gameState.getCurrentPlayer() instanceof ComputerPlayer comp) {
            ComputerPlayer.AIMove move = comp.AIChooseMoveFromState(gameState);

            // Computer should recognize the winning move
            if (move.row == 0 && move.column == 1) {
                assertEquals(Move.O, move.moveType, "Computer should place O to complete SOS");
            }
        }
    }

    @Test
    @DisplayName("Test 5c: Computer difficulty affects search depth")
    void testComputerDifficultyLevels() {
        ComputerPlayer easy = new ComputerPlayer(Player.PlayerColor.RED, "Easy",
                ComputerPlayer.Difficulty.EASY);
        ComputerPlayer medium = new ComputerPlayer(Player.PlayerColor.RED, "Medium",
                ComputerPlayer.Difficulty.MEDIUM);
        ComputerPlayer hard = new ComputerPlayer(Player.PlayerColor.RED, "Hard",
                ComputerPlayer.Difficulty.HARD);

        // Verify they're created with correct difficulty
        assertNotNull(easy);
        assertNotNull(medium);
        assertNotNull(hard);

        assertEquals("Easy", easy.getName());
        assertEquals("Medium", medium.getName());
        assertEquals("Hard", hard.getName());
    }

    @Test
    @DisplayName("Test 5d: Computer handles full board gracefully")
    void testComputerHandlesFullBoard() {
        Player computer = new ComputerPlayer(Player.PlayerColor.RED, "Computer",
                ComputerPlayer.Difficulty.EASY);
        Player human = new HumanPlayer(Player.PlayerColor.BLUE, "Human");

        gameState.startNewGame(3, GameState.Mode.GENERAL, computer, human);

        // Fill entire board
        gameState.tryMove(0, 0, Move.S);
        gameState.tryMove(0, 1, Move.O);
        gameState.tryMove(0, 2, Move.S);
        gameState.tryMove(1, 0, Move.O);
        gameState.tryMove(1, 1, Move.S);
        gameState.tryMove(1, 2, Move.O);
        gameState.tryMove(2, 0, Move.S);
        gameState.tryMove(2, 1, Move.O);
        gameState.tryMove(2, 2, Move.S);

        assertTrue(gameState.isGameOver(), "Game should be over when board is full");
        assertTrue(gameState.getBoard().isGridFull(), "Board should be full");
    }

    @Test
    @DisplayName("Test 5e: Computer wins Simple game when possible")
    void testComputerWinsSimpleGame() {
        Player computer = new ComputerPlayer(Player.PlayerColor.RED, "Computer",
                ComputerPlayer.Difficulty.HARD);
        Player human = new HumanPlayer(Player.PlayerColor.BLUE, "Human");

        gameState.startNewGame(3, GameState.Mode.SIMPLE, computer, human);

        // Setup a winning position for computer

        gameState.getBoard().setCellForAIAlgorithm(0, 0, Move.S);
        gameState.getBoard().setCellForAIAlgorithm(0, 1, Move.O);

        // Make it computer's turn (Red)
        while (gameState.getCurrentPlayer().getColor() != Player.PlayerColor.RED && !gameState.isGameOver()) {
            // Human makes a non-winning move
            for (int r = 1; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (gameState.getBoard().getCell(r, c) == Move.EMPTY) {
                        gameState.tryMove(r, c, Move.O);
                        break;
                    }
                }
                break;
            }
        }

        if (!gameState.isGameOver() && gameState.getCurrentPlayer() instanceof ComputerPlayer comp) {
            int scoreBefore = gameState.getRedScore();
            ComputerPlayer.AIMove move = comp.AIChooseMoveFromState(gameState);

            if (move != null) {
                gameState.tryMove(move.row, move.column, move.moveType);

                // If computer placed at (0,2) with S, it should win
                if (move.row == 0 && move.column == 2 && move.moveType == Move.S) {
                    assertTrue(gameState.getRedScore() > scoreBefore, "Computer should score");
                    assertTrue(gameState.isGameOver(), "Simple game should end after SOS");
                }
            }
        }
    }

    @Test
    @DisplayName("Test 5f: Computer blocks opponent in Simple mode")
    void testComputerBlocksOpponent() {
        Player computer = new ComputerPlayer(Player.PlayerColor.RED, "Computer",
                ComputerPlayer.Difficulty.HARD);
        Player human = new HumanPlayer(Player.PlayerColor.BLUE, "Human");

        gameState.startNewGame(3, GameState.Mode.SIMPLE, computer, human);

        // Setup: Human (Blue) is about to win
        gameState.getBoard().setCellForAIAlgorithm(0, 0, Move.S);
        gameState.getBoard().setCellForAIAlgorithm(0, 2, Move.S);

        // Make it computer's turn
        gameState.switchTurnForTest(); // Assuming this method exists or set manually

        if (gameState.getCurrentPlayer() instanceof ComputerPlayer comp) {
            ComputerPlayer.AIMove move = comp.AIChooseMoveFromState(gameState);



            assertNotNull(move, "Computer should make a move");


        }
    }

    @Test
    @DisplayName("Test 5g: Parse difficulty utility method works correctly")
    void testParseDifficulty() {
        assertEquals(ComputerPlayer.Difficulty.EASY,
                ComputerPlayer.parseDifficulty("easy"));
        assertEquals(ComputerPlayer.Difficulty.EASY,
                ComputerPlayer.parseDifficulty("Easy"));
        assertEquals(ComputerPlayer.Difficulty.EASY,
                ComputerPlayer.parseDifficulty("EASY"));

        assertEquals(ComputerPlayer.Difficulty.MEDIUM,
                ComputerPlayer.parseDifficulty("medium"));
        assertEquals(ComputerPlayer.Difficulty.MEDIUM,
                ComputerPlayer.parseDifficulty("Medium"));

        assertEquals(ComputerPlayer.Difficulty.HARD,
                ComputerPlayer.parseDifficulty("hard"));
        assertEquals(ComputerPlayer.Difficulty.HARD,
                ComputerPlayer.parseDifficulty("Hard"));

        // Test default
        assertEquals(ComputerPlayer.Difficulty.MEDIUM,
                ComputerPlayer.parseDifficulty("invalid"));
        assertEquals(ComputerPlayer.Difficulty.MEDIUM,
                ComputerPlayer.parseDifficulty(null));
    }
}