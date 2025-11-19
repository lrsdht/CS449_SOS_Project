package Sprint_4;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComputerPlayer extends Player {

    public enum Difficulty {EASY, MEDIUM, HARD}

    private final Difficulty difficulty;

    public ComputerPlayer(PlayerColor color, String name, Difficulty difficulty) {
        super(color, name);
        this.difficulty = difficulty;
    }

    public static Difficulty parseDifficulty(String difficultyString) {
        return switch (difficultyString.toLowerCase()) {
            case "easy" -> Difficulty.EASY;
            case "hard" -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };
    }

    @Override
    public Move chooseMove(Board board) {
        throw new UnsupportedOperationException("Computer player uses (function name) instead");
    }

    public AIMove AIChooseMoveFromState(GameState state) {
        int depth = getDepth(state.getBoard().getSize());

        GameNode rootNode = GameNode.fromGameState(state);
        boolean isMaximizingPlayer = (rootNode.currentTurn == this.getColor());

        MoveScore bestMove = performAlphaBetaSearch(rootNode, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizingPlayer);
        return new AIMove(bestMove.row, bestMove.column, bestMove.moveType);
    }

    private int getDepth(int boardSize) {
        return switch (difficulty) {
            case EASY -> 2;
            case MEDIUM -> 4;
            case HARD -> (boardSize < 7) ? 6 : 4;
        };
    }

    // minimax ai + alpha-beta pruning for performance
    private MoveScore performAlphaBetaSearch(GameNode node, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // base case: terminal node or depth limit reached
        if (depth == 0 || node.isGameOver()) {
            int score = node.evaluatePosition(this.getColor());
            return new MoveScore(score, -1, -1, Move.EMPTY);
        }

        List<MoveScore> possibleMoves = node.generateAllPossibleMoves();
        if (possibleMoves.isEmpty()) {
            int score = node.evaluatePosition(this.getColor());
            return new MoveScore(score, -1, -1, Move.EMPTY);
        }

        // reorder moves for pruning
        sortMoves(possibleMoves);

        if (isMaximizingPlayer) {
            return findMaximizingMove(node, depth, alpha, beta, possibleMoves);
        }
        else {
            return findMinimizingMove(node, depth, alpha, beta, possibleMoves);
        }
    }

    private MoveScore findMaximizingMove(GameNode node, int depth, int alpha, int beta, List<MoveScore> possibleMoves) {
        MoveScore bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (MoveScore move : possibleMoves) {
            GameNode childNode = node.applyMove(move, this.getColor());
            boolean nextIsMaximizing = (childNode.currentTurn == this.getColor());

            MoveScore result = performAlphaBetaSearch(childNode, depth - 1, alpha, beta, nextIsMaximizing);

            if (result.score > bestScore) {
                bestScore = result.score;
                bestMove = new MoveScore(bestScore, move.row, move.column, move.moveType);
            }

            alpha = Math.max(alpha, bestScore);
            if (beta <= alpha) {
                break;
            }
        }

        return bestMove != null ? bestMove : possibleMoves.get(0);
    }

    private MoveScore findMinimizingMove(GameNode node, int depth, int alpha, int beta, List<MoveScore> possibleMoves) {

        MoveScore bestMove = null;
        int bestScore = Integer.MAX_VALUE;

        for (MoveScore move : possibleMoves) {
            GameNode childNode = node.applyMove(move, this.getColor());
            boolean nextIsMaximizing = (childNode.currentTurn == this.getColor());

            MoveScore result = performAlphaBetaSearch(childNode, depth - 1, alpha, beta, nextIsMaximizing);

            if (result.score < bestScore) {
                bestScore = result.score;
                bestMove = new MoveScore(bestScore, move.row, move.column, move.moveType);
            }

            beta = Math.min(beta, bestScore);
            if (beta <= alpha) {
                break;
            }
        }

        return bestMove != null ? bestMove : possibleMoves.get(0);
    }

    private void sortMoves(List<MoveScore> moves) {
        java.util.Collections.shuffle(moves);
        moves.sort(Comparator.comparingInt(MoveScore::getValue).reversed());
    }

    private static class GameNode {
        final Board board;
        final GameState.Mode gameMode;
        final Player.PlayerColor currentTurn;
        final int redScore;
        final int blueScore;
        final boolean doesGameEndFromSOS;

        private GameNode(Board board, GameState.Mode gameMode, Player.PlayerColor currentTurn, int redScore, int blueScore, boolean doesGameEndFromSOS) {
            this.board = board;
            this.gameMode = gameMode;
            this.currentTurn = currentTurn;
            this.redScore = redScore;
            this.blueScore = blueScore;
            this.doesGameEndFromSOS = doesGameEndFromSOS;
        }

        static GameNode fromGameState(GameState state) {
            return new GameNode(
                    Board.AIBoardCopy(state.getBoard()),
                    state.getMode(),
                    state.getCurrentPlayer().getColor(),
                    state.getRedScore(),
                    state.getBlueScore(),
                    false
            );
        }

        boolean isGameOver() {
            if (gameMode == GameState.Mode.SIMPLE && doesGameEndFromSOS) {
                return true;
            }
            return board.isGridFull();
        }

        int evaluatePosition(Player.PlayerColor otherPlayerNextMove) {
            if (gameMode == GameState.Mode.GENERAL) {
                return evaluateGeneralMode(otherPlayerNextMove);
            }
            else {
                return evaluateSimpleMode(otherPlayerNextMove);
            }
        }

        private int evaluateGeneralMode(Player.PlayerColor otherPlayerNextMove) {
            int scoreDifference = calculateScoreDifference(otherPlayerNextMove);
            int potentialSOSCount = countPotentialSOSMoves();

            return (50 * scoreDifference) + (2 * potentialSOSCount);
        }

        private int evaluateSimpleMode(Player.PlayerColor otherPlayerNextMove) {
            if (doesGameEndFromSOS) {
                return evaluateSimpleGameEnd(otherPlayerNextMove);
            }

            int immediateWinningMoves = countImmediateWinningOpportunities();
            return 10 * immediateWinningMoves;
        }

        private int evaluateSimpleGameEnd(Player.PlayerColor otherPlayerNextMove) {
            if (redScore > blueScore) {
                return (otherPlayerNextMove == Player.PlayerColor.RED) ? 1000 : -1000;
            }
            else if (blueScore > redScore) {
                return (otherPlayerNextMove == Player.PlayerColor.BLUE) ? 1000 : -1000;
            }
            return 0;
        }

        private int calculateScoreDifference(Player.PlayerColor otherPlayerNextMove) {
            return (otherPlayerNextMove == Player.PlayerColor.RED)
                    ? (redScore - blueScore)
                    : (blueScore - redScore);
        }

        private int countPotentialSOSMoves() {
            int boardSize = board.getSize();
            int count = 0;

            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    if (board.getCell(row, column) != Move.EMPTY) {
                        continue;
                    }
                    if (wouldMoveCreateSOS(row, column, Move.O) > 0) {
                        count++;
                    }
                }
            }
            return count;
        }

        private int countImmediateWinningOpportunities() {
            int boardSize = board.getSize();
            int count = 0;

            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    if (board.getCell(row, column) != Move.EMPTY) {
                        continue;
                    }
                    count += wouldMoveCreateSOS(row, column, Move.O);
                    count += wouldMoveCreateSOS(row, column, Move.S);
                }
            }
            return count;
        }

        private int wouldMoveCreateSOS(int row, int column, Move moveType) {
            Move originalMove = board.getCell(row, column);
            board.setCellForAIAlgorithm(row, column, moveType);
            int sosCount = board.isThereAnSOSFrom(row, column);
            board.setCellForAIAlgorithm(row, column, originalMove);
            return sosCount;
        }

        List<MoveScore> generateAllPossibleMoves() {
            List<MoveScore> moves = new ArrayList<>();
            int boardSize = board.getSize();
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    if (board.getCell(row, column) != Move.EMPTY) {
                        continue;
                    }
                    int scoreIfMoveIsAnO = wouldMoveCreateSOS(row, column, Move.O);
                    int scoreIfMoveIsAnS = wouldMoveCreateSOS(row, column, Move.S);

                    moves.add(new MoveScore(scoreIfMoveIsAnO, row, column, Move.O));
                    moves.add(new MoveScore(scoreIfMoveIsAnS, row, column, Move.S));
                }
            }
            return moves;
        }

        GameNode applyMove(MoveScore move, Player.PlayerColor AIPlayerColor) {
            Board newBoard = Board.AIBoardCopy(this.board);
            newBoard.setCellForAIAlgorithm(move.row, move.column, move.moveType);

            int sosCreated = newBoard.isThereAnSOSFrom(move.row, move.column);

            int newRedScore = redScore;
            int newBlueScore = blueScore;
            Player.PlayerColor nextTurn;
            boolean doesNextEndGame = false;

            if (gameMode == GameState.Mode.GENERAL) {
                if (currentTurn == Player.PlayerColor.RED) {
                    newRedScore += sosCreated;
                }
                else {
                    newBlueScore += sosCreated;
                }

                nextTurn = (sosCreated > 0) ? currentTurn : getOpponentColor(currentTurn);
            }
            else {
                if (sosCreated > 0) {
                    if (currentTurn == Player.PlayerColor.RED) {
                        newRedScore = 1;
                    }
                    else {
                        newBlueScore = 1;
                    }
                    doesNextEndGame = true;
                    nextTurn = currentTurn;
                }
                else {
                    nextTurn = getOpponentColor(currentTurn);
                }
            }
            return new GameNode(newBoard, gameMode, nextTurn, newRedScore, newBlueScore, doesNextEndGame);
        }

        private  Player.PlayerColor getOpponentColor(Player.PlayerColor playerColor) {
            return (playerColor == Player.PlayerColor.RED)
                    ? Player.PlayerColor.BLUE
                    : Player.PlayerColor.RED;
        }
    }

    private static class MoveScore {
        final int score;
        final int row;
        final int column;
        final Move moveType;

        MoveScore(int score, int row, int column, Move moveType) {
            this.score = score;
            this.row = row;
            this.column = column;
            this.moveType = moveType;
        }

        int getValue() {
            return score * 10;
        }
    }

    public static class AIMove {
        public final int row;
        public final int column;
        public final Move moveType;

        public AIMove(int row, int column, Move moveType) {
            this.row = row;
            this.column = column;
            this.moveType = moveType;
        }
    }
}
