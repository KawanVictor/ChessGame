package controller;

import model.board.Position;
import model.board.Move;
import model.pieces.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessAI {
    private Game game;
    private Random random;
    
    public ChessAI(Game game) {
        this.game = game;
        this.random = new Random();
    }
    
    public void makeMove() {
        List<Move> possibleMoves = getAllPossibleMoves();
        if (possibleMoves.isEmpty()) return;
        
        Move selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        game.selectPiece(selectedMove.getFrom());
        game.movePiece(selectedMove.getTo());
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
