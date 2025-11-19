package sprint_3;


import sprint_3.Board;

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

    public abstract sprint_3.Move chooseMove(sprint_3.Board board);

}

class HumanPlayer extends sprint_3.Player {

    //constructor
    HumanPlayer(PlayerColor color, String name) {
        super(color, name);
    }

    @Override
    public sprint_3.Move chooseMove(Board board) {
        throw new UnsupportedOperationException(
                "GUI Error"
        );
    } // moves go from GUI
}
