package model.pieces;

import java.util.List;
import model.board.Board;
import model.board.Position;

public class Queen extends Piece {

    public Queen(Board board, boolean isWhite) {
        super(board, isWhite);
    }

    @Override
    public List<Position> getPossibleMoves() {
        int[][] directions = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        return getSlidingMoves(directions);
    }

    @Override
    public String getSymbol() { return "Q"; }
}
