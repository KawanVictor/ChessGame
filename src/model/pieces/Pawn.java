package model.pieces;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.board.Position;

public class Pawn extends Piece {

    public Pawn(Board board, boolean isWhite) {
        super(board, isWhite);
    }

    @Override
    public List<Position> getPossibleMoves() {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;

        Position front = new Position(position.getRow() + direction, position.getColumn());
        if (front.isValid() && board.isPositionEmpty(front)) {
            moves.add(front);

            if ((isWhite && position.getRow() == 6) || (!isWhite && position.getRow() == 1)) {
                Position doubleFront = new Position(position.getRow() + 2 * direction, position.getColumn());
                if (board.isPositionEmpty(doubleFront)) {
                    moves.add(doubleFront);
                }
            }
        }

        int[][] captures = {{direction, -1}, {direction, 1}};
        for (int[] cap : captures) {
            Position diag = new Position(position.getRow() + cap[0], position.getColumn() + cap[1]);
            if (diag.isValid()) {
                Piece pieceAt = board.getPieceAt(diag);
                if (pieceAt != null && pieceAt.isWhite() != isWhite) {
                    moves.add(diag);
                }
            }
        }
        return moves;
    }

    @Override
    public boolean attacks(Position target) {
        int direction = isWhite ? -1 : 1;
        return target.getRow() == position.getRow() + direction
                && Math.abs(target.getColumn() - position.getColumn()) == 1;
    }

    @Override
    public String getSymbol() { return "P"; }
}
