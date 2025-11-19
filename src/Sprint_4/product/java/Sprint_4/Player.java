package Sprint_4;


abstract class Player {
    enum PlayerColor { BLUE, RED}

    PlayerColor color;
    String name;

    // constructor
    Player(PlayerColor color, String name) {
        this.color = color;
        this.name = name;
    };

    public PlayerColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public abstract Move chooseMove(Board board);

    public boolean isHuman(){
        return this instanceof HumanPlayer;
    }

    public static Player createPlayer(PlayerColor color, String baseName, boolean isHuman, String difficulty) {
        if (isHuman) {
            return new HumanPlayer(color, baseName);
        } else {
            ComputerPlayer.Difficulty diff = ComputerPlayer.parseDifficulty(difficulty);
            return new ComputerPlayer(color, "Computer (" + difficulty + ")", diff);
        }
    }
}

class HumanPlayer extends Player {

    //constructor
    HumanPlayer(PlayerColor color, String name) {
        super(color, name);
    }

    @Override
    public Move chooseMove(Board board) {
        throw new UnsupportedOperationException(
                "GUI Error"
        );
    } // moves go from GUI
}
