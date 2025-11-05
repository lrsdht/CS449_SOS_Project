package sprint_3;

public class GameState {
    enum Mode { SIMPLE, GENERAL }

    Board board;
    Player redPlayer;
    Player bluePlayer;
    Player current;
    Mode mode;

    int redScore;
    int blueScore;

    private boolean gameOver;

    // constructor
    GameState() {};

    public void startNewGame(int boardSize, Mode mode, Player redPlayer, Player bluePlayer) {
        board = new Board(boardSize);
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.mode = mode;
        this.current = bluePlayer; // blue player starts by defualt bc GO DODGERS lol
        this.redScore = 0;
        this.blueScore = 0;
        this.gameOver = false;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean tryMove(int row, int column, Move move) {

        if (gameOver) {
            return false;
        }

        if (!board.place(row, column, move)) {
            return false;
        }

        int updateScores = board.isThereAnSOSFrom(row, column);

        if (mode == Mode.SIMPLE) {
            if (updateScores > 0) {
                if (current.getColor() == Player.PlayerColor.RED) {
                    redScore += updateScores;
                } else {
                    blueScore += updateScores;
                }
                gameOver = true;
                return true;
            }
            if (board.isGridFull()) {
                gameOver = true;       // draw (no winner)
                return true;
            }
            switchTurn();
            return true;
        } else if (mode == Mode.GENERAL) {
            if (current.getColor() == Player.PlayerColor.RED) {
                redScore += updateScores;
            } else {
                blueScore += updateScores;
            }

            if (board.isGridFull()) {
                gameOver = true;
                return true;
            }

            if (updateScores == 0) {
                switchTurn();
            }
            return true;
        }

        switchTurn();

        return true;
    }

    private void switchTurn() {
        if (current == bluePlayer) {
            current = redPlayer;
        } else {
            current = bluePlayer;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        if (!gameOver) {
            return null;
        }

        if (redScore > blueScore) {
            return redPlayer;
        } else if (blueScore > redScore) {
            return bluePlayer;
        } else {
            return null; // draw
        }
    }

    public String getGameResult() {
        if (!gameOver) {
            return "Game in progress";
        }

        Player winner = getWinner();
        if (winner == null) {
            return "Game Over - It's a Draw Red: " + redScore + " Blue: " + blueScore;
        } else {
            return "Game Over - Winner is " + winner.getName() + "! Red: " + redScore + " Blue: " + blueScore;
        }
    }
}
