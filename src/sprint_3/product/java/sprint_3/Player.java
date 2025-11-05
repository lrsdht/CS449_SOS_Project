package sprint_3;


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
