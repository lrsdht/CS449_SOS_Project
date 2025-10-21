package sprint_2;

import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class controller {

    @FXML private ComboBox<String> boardSizeSelector;
    @FXML private GridPane boardGrid;
    @FXML private RadioButton S;
    @FXML private RadioButton O;
    @FXML private Button newGame;
    @FXML private RadioButton simpleMode;
    @FXML private RadioButton generalMode;
    @FXML private Label statusLabel;

    private ToggleGroup soRadioButtonGroup;
    private ToggleGroup modeGroup;
    private Board board;

    private enum Mode { SIMPLE, GENERAL }
    private Mode mode = Mode.SIMPLE;

    @FXML
    public void initialize() {
        boardSizeSelector.getItems().addAll("3","6", "9");
        boardSizeSelector.setOnAction(event -> updateStatus());

        soRadioButtonGroup = new ToggleGroup();
        S.setToggleGroup(soRadioButtonGroup);
        O.setToggleGroup(soRadioButtonGroup);
        S.setSelected(true);

        modeGroup = new ToggleGroup();
        simpleMode.setToggleGroup(modeGroup);
        generalMode.setToggleGroup(modeGroup);
        simpleMode.setSelected(true);

        S.setOnAction(event -> updateStatus());
        O.setOnAction(event -> updateStatus());
        simpleMode.setOnAction(event -> updateStatus());
        generalMode.setOnAction(event -> updateStatus());
    }

    public void getBoardSize(ActionEvent event) {
        String boardSizeSelectionThatIsMade = boardSizeSelector.getValue();
        if (boardSizeSelectionThatIsMade != null) {
            startNewGame(Integer.parseInt(boardSizeSelectionThatIsMade));
        }
    }

    @FXML
    private void whenNewGameStarted(ActionEvent event) {
        mode = simpleMode.isSelected() ? Mode.SIMPLE : Mode.GENERAL;
        String size = boardSizeSelector.getValue();
        if (size != null) {
            startNewGame(Integer.parseInt(size));
        } else {
            if (statusLabel != null) {
                statusLabel.setText("Please select a board size first.");
            }
        }
    }

    private void startNewGame(int boardSize) {
        board = new Board(boardSize);
        buildBoard();
        updateStatus();
    }

    private void buildBoard() {
        int boardSize = board.getSize();
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
                boardGrid.add(button, j, i);
            }
        }
    }

    private void updateStatus() {
        String size = (board != null) ? String.valueOf(board.getSize()) :
                (boardSizeSelector.getValue() != null ? boardSizeSelector.getValue() : "Not selected");

        String moveSelected = S.isSelected() ? "S" : "O";
        String modeStr = simpleMode.isSelected() ? "Simple" : "General";

        if (statusLabel != null) {
            statusLabel.setText("Board: " + size + "x" + size + " | Mode: " + modeStr + " | Move: " + moveSelected);
        }
    }

    private void onButtonClick(int row, int column, Button cellBtn) {
        if (board == null) {
            if (statusLabel != null) {
                statusLabel.setText("Press New Game to start.");
            }
            return;
        }

        // Move depending on radio selection
        Board.Move moveThatWantsToBeMade = (S.isSelected()) ? Board.Move.S : Board.Move.O;

        boolean placed = board.place(row, column, moveThatWantsToBeMade);
        if (placed) {
            cellBtn.setText(moveThatWantsToBeMade == Board.Move.S ? "S" : "O");
            if (statusLabel != null) {
                statusLabel.setText("Placed " + (moveThatWantsToBeMade == Board.Move.S ? "S" : "O") +
                        " at (" + row + "," + column + ")");
            }
        } else {
            if (statusLabel != null) {
                statusLabel.setText("Cell already occupied - pick another.");
            }
        }
    }
}