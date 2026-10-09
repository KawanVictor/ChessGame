package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.board.Move;
import model.board.Position;
import model.pieces.Piece;


public class ChessAI {
    public enum Difficulty { RANDOM, MINIMAX }
    private static final int INFINITY = 1000000;
    private static final int MATE_VALUE = 100000;
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
        List<Move> possibleMoves = game.getAllLegalMoves();
        if (possibleMoves.isEmpty()) return;

        Move selectedMove;
        if (difficulty == Difficulty.MINIMAX) {
            selectedMove = findBestMove(possibleMoves, 2); // profundidade 2 (ajuste para mais dificuldade)
        } else { // IA aleatória
            selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
        game.selectPiece(selectedMove.getFrom());
        game.movePiece(selectedMove.getTo());
        // A IA sempre promove a rainha
        if (game.hasPendingPromotion()) {
            game.promotePawnAt(game.getPendingPromotionPosition(), "rainha");
        }
    }

    private Move findBestMove(List<Move> possibleMoves, int depth) {
        List<Move> bestMoves = new ArrayList<>();
        int bestValue = -INFINITY;
        for (Move move : possibleMoves) {
            game.simulateMove(move);
            int moveValue = -minimax(depth - 1, -INFINITY, INFINITY);
            game.undoSimulatedMove();

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMoves.clear();
            }
            if (moveValue == bestValue) bestMoves.add(move);
        }
        // Sorteia entre os lances de mesmo valor para não jogar sempre igual
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    // Minimax na forma negamax com poda alfa-beta: o valor é sempre do ponto de vista de quem joga
    private int minimax(int depth, int alpha, int beta) {
        List<Move> possibleMoves = game.getAllLegalMoves();
        if (possibleMoves.isEmpty()) {
            // Xeque-mate (quanto mais cedo, pior) ou afogamento
            return game.isInCheck(game.isWhiteTurn()) ? -MATE_VALUE - depth : 0;
        }
        if (depth == 0) return evaluateBoard();

        int bestValue = -INFINITY;
        for (Move move : possibleMoves) {
            game.simulateMove(move);
            int eval = -minimax(depth - 1, -beta, -alpha);
            game.undoSimulatedMove();

            bestValue = Math.max(bestValue, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) break;
        }
        return bestValue;
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
}
