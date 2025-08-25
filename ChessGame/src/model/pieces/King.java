package model.pieces;

import model.board.Board;
import model.board.Position;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    
    public King(Board board, boolean isWhite) {
        super(board, isWhite);
    }
    
    @Override
    public List<Position> getPossibleMoves() {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] dir : directions) {
            Position newPos = new Position(position.getRow() + dir[0], position.getColumn() + dir[1]);
            if (newPos.isValid()) {
                Piece pieceAt = board.getPieceAt(newPos);
                if (pieceAt == null || pieceAt.isWhite() != isWhite) {
                    moves.add(newPos);
                }
            }
        }
        return moves;
    }
    
    @Override
    public String getSymbol() { return "K"; }
}
