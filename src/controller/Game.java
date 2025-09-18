package controller;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.board.Move;
import model.board.Position;
import model.pieces.*;

public class Game {
    private Board board;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private Piece selectedPiece;
    private List<Move> moveHistory;

    // Controle de promoção
    private boolean pendingPromotion = false;
    private Position pendingPromotionPosition = null;

    public Game() {
        board = new Board();
        isWhiteTurn = true;
        isGameOver = false;
        moveHistory = new ArrayList<>();
        setupPieces();
    }

    private void setupPieces() {
        board.placePiece(new Rook(board, true), new Position(7, 0));
        board.placePiece(new Knight(board, true), new Position(7, 1));
        board.placePiece(new Bishop(board, true), new Position(7, 2));
        board.placePiece(new Queen(board, true), new Position(7, 3));
        board.placePiece(new King(board, true), new Position(7, 4));
        board.placePiece(new Bishop(board, true), new Position(7, 5));
        board.placePiece(new Knight(board, true), new Position(7, 6));
        board.placePiece(new Rook(board, true), new Position(7, 7));
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Pawn(board, true), new Position(6, col));
        }
        board.placePiece(new Rook(board, false), new Position(0, 0));
        board.placePiece(new Knight(board, false), new Position(0, 1));
        board.placePiece(new Bishop(board, false), new Position(0, 2));
        board.placePiece(new Queen(board, false), new Position(0, 3));
        board.placePiece(new King(board, false), new Position(0, 4));
        board.placePiece(new Bishop(board, false), new Position(0, 5));
        board.placePiece(new Knight(board, false), new Position(0, 6));
        board.placePiece(new Rook(board, false), new Position(0, 7));
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Pawn(board, false), new Position(1, col));
        }
    }

    public Board getBoard() { return board; }
    public boolean isWhiteTurn() { return isWhiteTurn; }
    public boolean isGameOver() { return isGameOver; }
    public Piece getSelectedPiece() { return selectedPiece; }
    public List<Move> getMoveHistory() { return moveHistory; }

    // Métodos para promoção personalizada
    public boolean hasPendingPromotion() { return pendingPromotion; }
    public Position getPendingPromotionPosition() { return pendingPromotionPosition; }

    public void promotePawnAt(Position pos, String escolha) {
        Piece oldPawn = board.getPieceAt(pos);
        Piece newPiece = null;
        boolean white = oldPawn.isWhite();
        switch (escolha.toLowerCase()) {
            case "rainha": newPiece = new Queen(board, white); break;
            case "torre": newPiece = new Rook(board, white); break;
            case "bispo": newPiece = new Bishop(board, white); break;
            case "cavalo": newPiece = new Knight(board, white); break;
            default: newPiece = new Queen(board, white);
        }
        board.placePiece(newPiece, pos);
        pendingPromotion = false;
        pendingPromotionPosition = null;
    }

    public void selectPiece(Position position) {
        Piece piece = board.getPieceAt(position);
        if (piece != null && piece.isWhite() == isWhiteTurn) {
            selectedPiece = piece;
        } else {
            selectedPiece = null;
        }
    }

    public boolean movePiece(Position destination) {
        if (selectedPiece == null || isGameOver) return false;
        if (!selectedPiece.canMoveTo(destination)) return false;
        if (moveCausesCheck(selectedPiece, destination)) return false;

        Piece capturedPiece = board.getPieceAt(destination);
        Position originalPosition = selectedPiece.getPosition();

        board.removePiece(originalPosition);
        board.placePiece(selectedPiece, destination);

        Move move = new Move(originalPosition, destination, selectedPiece, capturedPiece);
        moveHistory.add(move);

        // PROMOÇÃO de peão (detecção)
        if (selectedPiece instanceof Pawn) {
            int lastRow = selectedPiece.isWhite() ? 0 : 7;
            if (destination.getRow() == lastRow) {
                pendingPromotion = true;
                pendingPromotionPosition = destination;
            } else {
                pendingPromotion = false;
                pendingPromotionPosition = null;
            }
        } else {
            pendingPromotion = false;
            pendingPromotionPosition = null;
        }

        checkSpecialConditions(selectedPiece, destination);
        checkGameStatus();

        isWhiteTurn = !isWhiteTurn;
        selectedPiece = null;
        return true;
    }

    public boolean moveCausesCheck(Piece piece, Position destination) {
        // Simula movimento e verifica se deixa o próprio rei em xeque (implemente conforme desejar)
        return false;
    }

    public boolean isInCheck(boolean forWhite) {
        // Encontra o rei da cor especificada
        Position kingPos = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == forWhite && piece.getSymbol().equals("K")) {
                    kingPos = piece.getPosition();
                    break;
                }
            }
            if (kingPos != null) break;
        }
        if (kingPos == null) return false;
        // Verifica se o rei está sob ataque
        return board.isUnderAttack(kingPos, !forWhite);
    }

    private void checkSpecialConditions(Piece piece, Position destination) {
        // TODO: implementar roque e en passant conforme desejar
    }

    private void checkGameStatus() {
        // TODO: implementar verificação de xeque-mate e empate de fato
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) return false;
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        board.removePiece(lastMove.getTo());
        board.placePiece(lastMove.getPiece(), lastMove.getFrom());
        if (lastMove.getCapturedPiece() != null) {
            board.placePiece(lastMove.getCapturedPiece(), lastMove.getTo());
        }
        isWhiteTurn = !isWhiteTurn;
        return true;
    }
}
