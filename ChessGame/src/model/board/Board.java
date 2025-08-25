package model.board;

import model.pieces.Piece;

public class Board {
    private Piece[][] pieces;
    
    public Board() {
        pieces = new Piece[8][8];
    }
    
    public Piece getPieceAt(Position position) {
        if (!position.isValid()) return null;
        return pieces[position.getRow()][position.getColumn()];
    }
    
    public void placePiece(Piece piece, Position position) {
        if (!position.isValid()) return;
        pieces[position.getRow()][position.getColumn()] = piece;
        if (piece != null) piece.setPosition(position);
    }
    
    public void removePiece(Position position) {
        if (!position.isValid()) return;
        pieces[position.getRow()][position.getColumn()] = null;
    }
    
    public boolean isPositionEmpty(Position position) {
        return getPieceAt(position) == null;
    }
    
    public void clear() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieces[row][col] = null;
            }
        }
    }
    
    public boolean isUnderAttack(Position position, boolean byWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.isWhite() == byWhite) {
                    if (piece.canMoveTo(position)) return true;
                }
            }
        }
        return false;
    }
}
