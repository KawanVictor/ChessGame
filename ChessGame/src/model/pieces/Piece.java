package model.pieces;

import model.board.Board;
import model.board.Position;
import java.util.List;

public abstract class Piece {
    protected Position position;
    protected boolean isWhite;
    protected Board board;
    
    public Piece(Board board, boolean isWhite) {
        this.board = board;
        this.isWhite = isWhite;
    }
    
    public boolean isWhite() { return isWhite; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    
    public abstract List<Position> getPossibleMoves();
    
    public boolean canMoveTo(Position position) {
        List<Position> possibleMoves = getPossibleMoves();
        return possibleMoves.contains(position);
    }
    
    public abstract String getSymbol();
}
