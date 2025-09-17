package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.board.Move;
import model.board.Position;
import model.pieces.Piece;


public class ChessAI {
    public enum Difficulty { RANDOM, MINIMAX }
    private Game game;
    private Random random;
    private Difficulty difficulty;

    public ChessAI(Game game) {
        this(game, Difficulty.RANDOM);
    }

    public ChessAI(Game game, Difficulty difficulty) {
        this.game = game;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void makeMove() {
        if (difficulty == Difficulty.MINIMAX) {
            Move bestMove = findBestMove(2); // profundidade 2 (ajuste para mais dificuldade)
            if (bestMove != null) {
                game.selectPiece(bestMove.getFrom());
                game.movePiece(bestMove.getTo());
            }
        } else { // IA aleatória
            List<Move> possibleMoves = getAllPossibleMoves();
            if (possibleMoves.isEmpty()) return;
            Move selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
            game.selectPiece(selectedMove.getFrom());
            game.movePiece(selectedMove.getTo());
        }
    }

    private Move findBestMove(int depth) {
        List<Move> possibleMoves = getAllPossibleMoves();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        for (Move move : possibleMoves) {
            Piece piece = game.getBoard().getPieceAt(move.getFrom());
            Piece capturedPiece = game.getBoard().getPieceAt(move.getTo());
            game.getBoard().removePiece(move.getFrom());
            game.getBoard().placePiece(piece, move.getTo());

            int moveValue = -minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            game.getBoard().removePiece(move.getTo());
            game.getBoard().placePiece(piece, move.getFrom());
            if (capturedPiece != null) {
                game.getBoard().placePiece(capturedPiece, move.getTo());
            }

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0) return evaluateBoard();
        List<Move> possibleMoves = getAllPossibleMoves();
        if (possibleMoves.isEmpty()) {
            if (game.isInCheck(game.isWhiteTurn())) {
                return isMaximizing ? -10000 : 10000;
            } else {
                return 0;
            }
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                Piece piece = game.getBoard().getPieceAt(move.getFrom());
                Piece capturedPiece = game.getBoard().getPieceAt(move.getTo());
                game.getBoard().removePiece(move.getFrom());
                game.getBoard().placePiece(piece, move.getTo());

                int eval = minimax(depth - 1, alpha, beta, false);

                game.getBoard().removePiece(move.getTo());
                game.getBoard().placePiece(piece, move.getFrom());
                if (capturedPiece != null) {
                    game.getBoard().placePiece(capturedPiece, move.getTo());
                }

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                Piece piece = game.getBoard().getPieceAt(move.getFrom());
                Piece capturedPiece = game.getBoard().getPieceAt(move.getTo());
                game.getBoard().removePiece(move.getFrom());
                game.getBoard().placePiece(piece, move.getTo());

                int eval = minimax(depth - 1, alpha, beta, true);

                game.getBoard().removePiece(move.getTo());
                game.getBoard().placePiece(piece, move.getFrom());
                if (capturedPiece != null) {
                    game.getBoard().placePiece(capturedPiece, move.getTo());
                }

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluateBoard() {
        int value = 0;
        // Peão 100, Cavalo/Bispo 300, Torre 500, Rainha 900, Rei 10000
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    if (piece.isWhite() == game.isWhiteTurn()) {
                        value += pieceValue;
                    } else {
                        value -= pieceValue;
                    }
                }
            }
        }
        return value;
    }

    private int getPieceValue(Piece piece) {
        switch (piece.getSymbol()) {
            case "P": return 100;
            case "N": return 300;
            case "B": return 300;
            case "R": return 500;
            case "Q": return 900;
            case "K": return 10000;
            default: return 0;
        }
    }

    private List<Move> getAllPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        boolean isWhite = game.isWhiteTurn();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = game.getBoard().getPieceAt(pos);
                if (piece != null && piece.isWhite() == isWhite) {
                    for (Position movePos : piece.getPossibleMoves()) {
                        if (!game.moveCausesCheck(piece, movePos)) {
                            moves.add(new Move(pos, movePos, piece, game.getBoard().getPieceAt(movePos)));
                        }
                    }
                }
            }
        }
        return moves;
    }
}
