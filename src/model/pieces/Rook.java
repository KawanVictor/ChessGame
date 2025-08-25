package model.pieces;

import model.board.Board;
import model.board.Position;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    
    public Rook(Board board, boolean isWhite) {
        super(board, isWhite);
    }
    
    @Override
    public List<Position> getPossibleMoves() {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        
        for (int[] direction : directions) {
            int row = position.getRow();
            int col = position.getColumn();
            
            while (true) {
                row += direction[0];
                col += direction[1];
                Position newPos = new Position(row, col);
                
                if (!newPos.isValid()) break;
                
                Piece pieceAtPosition = board.getPieceAt(newPos);
                if (pieceAtPosition == null) {
                    moves.add(newPos);
                } else if (pieceAtPosition.isWhite() != isWhite) {
                    moves.add(newPos);
                    break;
                } else break;
            }
        }
        return moves;
    }
    
    @Override
    public String getSymbol() { return "R"; }
}
