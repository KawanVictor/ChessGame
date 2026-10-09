package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.board.Board;
import model.board.Move;
import model.board.Position;
import model.pieces.*;

// Testes das regras e da IA, sem dependências externas. Executar com: java -cp bin controller.GameTest
public class GameTest {
    private static int failures = 0;

    private static Position p(String square) {
        return new Position(8 - (square.charAt(1) - '0'), square.charAt(0) - 'a');
    }

    private static boolean move(Game game, String from, String to) {
        game.selectPiece(p(from));
        return game.movePiece(p(to));
    }

    private static void check(String name, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        if (!ok) failures++;
        System.out.println((ok ? "OK    " : "FALHA ") + name + (ok ? "" : ": obtido " + actual + ", esperado " + expected));
    }

    // Conta as sequências de lances legais até a profundidade dada (valores de referência conhecidos)
    private static long perft(Game game, int depth) {
        List<Move> moves = game.getAllLegalMoves();
        if (depth == 1) return moves.size();
        long nodes = 0;
        for (Move m : moves) {
            game.simulateMove(m);
            nodes += perft(game, depth - 1);
            game.undoSimulatedMove();
        }
        return nodes;
    }

    // Monta uma posição a partir do campo de peças de um FEN (brancas jogam)
    private static Game fromFen(String fen) {
        Game game = new Game();
        Board board = game.getBoard();
        board.clear();
        String[] rows = fen.split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char ch : rows[row].toCharArray()) {
                if (Character.isDigit(ch)) { col += ch - '0'; continue; }
                boolean white = Character.isUpperCase(ch);
                Piece piece;
                switch (Character.toLowerCase(ch)) {
                    case 'k': piece = new King(board, white); break;
                    case 'q': piece = new Queen(board, white); break;
                    case 'r': piece = new Rook(board, white); break;
                    case 'b': piece = new Bishop(board, white); break;
                    case 'n': piece = new Knight(board, white); break;
                    default: piece = new Pawn(board, white);
                }
                board.placePiece(piece, new Position(row, col++));
            }
        }
        return game;
    }

    private static String snapshot(Game game) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                if (piece == null) sb.append('.');
                else sb.append(piece.isWhite() ? piece.getSymbol() : piece.getSymbol().toLowerCase());
            }
        }
        return sb.toString() + (game.isWhiteTurn() ? " w" : " b");
    }

    private static void testPerft() {
        Game game = new Game();
        String before = snapshot(game);
        check("perft inicial d1", perft(game, 1), 20);
        check("perft inicial d2", perft(game, 2), 400);
        check("perft inicial d3", perft(game, 3), 8902);
        check("perft inicial d4", perft(game, 4), 197281);
        check("tabuleiro intacto após perft", snapshot(game), before);

        // Posição "Kiwipete": exercita roque, en passant e peças cravadas
        Game kiwipete = fromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R");
        check("perft kiwipete d1", perft(kiwipete, 1), 48);
        check("perft kiwipete d2", perft(kiwipete, 2), 2039);
        check("perft kiwipete d3", perft(kiwipete, 3), 97862);
    }

    private static void testCheckAndMate() {
        Game game = new Game();
        game.selectPiece(p("e2"));
        game.selectPiece(null);
        check("selectPiece(null) limpa a seleção", game.getSelectedPiece(), null);

        // Mate do louco
        game = new Game();
        move(game, "f2", "f3"); move(game, "e7", "e5"); move(game, "g2", "g4"); move(game, "d8", "h4");
        check("mate do louco encerra o jogo", game.isGameOver(), true);
        check("mate do louco é xeque-mate", game.isCheckmate(), true);
        check("lance após o mate é recusado", move(game, "a2", "a3"), false);
        game.undoLastMove();
        check("desfazer o mate reabre o jogo", game.isGameOver(), false);

        game = new Game();
        move(game, "e2", "e4"); move(game, "f7", "f6"); move(game, "d1", "h5");
        check("pretas em xeque", game.isInCheck(false), true);
        check("ignorar o xeque é recusado", move(game, "a7", "a6"), false);
        check("bloquear o xeque é aceito", move(game, "g7", "g6"), true);

        game = fromFen("8/8/4k3/8/4K3/8/8/8");
        check("rei ao lado do outro rei é recusado", move(game, "e4", "e5"), false);
    }

    private static void testCastling() {
        Game game = new Game();
        move(game, "e2", "e4"); move(game, "e7", "e5"); move(game, "g1", "f3"); move(game, "b8", "c6");
        move(game, "f1", "c4"); move(game, "f8", "c5");
        check("roque pequeno é aceito", move(game, "e1", "g1"), true);
        check("torre vai para f1", game.getBoard().getPieceAt(p("f1")) instanceof Rook, true);
        check("notação do roque", game.getMoveHistory().get(6), "O-O");
        game.undoLastMove();
        check("desfazer roque devolve a torre a h1", game.getBoard().getPieceAt(p("h1")) instanceof Rook, true);
        check("desfazer roque devolve o rei a e1", game.getBoard().getPieceAt(p("e1")) instanceof King, true);
        check("desfazer roque esvazia f1", game.getBoard().getPieceAt(p("f1")), null);

        move(game, "e1", "e2"); move(game, "a7", "a6"); move(game, "e2", "e1"); move(game, "a6", "a5");
        check("roque após mover o rei é recusado", move(game, "e1", "g1"), false);
    }

    private static void testEnPassant() {
        Game game = new Game();
        move(game, "e2", "e4"); move(game, "a7", "a6"); move(game, "e4", "e5"); move(game, "d7", "d5");
        check("en passant é aceito", move(game, "e5", "d6"), true);
        check("peão capturado sai do tabuleiro", game.getBoard().getPieceAt(p("d5")), null);
        game.undoLastMove();
        check("desfazer en passant restaura o peão", game.getBoard().getPieceAt(p("d5")) instanceof Pawn, true);
        move(game, "a2", "a3"); move(game, "a6", "a5");
        check("en passant atrasado é recusado", move(game, "e5", "d6"), false);
    }

    private static void testPromotion() {
        Game game = fromFen("7k/P7/8/8/8/8/8/K7");
        check("lance de promoção é aceito", move(game, "a7", "a8"), true);
        check("promoção fica pendente", game.hasPendingPromotion(), true);
        game.promotePawnAt(game.getPendingPromotionPosition(), "Torre");
        check("xeque após promover a torre", game.isInCheck(false), true);
        check("notação da promoção", game.getMoveHistory().get(0), "Pa7-a8=R");
        game.undoLastMove();
        check("desfazer promoção devolve o peão", game.getBoard().getPieceAt(p("a7")) instanceof Pawn, true);
        check("desfazer promoção esvazia a8", game.getBoard().getPieceAt(p("a8")), null);
    }

    private static void testDraws() {
        Game game = fromFen("7k/8/5K2/6Q1/8/8/8/8");
        move(game, "g5", "g6");
        check("afogamento encerra o jogo", game.isDraw(), true);
        check("motivo: afogamento", game.getDrawReason(), "Afogamento");

        game = fromFen("7k/8/8/8/8/8/p7/K7");
        move(game, "a1", "a2");
        check("rei contra rei é empate", game.getDrawReason(), "Material insuficiente");

        game = fromFen("7k/8/8/8/8/8/p7/K1N5");
        move(game, "a1", "a2");
        check("rei e cavalo contra rei é empate", game.getDrawReason(), "Material insuficiente");

        game = fromFen("7k/8/8/8/8/8/p7/K1R5");
        move(game, "a1", "a2");
        check("rei e torre contra rei não é empate", game.isGameOver(), false);

        // Tripla repetição: os cavalos vão e voltam
        game = new Game();
        move(game, "g1", "f3"); move(game, "g8", "f6"); move(game, "f3", "g1"); move(game, "f6", "g8");
        check("duas repetições não encerram", game.isGameOver(), false);
        move(game, "g1", "f3"); move(game, "g8", "f6"); move(game, "f3", "g1"); move(game, "f6", "g8");
        check("motivo: tripla repetição", game.getDrawReason(), "Tripla repetição");
        game.undoLastMove();
        check("desfazer a repetição reabre o jogo", game.isGameOver(), false);
        check("refazer o lance repete o empate", move(game, "f6", "g8") && game.isDraw(), true);

        // 50 lances: a torre percorre casas diferentes para não repetir posições
        game = fromFen("7k/8/8/8/8/8/8/KR6");
        List<Position> path = new ArrayList<>();
        for (int col = 1; col <= 4; col++) {
            for (int i = 0; i < 6; i++) {
                int rank = (col % 2 == 1) ? 1 + i : 6 - i;
                path.add(new Position(8 - rank, col));
            }
        }
        int index = 0, direction = 1, kingRank = 1, whiteMoves = 0;
        boolean accepted = true;
        while (whiteMoves < 50) {
            int next = index + direction;
            if (next < 0 || next >= path.size()) {
                // Fim do percurso: o rei sobe uma casa e a torre volta
                game.selectPiece(new Position(8 - kingRank, 0));
                accepted &= game.movePiece(new Position(8 - ++kingRank, 0));
                direction = -direction;
            } else {
                game.selectPiece(path.get(index));
                accepted &= game.movePiece(path.get(next));
                index = next;
            }
            whiteMoves++;
            if (whiteMoves == 50) check("49 lances e meio não encerram", game.isGameOver(), false);
            accepted &= (whiteMoves % 2 == 1) ? move(game, "h8", "g8") : move(game, "g8", "h8");
        }
        check("lances da regra dos 50 aceitos", accepted, true);
        check("motivo: regra dos 50 lances", game.getDrawReason(), "Regra dos 50 lances");
    }

    private static void testAI() {
        // Dama branca desprotegida em g4, atacada pelo bispo de c8
        Game game = new Game();
        move(game, "e2", "e4"); move(game, "a7", "a6"); move(game, "d1", "g4");
        move(game, "d7", "d5"); move(game, "a2", "a3");
        new ChessAI(game, ChessAI.Difficulty.MINIMAX).makeMove();
        check("minimax captura a dama desprotegida", game.getMoveHistory().get(5), "Bc8xg4");

        game = new Game();
        move(game, "f2", "f3"); move(game, "e7", "e5"); move(game, "g2", "g4");
        new ChessAI(game, ChessAI.Difficulty.MINIMAX).makeMove();
        check("minimax encontra mate em 1", game.isCheckmate(), true);

        Set<String> replies = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            game = new Game();
            move(game, "e2", "e4");
            new ChessAI(game, ChessAI.Difficulty.MINIMAX).makeMove();
            replies.add(game.getMoveHistory().get(1).toString());
        }
        check("minimax varia a resposta a 1.e4", replies.size() > 1, true);

        game = fromFen("7k/8/8/8/8/8/p7/7K");
        move(game, "h1", "h2");
        new ChessAI(game, ChessAI.Difficulty.MINIMAX).makeMove();
        check("IA promove sem deixar pendência", game.hasPendingPromotion(), false);
        check("IA promove a rainha", game.getBoard().getPieceAt(p("a1")) instanceof Queen, true);

        // Partidas completas entre as duas IAs devem sempre terminar
        int finished = 0;
        for (int i = 0; i < 5; i++) {
            game = new Game();
            ChessAI white = new ChessAI(game, ChessAI.Difficulty.RANDOM);
            ChessAI black = new ChessAI(game, ChessAI.Difficulty.MINIMAX);
            int plies = 0;
            while (!game.isGameOver() && plies++ < 1000) {
                (game.isWhiteTurn() ? white : black).makeMove();
            }
            if (game.isGameOver()) finished++;
        }
        check("partidas IA aleatória x minimax terminam", finished, 5);
    }

    public static void main(String[] args) {
        testPerft();
        testCheckAndMate();
        testCastling();
        testEnPassant();
        testPromotion();
        testDraws();
        testAI();
        System.out.println(failures == 0 ? "Todos os testes passaram." : failures + " teste(s) falharam.");
        if (failures > 0) System.exit(1);
    }
}
