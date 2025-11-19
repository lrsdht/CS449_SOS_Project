package Sprint_4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static Sprint_4.Player.createPlayer;

public class Controller {

    @FXML
    private RadioButton blueHuman;
    @FXML
    private RadioButton blueComputer;
    @FXML
    private ComboBox<String> blueDifficulty;
    @FXML
    private RadioButton redHuman;
    @FXML
    private RadioButton redComputer;
    @FXML
    private ComboBox<String> redDifficulty;
    @FXML
    private ComboBox<String> boardSizeSelector;
    @FXML
    private GridPane boardGrid;
    @FXML
    private RadioButton redS;
    @FXML
    private RadioButton redO;
    @FXML
    private RadioButton blueS;
    @FXML
    private RadioButton blueO;
    @FXML
    private Button newGame;
    @FXML
    private Button[][] cellButtons;
    @FXML
    private RadioButton simpleMode;
    @FXML
    private RadioButton generalMode;
    @FXML
    private Label statusLabel;
    @FXML
    private Pane overlay;
    @FXML
    private ToggleGroup redGroup;
    @FXML
    private ToggleGroup blueGroup;
    @FXML
    private ToggleGroup modeGroup;
    private GameState gameState;


    @FXML
    public void initialize() {
        boardSizeSelector.getItems().addAll("3", "6", "9");
        boardSizeSelector.setValue("3");

        blueDifficulty.getItems().addAll("Easy", "Medium", "Hard");
        blueDifficulty.setValue("Medium");
        redDifficulty.getItems().addAll("Easy", "Medium", "Hard");
        redDifficulty.setValue("Medium");

        blueComputer.selectedProperty().addListener((obs, oldVal, newVal) -> {
            blueDifficulty.setVisible(newVal);
        });
        redComputer.selectedProperty().addListener((obs, oldVal, newVal) -> {
            redDifficulty.setVisible(newVal);
        });

        redS.setSelected(true);
        blueS.setSelected(true);
        simpleMode.setSelected(true);

        if (redS.getToggleGroup() != null) {
            redS.getToggleGroup().selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateStatus());
        }
        if (blueS.getToggleGroup() != null) {
            blueS.getToggleGroup().selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateStatus());
        }
        if (simpleMode.getToggleGroup() != null) {
            simpleMode.getToggleGroup().selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateStatus());
        }

        updateStatus();
    }

    @FXML
    private void NewGameButtonCode(ActionEvent event) {
        int size = Integer.parseInt(boardSizeSelector.getValue());

        GameState.Mode mode;
        if (simpleMode.isSelected()) {
            mode = GameState.Mode.SIMPLE;
        } else {
            mode = GameState.Mode.GENERAL;
        }

        Player redPlayer = createPlayer(Player.PlayerColor.RED, "Red", redHuman.isSelected(), redDifficulty.getValue());
        Player bluePlayer = createPlayer(Player.PlayerColor.BLUE, "Blue", blueHuman.isSelected(), blueDifficulty.getValue());

        if (gameState == null) {
            gameState = new GameState();
        }
        gameState.startNewGame(size, mode, redPlayer, bluePlayer);

        buildBoard();
        overlay.getChildren().clear();
        updateStatus();
    } // wired to newGame button

    void buildBoard() {
        int boardSize = gameState.getBoard().getSize();

        cellButtons = new Button[boardSize][boardSize];

        boardGrid.getChildren().clear();
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        // Calculate cell size based on fixed 300x300 grid
        double cellSize = 300.0 / boardSize;

        // Create columns
        for (int i = 0; i < boardSize; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(cellSize);
            column.setMinWidth(cellSize);
            column.setMaxWidth(cellSize);
            boardGrid.getColumnConstraints().add(column);
        }

        // Create Rows
        for (int i = 0; i < boardSize; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(cellSize);
            row.setMinHeight(cellSize);
            row.setMaxHeight(cellSize);
            boardGrid.getRowConstraints().add(row);
        }

        // Add buttons to grid
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Button button = new Button("");
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.setMinSize(cellSize, cellSize);
                button.setPrefSize(cellSize, cellSize);
                button.setStyle("-fx-font-size: " + (cellSize / 3) + "px; -fx-font-weight: bold;");
                final int row = i, column = j;
                button.setOnAction(event -> onButtonClick(row, column, button));

                cellButtons[i][j] = button;
                boardGrid.add(button, j, i);
            }
        }

        updatePlayerControlsState();

        javafx.application.Platform.runLater(this::triggerComputerTurnIfNeeded);
    }

    private void updateStatus() {
            if (gameState == null || gameState.getBoard() == null) {
                statusLabel.setText("Select size and start a new game");
                return;
            }

            if (gameState.isGameOver()) {
                statusLabel.setText(gameState.getGameResult());
                return;
            }

            int size = gameState.getBoard().getSize();
            GameState.Mode mode = gameState.getMode();
            Player currentPlayer = gameState.getCurrentPlayer();
            Player.PlayerColor currentColor = currentPlayer.getColor();

            String currentMove;
            if (currentColor == Player.PlayerColor.RED) {
                currentMove = redS.isSelected() ? "S" : "O";
            } else {
                currentMove = blueS.isSelected() ? "S" : "O";
            }

            String modeStr = (mode == GameState.Mode.SIMPLE) ? "Simple" : "General";
            String colorStr = (currentColor == Player.PlayerColor.RED) ? "Red" : "Blue";

            String message = String.format("Board: %dx%d | Mode: %s | Turn: %s | %s Move: %s | Red: %d | Blue: %d",
                size, size, modeStr, colorStr, colorStr, currentMove,
                gameState.getRedScore(), gameState.getBlueScore());

            statusLabel.setText(message);
        }

    private void onButtonClick(int row, int column, Button cellBtn) {
        if (gameState == null || gameState.getBoard() == null) {
            statusLabel.setText("Press New Game to start");
            return;
        }

        if (gameState.isGameOver()) {
            statusLabel.setText(gameState.getGameResult() + " ==> Start a new game!");
            return;
        }

        Player currentPlayer = gameState.getCurrentPlayer();

        if (currentPlayer instanceof ComputerPlayer) {
            return;
        }

        // choose which move based on current player's color
        Move chosenMove;
        if (currentPlayer.getColor() == Player.PlayerColor.RED) {
            chosenMove = redS.isSelected() ? Move.S : Move.O;
        } else {
            chosenMove = blueS.isSelected() ? Move.S : Move.O;
        }

        if (!gameState.tryMove(row, column, chosenMove)) {
            statusLabel.setText("Cell occupied or invalid move");
            return;
        }

        if (chosenMove == Move.S) {
            cellBtn.setText("S");
        } else {
            cellBtn.setText("O");
        }
        drawSegmentsFor(row, column);
        updateStatus();

        triggerComputerTurnIfNeeded();
    }

    private void drawSegmentsFor(int row, int column) {
        int[][] directions = { {0,1}, {1,0}, {1,1}, {1,-1} };

        for (int[] d : directions) {
            int dr = d[0], dc = d[1];

            int r1 = row - dr, c1 = column - dc, r2 = row + dr, c2 = column + dc;
            if (inBounds(r1,c1) && inBounds(r2,c2)
                    && getCell(r1,c1) == Move.S && getCell(row,column) == Move.O && getCell(r2,c2) == Move.S) {
                drawSegment(r1,c1,r2,c2);
            }

            int rC = row + dr, cC = column + dc, rE = row + 2*dr, cE = column + 2*dc;
            if (inBounds(rC,cC) && inBounds(rE,cE)
                    && getCell(row,column) == Move.S && getCell(rC,cC) == Move.O && getCell(rE,cE) == Move.S) {
                drawSegment(row,column,rE,cE);
            }

            int rB = row - 2*dr, cB = column - 2*dc, rM = row - dr, cM = column - dc;
            if (inBounds(rB,cB) && inBounds(rM,cM)
                    && getCell(rB,cB) == Move.S && getCell(rM,cM) == Move.O && getCell(row,column) == Move.S) {
                drawSegment(rB,cB,row,column);
            }
        }
    }

    private void drawSegment(int row1, int column1, int row2, int column2) {
        int boardSize = gameState.getBoard().getSize();
        double cell = 300.0 / boardSize;
        double x1 = (column1 + 0.5) * cell;
        double y1 = (row1 + 0.5) * cell;
        double x2 = (column2 + 0.5) * cell;
        double y2 = (row2 + 0.5) * cell;

        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(3.0);

        // color by current player who just played
        Player.PlayerColor color = gameState.getCurrentPlayer().getColor();
        line.setStroke(color == Player.PlayerColor.RED ? Color.RED : Color.BLUE);

        overlay.getChildren().add(line);
    }

    private boolean inBounds(int r, int c) {
        return gameState.getBoard().isMoveInBounds(r, c);
    }

    private Move getCell(int r, int c) {
        return gameState.getBoard().getCell(r, c);
    }

    private void triggerComputerTurnIfNeeded() {
        if (gameState == null || gameState.isGameOver()) {
            return;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer computerPlayer) {
            executeComputerMove(computerPlayer);
        }
        else {
            enableAllButtons(true);
        }
    }

    private void executeComputerMove(ComputerPlayer computerPlayer) {
        enableAllButtons(false);
        statusLabel.setText(computerPlayer.getName() + " is thinking...");

        new Thread(() -> {
            try {
                Thread.sleep(300);

                ComputerPlayer.AIMove aiMove = computerPlayer.AIChooseMoveFromState(gameState);

                javafx.application.Platform.runLater(() -> {
                    if (aiMove != null && gameState.tryMove(aiMove.row,  aiMove.column, aiMove.moveType)) {
                        cellButtons[aiMove.row][aiMove.column].setText(
                                aiMove.moveType == Move.S ? "S" : "O"
                        );
                        drawSegmentsFor(aiMove.row, aiMove.column);

                        updateStatus();
                        enableAllButtons(true);

                        triggerComputerTurnIfNeeded();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void enableAllButtons(boolean enableButtons) {
        if (cellButtons == null) {
            return;
        }

        for (Button[] row: cellButtons) {
            for (Button button: row) {
                if (button != null) {
                    button.setDisable(!enableButtons);
                }
            }
        }
    }

    private void updatePlayerControlsState() {
        if (gameState == null) return;

        Player redPlayer = gameState.redPlayer;
        Player bluePlayer = gameState.bluePlayer;

        // Disable/enable Red player's S/O buttons
        boolean redIsHuman = redPlayer != null && redPlayer.isHuman();
        redS.setDisable(!redIsHuman);
        redO.setDisable(!redIsHuman);

        // Disable/enable Blue player's S/O buttons
        boolean blueIsHuman = bluePlayer != null && bluePlayer.isHuman();
        blueS.setDisable(!blueIsHuman);
        blueO.setDisable(!blueIsHuman);
    }
}