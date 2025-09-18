package view;

import controller.Game;
import controller.ChessAI;
import model.board.Position;
import model.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private JTextArea moveHistoryArea;
    private JLabel statusLabel;

    public ChessGUI() {
        setLookAndFeel();
        game = new Game();
        ai = new ChessAI(game, ChessAI.Difficulty.RANDOM);
        playAgainstAI = false;
        aiPlaysWhite = false;
        initializeGUI();
        loadPieceIcons();
        updateBoardDisplay();
        updateMoveHistory();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", new Color(36, 36, 36));
            UIManager.put("nimbusBase", new Color(56, 56, 76));
            UIManager.put("nimbusBlueGrey", new Color(36, 36, 56));
            UIManager.put("text", Color.WHITE);
            UIManager.put("MenuBar.background", new Color(24, 24, 32));
            UIManager.put("MenuBar.foreground", Color.WHITE);
            UIManager.put("Menu.foreground", Color.WHITE);
            UIManager.put("Menu.background", new Color(24, 24, 32));
            UIManager.put("MenuItem.background", new Color(48, 48, 64));
            UIManager.put("MenuItem.foreground", Color.WHITE);
        } catch (Exception ignored) {}
    }

    private void initializeGUI() {
        setTitle("Jogo de Xadrez em Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 670);
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(28,28,28));
        turnLabel = new JLabel("Turno: Brancas", JLabel.CENTER);
        turnLabel.setForeground(new Color(235, 235, 235));
        turnLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        infoPanel.add(turnLabel, BorderLayout.CENTER);
        statusLabel = new JLabel("Bem-vindo ao Jogo de Xadrez!", JLabel.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        statusLabel.setForeground(new Color(160, 207, 246));
        infoPanel.add(statusLabel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.NORTH);

        createBoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(220, 650));
        rightPanel.setBackground(new Color(36,36,36));
        JLabel historyLabel = new JLabel("Histórico de Movimentos", JLabel.CENTER);
        historyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        historyLabel.setForeground(new Color(160, 207, 246));
        rightPanel.add(historyLabel, BorderLayout.NORTH);

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setBackground(new Color(48,48,64));
        moveHistoryArea.setForeground(Color.WHITE);
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30,30,38));
        JButton newGameButton = createModernButton("Novo Jogo");
        newGameButton.addActionListener(e -> {
            game = new Game();
            ai = new ChessAI(game, ChessAI.Difficulty.RANDOM);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Brancas");
            statusLabel.setText("Novo jogo iniciado!");
        });
        controlPanel.add(newGameButton);

        JButton undoButton = createModernButton("Desfazer");
        undoButton.addActionListener(e -> {
            if (game.undoLastMove()) {
                updateBoardDisplay();
                updateMoveHistory();
                turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
                statusLabel.setText("Último movimento desfeito.");
            } else {
                statusLabel.setText("Nada a desfazer!");
            }
        });
        controlPanel.add(undoButton);

        add(controlPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(24,24,32));
        menuBar.setBorderPainted(false);
        JMenu gameMenu = new JMenu("Jogo");
        gameMenu.setForeground(Color.WHITE);

        JMenuItem humanVsHuman = new JMenuItem("Humano vs Humano");
        humanVsHuman.addActionListener(e -> {
            playAgainstAI = false;
            game = new Game();
            ai = new ChessAI(game, ChessAI.Difficulty.RANDOM);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Brancas");
            statusLabel.setText("Modo: Humano vs Humano.");
        });
        gameMenu.add(humanVsHuman);

        JMenuItem humanVsAiRandom = new JMenuItem("Humano vs Computador (IA Simples)");
        humanVsAiRandom.addActionListener(e -> {
            playAgainstAI = true;
            aiPlaysWhite = false;
            game = new Game();
            ai = new ChessAI(game, ChessAI.Difficulty.RANDOM);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Brancas");
            statusLabel.setText("Modo: Humano vs IA Simples.");
        });
        gameMenu.add(humanVsAiRandom);

        JMenuItem humanVsAiMinimax = new JMenuItem("Humano vs Computador (IA Minimax)");
        humanVsAiMinimax.addActionListener(e -> {
            playAgainstAI = true;
            aiPlaysWhite = false;
            game = new Game();
            ai = new ChessAI(game, ChessAI.Difficulty.MINIMAX);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Brancas");
            statusLabel.setText("Modo: Humano vs IA Minimax.");
        });
        gameMenu.add(humanVsAiMinimax);

        JMenuItem aiVsHuman = new JMenuItem("Computador vs Humano (Pretas, IA Simples)");
        aiVsHuman.addActionListener(e -> {
            playAgainstAI = true;
            aiPlaysWhite = true;
            game = new Game();
            ai = new ChessAI(game, ChessAI.Difficulty.RANDOM);
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: Pretas");
            statusLabel.setText("Modo: IA começa!");
            ai.makeMove();
            updateBoardDisplay();
            updateMoveHistory();
            turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
        });
        gameMenu.add(aiVsHuman);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createModernButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(77, 138, 209));
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8,24,8,24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        return btn;
    }

    private void createBoardPanel() {
        squares = new JButton[8][8];
        boardPanel = new JPanel(new GridLayout(8, 8)) {
            @Override
            public Insets getInsets() { return new Insets(8,8,8,8); }
        };
        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(72, 72));
                squares[row][col].setBackground(isWhite ? new Color(225, 199, 159) : new Color(143, 96, 50));
                squares[row][col].setBorder(BorderFactory.createEmptyBorder());
                squares[row][col].setFocusPainted(false);
                squares[row][col].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                squares[row][col].setHorizontalAlignment(SwingConstants.LEFT);
                squares[row][col].setVerticalAlignment(SwingConstants.TOP);
                final int r = row;
                final int c = col;

                String label = String.valueOf((char)('a' + col)) + (8 - row);
                squares[row][col].setText("<html><span style='font-size:10px; color:gray;'>" + label + "</span></html>");

                squares[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) { handleSquareClick(r, c);}
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        squares[r][c].setBackground(new Color(227, 167, 33, 100));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        boolean baseWhite = ((r+c)%2 == 0);
                        squares[r][c].setBackground(baseWhite ? new Color(225, 199, 159) : new Color(143, 96, 50));
                    }
                });
                boardPanel.add(squares[row][col]);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();
        String[] pieces = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        String[] colors = {"white", "black"};
        for (String color : colors) {
            for (String piece : pieces) {
                String key = color + "_" + piece;
                String path = "/" + key + ".png";
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    pieceIcons.put(key, new ImageIcon(imgURL));
                } else {
                    System.err.println("Imagem não encontrada: " + path);
                }
            }
        }
    }

    private String getPieceIconKey(Piece piece) {
        String color = piece.isWhite() ? "white" : "black";
        switch (piece.getSymbol()) {
            case "K": return color + "_king";
            case "Q": return color + "_queen";
            case "R": return color + "_rook";
            case "B": return color + "_bishop";
            case "N": return color + "_knight";
            case "P": return color + "_pawn";
            default: return null;
        }
    }

    private void updateBoardDisplay() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                String label = String.valueOf((char)('a' + col)) + (8 - row);
                if (piece == null) {
                    squares[row][col].setText("<html><span style='font-size:10px; color:gray;'>" + label + "</span></html>");
                    squares[row][col].setIcon(null);
                } else {
                    String key = getPieceIconKey(piece);
                    squares[row][col].setIcon(pieceIcons.get(key));
                    squares[row][col].setText("");
                }
                squares[row][col].setBorder(BorderFactory.createEmptyBorder());
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
        moveHistoryArea.setText(sb.toString());
    }

    private void handleSquareClick(int row, int col) {
        Position position = new Position(row, col);
        if (game.getSelectedPiece() == null) {
            game.selectPiece(position);
            if (game.getSelectedPiece() != null) {
                squares[row][col].setBorder(BorderFactory.createLineBorder(new Color(51, 182, 222), 3));
                for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                    squares[pos.getRow()][pos.getColumn()].setBorder(
                            BorderFactory.createLineBorder(new Color(90, 252, 108), 3));
                }
                statusLabel.setText("Peça selecionada: " + game.getSelectedPiece().getSymbol() + " (" +
                        (game.getSelectedPiece().isWhite() ? "Branca" : "Preta") + ")");
            }
        } else {
            Piece selectedPiece = game.getSelectedPiece();
            clearHighlights();
            if (selectedPiece.getPosition().equals(position)) {
                game.selectPiece(null);
                statusLabel.setText("Seleção desfeita.");
            } else if (game.getBoard().getPieceAt(position) != null &&
                    selectedPiece.isWhite() == game.getBoard().getPieceAt(position).isWhite()) {
                game.selectPiece(position);
                if (game.getSelectedPiece() != null) {
                    squares[row][col].setBorder(BorderFactory.createLineBorder(new Color(51, 182, 222), 3));
                    for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                        squares[pos.getRow()][pos.getColumn()].setBorder(
                                BorderFactory.createLineBorder(new Color(90, 252, 108), 3));
                    }
                }
                statusLabel.setText("Nova peça selecionada: " + game.getSelectedPiece().getSymbol() + " (" +
                        (game.getSelectedPiece().isWhite() ? "Branca" : "Preta") + ")");
            } else {
                boolean moveSuccessful = game.movePiece(position);
                if (moveSuccessful) {
                    updateBoardDisplay();
                    updateMoveHistory();
                    turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
                    statusLabel.setText("Movimento realizado.");

                    if (game.isInCheck(game.isWhiteTurn())) {
                        JOptionPane.showMessageDialog(this, "Xeque!", "Aviso", JOptionPane.WARNING_MESSAGE);
                        statusLabel.setText("Xeque!");
                    }

                    if (game.hasPendingPromotion()) {
                        promotePawn(game.getPendingPromotionPosition());
                        updateBoardDisplay();
                        updateMoveHistory();
                    }

                    if (playAgainstAI && game.isWhiteTurn() == aiPlaysWhite && !game.isGameOver()) {
                        Timer timer = new Timer(500, e -> {
                            ai.makeMove();
                            updateBoardDisplay();
                            updateMoveHistory();
                            turnLabel.setText("Turno: " + (game.isWhiteTurn() ? "Brancas" : "Pretas"));
                            if (game.isInCheck(game.isWhiteTurn())) {
                                JOptionPane.showMessageDialog(this, "Xeque!", "Aviso", JOptionPane.WARNING_MESSAGE);
                                statusLabel.setText("Xeque!");
                            }
                            if (game.hasPendingPromotion()) {
                                promotePawn(game.getPendingPromotionPosition());
                                updateBoardDisplay();
                                updateMoveHistory();
                            }
                            if (game.isGameOver()) {
                                String winner = game.isWhiteTurn() ? "Pretas" : "Brancas";
                                statusLabel.setText("Jogo terminado. " + winner + " vencem!");
                                JOptionPane.showMessageDialog(this, winner + " vencem! Xeque-mate.");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                    if (game.isGameOver()) {
                        String winner = game.isWhiteTurn() ? "Pretas" : "Brancas";
                        statusLabel.setText("Jogo terminado. " + winner + " vencem!");
                        JOptionPane.showMessageDialog(this, winner + " vencem! Xeque-mate.");
                    }
                } else {
                    statusLabel.setText("Movimento inválido. Tente novamente.");
                }
            }
        }
    }

    private void promotePawn(Position pawnPos) {
        String[] options = {"Rainha", "Torre", "Bispo", "Cavalo"};
        String escolha = (String) JOptionPane.showInputDialog(
            this,
            "Escolha a peça para promover:",
            "Promoção de Peão",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        if (escolha != null) {
            game.promotePawnAt(pawnPos, escolha);
        } else {
            game.promotePawnAt(pawnPos, "Rainha");
        }
    }

    private void clearHighlights() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c].setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGUI());
    }
}
