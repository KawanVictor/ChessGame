package controller;

import model.board.Board;
import model.board.Position;
import model.board.Move;
import model.pieces.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private Piece selectedPiece;
    private List<Move> moveHistory;
    
    public Game() {
        board = new Board();
        isWhiteTurn = true;
        isGameOver = false;
        moveHistory = new ArrayList<>();
        setupPieces();
    }

    private void setupPieces() {
        // Brancas
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
        
        // Pretas
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
        
        checkSpecialConditions(selectedPiece, destination);
        checkGameStatus();
        
        isWhiteTurn = !isWhiteTurn;
        selectedPiece = null;
        return true;
    }
    
    private boolean moveCausesCheck(Piece piece, Position destination) {
        // TODO: implementar lógica completa
        return false;
    }
    
    private void checkSpecialConditions(Piece piece, Position destination) {
        // TODO: implementar promoção, roque e en passant
    }
    
    private void checkGameStatus() {
        // TODO: implementar verificação de xeque, xeque-mate e empate
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
