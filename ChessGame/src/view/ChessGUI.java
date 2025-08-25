package view;

import controller.Game;
import controller.ChessAI;
import model.board.Position;
import model.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class ChessGUI extends JFrame {
    private Game game;
    private ChessAI ai;
    private boolean playAgainstAI;
    private boolean aiPlaysWhite;
    
    private JPanel boardPanel;
    private JButton[][] squares;
    private Map<String, ImageIcon> pieceIcons;
    private JLabel turnLabel;
    private JTextArea moveHistoryTextArea;
    
    public ChessGUI() {
        game = new Game();
        ai = new ChessAI(game);
        playAgainstAI = false;
        aiPlaysWhite = false;
        
        initializeGUI();
        loadPieceIcons();
        updateBoardDisplay();
    }
    
    private void initializeGUI() {
        setTitle("Jogo de Xadrez em Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        setLayout(new BorderLayout());
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new BorderLayout());
        turnLabel = new JLabel("Turno: Brancas", JLabel.CENTER);
        infoPanel.add(turnLabel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
        
        // Painel do tabuleiro
        boardPanel = new JPanel(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);
        
        squares = new JButton[8][8];
        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(70, 70));
                squares[row][col].setBackground(isWhite ? new Color(240, 217, 181) : new Color(181, 136, 99));
                
                final int r = row;
                final int c = col;
                squares[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(r, c);
                    }
                });
                
                boardPanel.add(squares[row][col]);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        
        // Painel lateral com histórico
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(180, 600));
        
        JLabel historyLabel = new JLabel("Histórico de Movimentos", JLabel.CENTER);
        rightPanel.add(historyLabel, BorderLayout.NORTH);
        
        moveHistoryTextArea = new JTextArea();
        moveHistoryTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(moveHistoryTextArea);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(rightPanel, BorderLayout.EAST);
        
        // Painel inferior com botões
        JPanel controlPanel = new JPanel();
        
        JButton newGameButton = new JButton("Novo Jogo");
        newGameButton.addActionListener(e -> {
            game = new Game();
            ai = new ChessAI(game);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Brancas");
        });
        controlPanel.add(newGameButton);
        
        JButton undoButton = new JButton("Desfazer");
        undoButton.addActionListener(e -> {
            if (game.undoLastMove()) {
                updateBoardDisplay();
                updateMoveHistory();
                turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
            }
        });
        controlPanel.add(undoButton);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Jogo");
        
        JMenuItem humanVsHuman = new JMenuItem("Humano vs Humano");
        humanVsHuman.addActionListener(e -> {
            playAgainstAI = false;
            game = new Game();
            updateBoardDisplay();
        });
        gameMenu.add(humanVsHuman);
        
        JMenuItem humanVsAI = new JMenuItem("Humano vs Computador (Brancas)");
        humanVsAI.addActionListener(e -> {
            playAgainstAI = true;
            aiPlaysWhite = false;
            game = new Game();
            ai = new ChessAI(game);
            updateBoardDisplay();
        });
        gameMenu.add(humanVsAI);
        
        JMenuItem aiVsHuman = new JMenuItem("Computador vs Humano (Pretas)");
        aiVsHuman.addActionListener(e -> {
            playAgainstAI = true;
            aiPlaysWhite = true;
            game = new Game();
            ai = new ChessAI(game);
            updateBoardDisplay();
            
            ai.makeMove();
            updateBoardDisplay();
        });
        gameMenu.add(aiVsHuman);
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] pieces = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        String[] colors = {"white", "black"};
        
        for (String color : colors) {
            for (String piece : pieces) {
                String key = (color.equals("white") ? "w" : "b") + piece.substring(0, 1).toUpperCase();
                String path = "/resources/" + color + "_" + piece + ".png";
                pieceIcons.put(key, new ImageIcon(getClass().getResource(path)));
            }
        }
    }
    
    private void updateBoardDisplay() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                if (piece == null) {
                    squares[row][col].setIcon(null);
                } else {
                    String key = (piece.isWhite() ? "w" : "b") + piece.getSymbol();
                    squares[row][col].setIcon(pieceIcons.get(key));
                }
                squares[row][col].setBorder(null);
            }
        }
    }
    
    private void handleSquareClick(int row, int col) {
        Position position = new Position(row, col);
        
        if (game.getSelectedPiece() == null) {
            game.selectPiece(position);
            if (game.getSelectedPiece() != null) {
                squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                    squares[pos.getRow()][pos.getColumn()].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                }
            }
        } else {
            Piece selectedPiece = game.getSelectedPiece();
            clearHighlights();
            
            boolean moveSuccessful = game.movePiece(position);
            if (moveSuccessful) {
                updateBoardDisplay();
                updateMoveHistory();
                turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
                
                if (playAgainstAI && game.isWhiteTurn() == aiPlaysWhite && !game.isGameOver()) {
                    Timer timer = new Timer(500, e -> {
                        ai.makeMove();
                        updateBoardDisplay();
                        updateMoveHistory();
                        turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            } else {
                game.selectPiece(position);
                if (game.getSelectedPiece() != null) {
                    squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                    for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                        squares[pos.getRow()][pos.getColumn()].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    }
                }
            }
        }
    }
    
    private void clearHighlights() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c].setBorder(null);
            }
        }
    }
    
    private void updateMoveHistory() {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (int i = 0; i < game.getMoveHistory().size(); i++) {
            if (i % 2 == 0) sb.append(index++).append(". ");
            sb.append(game.getMoveHistory().get(i).toString()).append(" ");
            if (i % 2 == 1) sb.append("\n");
        }
        moveHistoryTextArea.setText(sb.toString());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGUI());
    }
}
