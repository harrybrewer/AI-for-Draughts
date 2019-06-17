import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.List;

//Paints square grid
class DrawBoard extends JPanel implements MouseListener {

    private static final int squareSize = 80, pieceSize = 60;
    private static final Font gameFont = new Font("TimesRoman", Font.PLAIN, 24);
    private static final Color gameBlack = new Color(0x101010), gameRed = new Color(0xCC0100);
    private int count = 1;
    final GameData gameData;
    private static int turnSinceTake = 0;
    int currentPlayer;
    private boolean gamePlaying;
    private List<Move> canMove, availableMove;
    private Move selected;
    boolean aiPlaying, twoAiPlayer;
    private boolean beenSelected, doubleMoveBool = false;
    final private AIManager AIManager = new AIManager();
    private Move doubleMove;
    int aiPlayer, aiPlayer2;
    static String blackPlayer = "";
    static String redPlayer = "";
    private static int drawCounter = 50;
    private boolean testing = false;

    DrawBoard() {
        addMouseListener(this);
        gameData = new GameData();
        startGame();
        repaint();
    }

    //Resets the variables used during the game
    private void startGame() {
        turnSinceTake = 0;
        if(redPlayer.equals("")){
            redPlayer = "p";
        }
        if(blackPlayer.equals("")){
            blackPlayer = "p";
        }
        gameData.resetPieceCount();
        gameData.pieceSetUp();
        gamePlaying = true;
        beenSelected = false;
    }

    //End game method
    //Will be used so the user can start a new game or return to the main menu
    private void gameOver(int noMoves){
        repaint();
        String data = gameData.getPieceCount(0) + "," + gameData.getPieceCount(1) + ",";
        if(noMoves == 2 || noMoves == 1){
            data += "Draw,\n";
        }else if(gameData.getPieceCount(1) > gameData.getPieceCount(0)){
            data += "Red,\n";
        }else{
            data += "Black,\n";
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("scores.csv", true));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gamePlaying = false;

        if(count <= 50 && testing){
            count++;
            System.out.println(count);
            Opening.frame.setState(Frame.ICONIFIED);
            Opening.frame.setState(Frame.NORMAL);
            startGame();
        }else{
            gameOverUserDisplay(noMoves);
        }
    }

    private void gameOverUserDisplay(int noMoves) {
        Object[] options = {"New Game"};
        String message = "";
        if(noMoves == 2){
            message += "Draw!";
            int response = JOptionPane.showOptionDialog(null, message, "Game over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (response == 0) {
                System.out.println("Restarting");
                Opening.frame.setState(Frame.ICONIFIED);
                Opening.frame.setState(Frame.NORMAL);
                startGame();
            } else if (response == 1) {
                System.out.println("Menu?");
            }
        }else {
            if (noMoves == 1) {
                message += "No valid moves available\n";
            } else if (noMoves == 0) {
                message += "All pieces taken\n";
            }
            if (gameData.getPieceCount(1) > gameData.getPieceCount(0)) {
                message += "Game over, Red won!";
            } else {
                message += "Game over, Black won!";
            }
            int response = JOptionPane.showOptionDialog(null, message, "Game over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (response == 0) {
                System.out.println("Restarting");
                Opening.frame.setState(Frame.ICONIFIED);
                Opening.frame.setState(Frame.NORMAL);
                startGame();
            }
        }
    }

    //Quick check if there are any valid moves a player can make otherwise game over
    private void isGameOver(List<Move> moves) {
        if (moves.size() == 0) {
            gameOver(1);
        }
    }

