package Objects;

public abstract class Move {

    int row, col;

    public Move(int row, int col){
        this.row = row;
        this.col = col;
    }

    public abstract boolean isValid(Board b, String direction);
}