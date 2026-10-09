package model.board;

import model.pieces.Piece;

public class Move {
    private Position from;
    private Position to;
    private Piece piece;
    private Piece capturedPiece;
    private Position capturedPosition;
    private boolean isPromotion;
    private String promotionSymbol;
    private boolean isCastling;
    private Position rookFrom;
    private Position rookTo;
    private boolean isEnPassant;

    public Move(Position from, Position to, Piece piece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.capturedPosition = to;
        this.isPromotion = false;
        this.isCastling = false;
        this.isEnPassant = false;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getPiece() { return piece; }
    public Piece getCapturedPiece() { return capturedPiece; }

    // Casa da peça capturada (diferente do destino apenas no en passant)
    public Position getCapturedPosition() { return capturedPosition; }

    public boolean isPromotion() { return isPromotion; }
    public void setPromotion(boolean promotion) { isPromotion = promotion; }
    public void setPromotionSymbol(String promotionSymbol) { this.promotionSymbol = promotionSymbol; }

    public boolean isCastling() { return isCastling; }
    public void setCastling(Position rookFrom, Position rookTo) {
        isCastling = true;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
    }
    public Position getRookFrom() { return rookFrom; }
    public Position getRookTo() { return rookTo; }

    public boolean isEnPassant() { return isEnPassant; }
    public void setEnPassant(Position capturedPosition) {
        isEnPassant = true;
        this.capturedPosition = capturedPosition;
    }

    @Override
    public String toString() {
        if (isCastling) return to.getColumn() > from.getColumn() ? "O-O" : "O-O-O";
        String text = piece.getSymbol() + from.toString() + (capturedPiece != null ? "x" : "-") + to.toString();
        if (promotionSymbol != null) text += "=" + promotionSymbol;
        return text;
    }
}
