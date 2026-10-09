package model.pieces;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.board.Position;

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

    // Movimentos em linha (torre, bispo e rainha): avança em cada direção até encontrar uma peça
    protected List<Position> getSlidingMoves(int[][] directions) {
        List<Position> moves = new ArrayList<>();
        for (int[] direction : directions) {
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];
                Position newPos = new Position(row, col);

                if (!newPos.isValid()) break;

                Piece pieceAt = board.getPieceAt(newPos);
                if (pieceAt == null) {
                    moves.add(newPos);
                } else if (pieceAt.isWhite() != isWhite) {
                    moves.add(newPos);
                    break;
                } else break;
            }
        }
        return moves;
    }

    public boolean canMoveTo(Position position) {
        List<Position> possibleMoves = getPossibleMoves();
        return possibleMoves.contains(position);
    }

    // Casas que a peça ameaça (difere dos movimentos apenas para o peão)
    public boolean attacks(Position position) {
        return canMoveTo(position);
    }

    public abstract String getSymbol();
}
