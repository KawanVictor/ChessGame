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
    private boolean isCheckmate;
    private Piece selectedPiece;
    private List<Move> moveHistory;

    // Controle de empate
    private String drawReason;
    private List<String> positionKeys;

    // Controle de promoção
    private boolean pendingPromotion = false;
    private Position pendingPromotionPosition = null;

    public Game() {
        board = new Board();
        isWhiteTurn = true;
        isGameOver = false;
        isCheckmate = false;
        moveHistory = new ArrayList<>();
        positionKeys = new ArrayList<>();
        setupPieces();
        positionKeys.add(positionKey());
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
    public boolean isCheckmate() { return isCheckmate; }
    public boolean isDraw() { return isGameOver && !isCheckmate; }
    public String getDrawReason() { return drawReason; }
    public Piece getSelectedPiece() { return selectedPiece; }
    public List<Move> getMoveHistory() { return moveHistory; }

    // Métodos para promoção personalizada
    public boolean hasPendingPromotion() { return pendingPromotion; }
    public Position getPendingPromotionPosition() { return pendingPromotionPosition; }

    public void promotePawnAt(Position pos, String escolha) {
        Piece oldPawn = board.getPieceAt(pos);
        if (!pendingPromotion || oldPawn == null) return;
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
        moveHistory.get(moveHistory.size() - 1).setPromotionSymbol(newPiece.getSymbol());
        pendingPromotion = false;
        pendingPromotionPosition = null;
        checkGameStatus();
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
        if (selectedPiece == null || isGameOver || pendingPromotion) return false;
        if (!getLegalMoves(selectedPiece).contains(destination)) return false;

        Move move = createMove(selectedPiece, destination);
        applyMove(move);
        selectedPiece = null;

        // PROMOÇÃO de peão: o status do jogo só é verificado após a escolha da peça
        if (move.isPromotion()) {
            pendingPromotion = true;
            pendingPromotionPosition = destination;
        } else {
            checkGameStatus();
        }
        return true;
    }

    // Movimentos da peça que não deixam o próprio rei em xeque (inclui roque e en passant)
    public List<Position> getLegalMoves(Piece piece) {
        List<Position> candidates = new ArrayList<>(piece.getPossibleMoves());
        addCastlingMoves(piece, candidates);
        addEnPassantMove(piece, candidates);

        List<Position> legalMoves = new ArrayList<>();
        for (Position destination : candidates) {
            if (!moveCausesCheck(piece, destination)) legalMoves.add(destination);
        }
        return legalMoves;
    }

    public List<Move> getAllLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == isWhiteTurn) {
                    for (Position destination : getLegalMoves(piece)) {
                        moves.add(createMove(piece, destination));
                    }
                }
            }
        }
        return moves;
    }

    public boolean moveCausesCheck(Piece piece, Position destination) {
        // Simula o movimento e verifica se deixa o próprio rei em xeque
        Move move = createMove(piece, destination);
        executeOnBoard(move);
        boolean inCheck = isInCheck(piece.isWhite());
        undoOnBoard(move);
        return inCheck;
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

    private void addCastlingMoves(Piece piece, List<Position> moves) {
        if (!(piece instanceof King) || hasMoved(piece)) return;
        boolean white = piece.isWhite();
        int row = white ? 7 : 0;
        if (!piece.getPosition().equals(new Position(row, 4)) || isInCheck(white)) return;

        // Roque pequeno
        if (canCastleWith(new Position(row, 7), white)
                && board.isPositionEmpty(new Position(row, 5))
                && board.isPositionEmpty(new Position(row, 6))
                && !board.isUnderAttack(new Position(row, 5), !white)) {
            moves.add(new Position(row, 6));
        }
        // Roque grande
        if (canCastleWith(new Position(row, 0), white)
                && board.isPositionEmpty(new Position(row, 1))
                && board.isPositionEmpty(new Position(row, 2))
                && board.isPositionEmpty(new Position(row, 3))
                && !board.isUnderAttack(new Position(row, 3), !white)) {
            moves.add(new Position(row, 2));
        }
    }

    private boolean canCastleWith(Position rookPos, boolean white) {
        Piece rook = board.getPieceAt(rookPos);
        return rook instanceof Rook && rook.isWhite() == white && !hasMoved(rook);
    }

    private void addEnPassantMove(Piece piece, List<Position> moves) {
        if (!(piece instanceof Pawn) || moveHistory.isEmpty()) return;
        Move last = moveHistory.get(moveHistory.size() - 1);
        Position pos = piece.getPosition();
        if (last.getPiece() instanceof Pawn
                && Math.abs(last.getFrom().getRow() - last.getTo().getRow()) == 2
                && last.getTo().getRow() == pos.getRow()
                && Math.abs(last.getTo().getColumn() - pos.getColumn()) == 1) {
            int direction = piece.isWhite() ? -1 : 1;
            moves.add(new Position(pos.getRow() + direction, last.getTo().getColumn()));
        }
    }

    private boolean hasMoved(Piece piece) {
        for (Move move : moveHistory) {
            if (move.getPiece() == piece) return true;
        }
        return false;
    }

    private Move createMove(Piece piece, Position destination) {
        Position from = piece.getPosition();
        Piece captured = board.getPieceAt(destination);

        if (piece instanceof Pawn) {
            Move move;
            if (from.getColumn() != destination.getColumn() && captured == null) {
                // En passant: o peão capturado está ao lado, não na casa de destino
                Position capturedPos = new Position(from.getRow(), destination.getColumn());
                move = new Move(from, destination, piece, board.getPieceAt(capturedPos));
                move.setEnPassant(capturedPos);
            } else {
                move = new Move(from, destination, piece, captured);
            }
            int lastRow = piece.isWhite() ? 0 : 7;
            move.setPromotion(destination.getRow() == lastRow);
            return move;
        }

        Move move = new Move(from, destination, piece, captured);
        if (piece instanceof King && Math.abs(destination.getColumn() - from.getColumn()) == 2) {
            boolean kingSide = destination.getColumn() > from.getColumn();
            move.setCastling(new Position(from.getRow(), kingSide ? 7 : 0),
                    new Position(from.getRow(), kingSide ? 5 : 3));
        }
        return move;
    }

    private void executeOnBoard(Move move) {
        if (move.getCapturedPiece() != null) board.removePiece(move.getCapturedPosition());
        board.removePiece(move.getFrom());
        board.placePiece(move.getPiece(), move.getTo());
        if (move.isCastling()) {
            Piece rook = board.getPieceAt(move.getRookFrom());
            board.removePiece(move.getRookFrom());
            board.placePiece(rook, move.getRookTo());
        }
    }

    private void undoOnBoard(Move move) {
        board.removePiece(move.getTo());
        board.placePiece(move.getPiece(), move.getFrom());
        if (move.getCapturedPiece() != null) {
            board.placePiece(move.getCapturedPiece(), move.getCapturedPosition());
        }
        if (move.isCastling()) {
            Piece rook = board.getPieceAt(move.getRookTo());
            board.removePiece(move.getRookTo());
            board.placePiece(rook, move.getRookFrom());
        }
    }

    private void applyMove(Move move) {
        executeOnBoard(move);
        moveHistory.add(move);
        isWhiteTurn = !isWhiteTurn;
    }

    private void revertLastMove() {
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        undoOnBoard(lastMove);
        isWhiteTurn = !isWhiteTurn;
    }

    // Usados pela IA para explorar lances sem alterar o estado da partida (promove sempre a rainha)
    void simulateMove(Move move) {
        applyMove(move);
        if (move.isPromotion()) {
            board.placePiece(new Queen(board, move.getPiece().isWhite()), move.getTo());
        }
    }

    void undoSimulatedMove() {
        revertLastMove();
    }

    private void checkGameStatus() {
        isGameOver = false;
        isCheckmate = false;
        drawReason = null;
        positionKeys.add(positionKey());

        if (getAllLegalMoves().isEmpty()) {
            // Sem lances: xeque-mate se o rei está em xeque, senão afogamento
            isGameOver = true;
            isCheckmate = isInCheck(isWhiteTurn);
            if (!isCheckmate) drawReason = "Afogamento";
        } else if (hasInsufficientMaterial()) {
            drawReason = "Material insuficiente";
        } else if (movesWithoutProgress() >= 100) {
            drawReason = "Regra dos 50 lances";
        } else if (countRepetitions() >= 3) {
            drawReason = "Tripla repetição";
        }
        if (drawReason != null) isGameOver = true;
    }

    // Rei contra rei, rei e peça menor contra rei, ou apenas bispos em casas da mesma cor
    private boolean hasInsufficientMaterial() {
        List<Piece> others = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && !(piece instanceof King)) others.add(piece);
            }
        }
        if (others.isEmpty()) return true;
        if (others.size() == 1) return others.get(0) instanceof Knight || others.get(0) instanceof Bishop;

        int squareColor = -1;
        for (Piece piece : others) {
            if (!(piece instanceof Bishop)) return false;
            int color = (piece.getPosition().getRow() + piece.getPosition().getColumn()) % 2;
            if (squareColor != -1 && color != squareColor) return false;
            squareColor = color;
        }
        return true;
    }

    // Meios-lances desde a última captura ou movimento de peão
    private int movesWithoutProgress() {
        int count = 0;
        for (int i = moveHistory.size() - 1; i >= 0; i--) {
            Move move = moveHistory.get(i);
            if (move.getPiece() instanceof Pawn || move.getCapturedPiece() != null) break;
            count++;
        }
        return count;
    }

    private int countRepetitions() {
        String current = positionKeys.get(positionKeys.size() - 1);
        int count = 0;
        for (String key : positionKeys) {
            if (key.equals(current)) count++;
        }
        return count;
    }

    // Identifica a posição: peças, vez, direitos de roque e coluna de en passant
    private String positionKey() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece == null) sb.append('.');
                else sb.append(piece.isWhite() ? piece.getSymbol() : piece.getSymbol().toLowerCase());
            }
        }
        sb.append(isWhiteTurn ? 'w' : 'b');
        for (boolean white : new boolean[] {true, false}) {
            int row = white ? 7 : 0;
            Piece king = board.getPieceAt(new Position(row, 4));
            boolean kingAtHome = king instanceof King && king.isWhite() == white && !hasMoved(king);
            sb.append(kingAtHome && canCastleWith(new Position(row, 7), white) ? '1' : '0');
            sb.append(kingAtHome && canCastleWith(new Position(row, 0), white) ? '1' : '0');
        }
        if (!moveHistory.isEmpty()) {
            Move last = moveHistory.get(moveHistory.size() - 1);
            if (last.getPiece() instanceof Pawn
                    && Math.abs(last.getFrom().getRow() - last.getTo().getRow()) == 2) {
                sb.append(last.getTo().getColumn());
            }
        }
        return sb.toString();
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) return false;
        revertLastMove();
        // Mantém uma chave por lance concluído, além da posição inicial
        while (positionKeys.size() > moveHistory.size() + 1) {
            positionKeys.remove(positionKeys.size() - 1);
        }
        pendingPromotion = false;
        pendingPromotionPosition = null;
        isGameOver = false;
        isCheckmate = false;
        drawReason = null;
        selectedPiece = null;
        return true;
    }
}
