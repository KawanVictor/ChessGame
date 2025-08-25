package model.pieces;

import model.board.Board;
import model.board.Position;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    
    public Pawn(Board board, boolean isWhite) {
        super(board, isWhite);
    }
    
    @Override
    public List<Position> getPossibleMoves() {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;
        
        // Movimento simples
        Position front = new Position(position.getRow() + direction, position.getColumn());
        if (front.isValid() && board.isPositionEmpty(front)) {
            moves.add(front);
            
            // Movimento duplo na primeira jogada
            if ((isWhite && position.getRow() == 6) || (!isWhite && position.getRow() == 1)) {
                Position doubleFront = new Position(position.getRow() + 2 * direction, position.getColumn());
                if (board.isPositionEmpty(doubleFront)) {
                    moves.add(doubleFront);
                }
            }
        }
        
        // Capturas diagonais
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
    public String getSymbol() { return "P"; }
}