    //Paint component class
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        Integer currentX = 100;
        Integer currentY = 50;
        //Every repaint the board and all the pieces on it are repainted from scratch
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Paints the game board
                if (i % 2 == j % 2) {
                    g.setColor(new Color(0x663300));
                } else {
                    g.setColor(new Color(0xCC6600));
                }
                g2d.fill(new Rectangle2D.Double(currentX, currentY, squareSize, squareSize));
                //Checks what piece is to be drawn and sets the colour that is appropriate
                if (gameData.gamePieces[i][j].getPieceVal() == 1 || gameData.gamePieces[i][j].getPieceVal() == 2) {
                    g2d.setColor(gameBlack);
                } else if (gameData.gamePieces[i][j].getPieceVal() == 3 || gameData.gamePieces[i][j].getPieceVal() == 4) {
                    g2d.setColor(gameRed);
                } else if (gameData.gamePieces[i][j].getPieceVal() == 0) {
                    g2d.setColor(new Color(0, 0, 0, 0));
                }
                //Each piece is drawn using an ellipse
                Ellipse2D pieceShape = new Ellipse2D.Double(currentX + 10, currentY + 10, pieceSize, pieceSize);
                //This is then added to each piece object so it can be used for drawing the borders later and for the mouse listener
                gameData.gamePieces[i][j].setOval(pieceShape);
                g2d.fill(pieceShape);
                //Checks if the piece is a king, if so adds a K to the piece
                if (gameData.gamePieces[i][j].getPieceVal() == 2 || gameData.gamePieces[i][j].getPieceVal() == 4) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
                    g.drawString("K", currentX + 30, currentY + 50);
                }
                currentX += squareSize;
            }
            currentY += squareSize;
            currentX = 100;
        }

        //Draws a set of pieces that are used to track the number of pieces that have been lost
        g.setFont(gameFont);
        g2d.setColor(gameBlack);
        g.drawString("Lost pieces", 775, 275);
        g2d.fill(new Ellipse2D.Double(800, 300, pieceSize, pieceSize));
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(gameData.getPieceCount(1)), 820, 335);
        g2d.setColor(gameRed);
        g2d.fill(new Ellipse2D.Double(800, 400, pieceSize, pieceSize));
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(gameData.getPieceCount(0)), 820, 435);
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(turnSinceTake), 820, 535);

        //The following is the system that displays the visuals for the selecting of pieces and the movement
        if (gamePlaying) {
            g2d.setColor(Color.white);
            g2d.setStroke(new BasicStroke(2));
            //If the move hasn't been moved yet drawn all the pieces that can be selected
            if (!doubleMoveBool) {
                if (currentPlayer == 3 && !twoAiPlayer){
                    canMove = gameData.getPiecesCanMove(currentPlayer);
                    isGameOver(canMove);
                    for (Move m : canMove) {
                        Ellipse2D oval = gameData.gamePieces[m.currentRow][m.currentColumn].getOval();
                        g2d.draw(oval);
                    }
                } else if (currentPlayer == 1 && !twoAiPlayer) {
                    canMove = gameData.getPiecesCanMove(currentPlayer);
                    isGameOver(canMove);
                    for (Move m : canMove) {
                        Ellipse2D oval = gameData.gamePieces[m.currentRow][m.currentColumn].getOval();
                        g2d.draw(oval);
                    }
                }
            } else {
                if(!twoAiPlayer && currentPlayer != aiPlayer) {
                    canMove.clear();
                    canMove.add(doubleMove);
                    isGameOver(canMove);
                    for (Move m : canMove) {
                        Ellipse2D oval = gameData.gamePieces[m.currentRow][m.currentColumn].getOval();
                        g2d.draw(oval);
                    }
                }
            }
            if ((beenSelected && !aiPlaying) ||(beenSelected && currentPlayer!=aiPlayer)) {
                g2d.setColor(Color.red);
                Ellipse2D moved = gameData.gamePieces[selected.currentRow][selected.currentColumn].getOval();
                g2d.draw(moved);
                availableMove = gameData.getAvailableMoves(selected, currentPlayer);
                for (Move m : availableMove) {
                    Ellipse2D oval = gameData.gamePieces[m.newRow][m.newColumn].getOval();
                    g2d.draw(oval);
                }
            }
            if (aiPlaying && (currentPlayer == aiPlayer || currentPlayer == aiPlayer2)) {
                List<Move> moves = gameData.getPiecesCanMove(currentPlayer);
                if(moves.size() > 0) {
                    AIManager.runAlg(this);
                }else{
                    isGameOver(moves);
                }
            }
        }
    }

    private void pieceClicked(int x, int y) {
        int pieceVal = gameData.gamePieces[x][y].getPieceVal();
        if (pieceVal == 2) { //red
            pieceVal = 1;
        } else if (pieceVal == 4) { //black
            pieceVal = 3;
        }
        if (pieceVal == currentPlayer) {
            for (Move p : canMove) {
                if (p.currentRow == x && p.currentColumn == y) {
                    selected = new Move(x, y, 0, 0);
                    beenSelected = true;
                    repaint();
                }
            }
        } else if (pieceVal == 0) {
            if (availableMove != null) {
                for (Move m : availableMove) {
                    if (m.newRow == x && m.newColumn == y) {
                        boardMakeMove(m);
                    }
                }
            }
        }
    }

    void boardMakeMove(Move move) {
        if(turnSinceTake == drawCounter){
            gameOver(2);
        }
        turnSinceTake +=1;
        doubleMoveBool = false;
        gameData.makeMove(move.currentRow, move.currentColumn, move.newRow, move.newColumn);
        int redLost = gameData.getPieceCount(0);
        int blackLost = gameData.getPieceCount(1);
        if (redLost == 12) {
            gameOver(0);
        } else if (blackLost == 12) {
            gameOver(0);
        }
        boolean firstMoveCap = false;
        if (gameData.captureAi) {
            turnSinceTake = 0;
            firstMoveCap = true;
        }
        if (firstMoveCap) {
            Move newMove = new Move(move.newRow, move.newColumn, 0, 0);
            gameData.getAvailableMoves(newMove, currentPlayer);
            if (gameData.captureAi) {
                doubleMoveBool = true;
                beenSelected = false;
                doubleMove = new Move(move.newRow, move.newColumn, 0, 0);
            }
        }
        if (!doubleMoveBool) {
            if (currentPlayer == 1) {
                currentPlayer = 3;
            } else if (currentPlayer == 3) {
                currentPlayer = 1;
            } else {
                System.out.println("No current player"); //Shouldn't ever happen
            }
            beenSelected = false;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Ellipse2D oval = gameData.gamePieces[i][j].getOval();
                if (event.getButton() == 1) {
                    try {
                        if (oval.contains(event.getX(), event.getY())) {
                            pieceClicked(i, j);
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
