import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Opening extends JFrame {

    //Creating main
    static Opening frame;
    public static void main(String[] args) {
        frame = new Opening();
        frame.setSize(700, 500);
        frame.setTitle("Draughts");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    //creating variables for the JFrame design
    final JRadioButton onePlayer, twoPlayer, zeroPlayer, blackPiece, redPiece;
    final JRadioButton heuristic, random, ABP, heuristic2, random2, ABP2, heuristic3, random3, ABP3;
    final JPanel titleDisplay, buttDisplay, algDisplay, helpDisplay, twoAlgDisplay;
    static int pieceColour, noPlayers, algorithm, depth;
    final private GridBagConstraints constraints = new GridBagConstraints();
    final DrawBoard game = new DrawBoard();

    private Opening() {
        //3 different panels are made
        //first for the title, second for the first question display
        //and the third for the algorithm choosing display
        titleDisplay = new JPanel();
        buttDisplay = new JPanel();
        algDisplay = new JPanel();
        helpDisplay = new JPanel();
        twoAlgDisplay = new JPanel();
        add(titleDisplay, BorderLayout.NORTH);

        buttDisplay.setLayout(new GridBagLayout());
        algDisplay.setLayout(new GridBagLayout());
        helpDisplay.setLayout(new GridBagLayout());
        twoAlgDisplay.setLayout(new GridBagLayout());
        add(buttDisplay);

        //first display page contents
        titleDisplay.add(new JLabel("Draughts"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        buttDisplay.add(new JLabel("Please choose how many noPlayers there are"), consSet(0, 0, 3));

        //using radio buttons for the number of noPlayers and the piece colour decisions
        onePlayer = new JRadioButton("One Player");
        twoPlayer = new JRadioButton("Two Player");
        zeroPlayer = new JRadioButton("Zero Player");
        ButtonGroup group = new ButtonGroup();
        group.add(onePlayer);
        group.add(twoPlayer);
        group.add(zeroPlayer);
        //twoPlayer.setSelected(true);
        onePlayer.setSelected(true);
        buttDisplay.add(onePlayer, consSet(0, 1, 1));
        buttDisplay.add(twoPlayer, consSet(1, 1, 1));
        buttDisplay.add(zeroPlayer, consSet(2, 1, 1));

        //Sets up the choosing for which player is to start
        buttDisplay.add(new JLabel("Choose the colour of player 1"), consSet(0, 2, 3));
        blackPiece = new JRadioButton("Black");
        redPiece = new JRadioButton("Red");
        ButtonGroup group2 = new ButtonGroup();
        group2.add(blackPiece);
        group2.add(redPiece);
        redPiece.setSelected(true);
        buttDisplay.add(redPiece, consSet(0, 3, 1));
        buttDisplay.add(blackPiece, consSet(1, 3, 1));

        JButton nextMenu = new JButton("Next");
        buttDisplay.add(nextMenu, consSet(0, 4, 1));
        //using button handlers for the next button, matched with an action for each different button
        ButtonHandler nextBut = new ButtonHandler(0, this);
        nextMenu.addActionListener(nextBut);

        JButton helpButton = new JButton("Help Menu");
        buttDisplay.add(helpButton, consSet(2, 4, 1));
        ButtonHandler helpBut = new ButtonHandler(3, this);
        helpButton.addActionListener(helpBut);

        //second page display contents
        //using the same methods of display as the first page
        //Displays the different options for Heuristic for the user to choose from
        //Listed Heuristic are just placeholders and my not reflect the final algorithms used
        constraints.fill = GridBagConstraints.HORIZONTAL;
        algDisplay.add(new JLabel("Please choose the algorithm you wish to face"), consSet(0, 0, 3));
        heuristic = new JRadioButton("Heuristic");
        random = new JRadioButton("Random");
        ABP = new JRadioButton("ABP");
        ButtonGroup group3 = new ButtonGroup();
        group3.add(heuristic);
        group3.add(random);
        group3.add(ABP);
        heuristic.setSelected(true);
        algDisplay.add(heuristic, consSet(1, 1, 1));
        algDisplay.add(random, consSet(1, 2, 1));
        algDisplay.add(ABP, consSet(1,3,1));

        //Sets up the two buttons on the algorithm screen
        JButton nextAlg = new JButton("Next");
        algDisplay.add(nextAlg, consSet(2, 4, 1));
        ButtonHandler nextAlgHand = new ButtonHandler(1, this);
        nextAlg.addActionListener(nextAlgHand);
        JButton backAlg = new JButton("Back");
        algDisplay.add(backAlg, consSet(0, 4, 1));
        ButtonHandler backAlgHand = new ButtonHandler(2, this);
        backAlg.addActionListener(backAlgHand);

        //Help menu JPanel setup
        constraints.fill = GridBagConstraints.CENTER;
        helpDisplay.add(new JLabel("Help Menu"), consSet(0, 0, 1));
        helpDisplay.add(new JLabel("How to play"), consSet(0,1,1));
        helpDisplay.add(new JLabel(" "), consSet(0,2,1));
        JTextArea ruleText = new JTextArea("- On your turn click on one of your pieces that has a white circle around it\n" +
                "- The locations that a piece can go will be highlighted in red, click on one to move there\n" +
                "- If a piece can do multiple jumps, once it has been moved the first time you will be able to select the next place to jump\n");
        helpDisplay.add(ruleText, consSet(0,3,1));
        helpDisplay.add(new JLabel("Game rules"), consSet(0,4,1));
        helpDisplay.add(new JLabel(" "), consSet(0,5,1));
        ruleText = new JTextArea("- Each turn one piece can be moved\n" +
                "- There are two types of pieces, kings and standard\n" +
                "- A standard piece can only move and take pieces forward, a king can move up and down the board\n" +
                "- If a capture can be made the player must do so\n" +
                "- To make a standard piece into a king piece, it needs to be moved to the enemy's back row\n");
        helpDisplay.add(ruleText,  consSet(0,6,1));
        helpDisplay.add(new JLabel(" "), consSet(0,5,1));
        JButton backHelp = new JButton("Return");
        helpDisplay.add(backHelp, consSet(0, 7, 1));
        ButtonHandler backHelpHand = new ButtonHandler(4, this);
        backHelp.addActionListener(backHelpHand);



        constraints.fill = GridBagConstraints.HORIZONTAL;
        twoAlgDisplay.add(new JLabel("Please choose two algorithms"), consSet(0, 0, 2));
        twoAlgDisplay.add(new JLabel("Choose one from each column"), consSet(0, 1, 2));
        ButtonGroup group4 = new ButtonGroup();
        heuristic3 = new JRadioButton("Heuristic");
        random3 = new JRadioButton("Random");
        ABP3 = new JRadioButton("ABP");
        group4.add(heuristic3);
        group4.add(random3);
        group4.add(ABP3);
        heuristic3.setSelected(true);
        twoAlgDisplay.add(new JLabel("Black"), consSet(0, 2, 1));
        twoAlgDisplay.add(heuristic3, consSet(0, 3, 1));
        twoAlgDisplay.add(random3, consSet(0, 4, 1));
        twoAlgDisplay.add(ABP3, consSet(0,5,1));

        ButtonGroup group5 = new ButtonGroup();
        heuristic2 = new JRadioButton("Heuristic");
        random2 = new JRadioButton("Random");
        ABP2 = new JRadioButton("ABP");
        group5.add(heuristic2);
        group5.add(random2);
        group5.add(ABP2);
        heuristic2.setSelected(true);
        twoAlgDisplay.add(new JLabel("Red"), consSet(2, 2, 1));
        twoAlgDisplay.add(heuristic2, consSet(2, 3, 1));
        twoAlgDisplay.add(random2, consSet(2, 4, 1));
        twoAlgDisplay.add(ABP2, consSet(2,5,1));
        twoAlgDisplay.add(new JLabel(" "), consSet(0,2,1));

        //Sets up the two buttons on the algorithm screen
        JButton nextTwoAlg = new JButton("Next");
        twoAlgDisplay.add(nextTwoAlg, consSet(2, 6, 1));
        ButtonHandler nextTwoAlgHand = new ButtonHandler(5, this);
        nextTwoAlg.addActionListener(nextTwoAlgHand);
        JButton backTwoAlg = new JButton("Back");
        twoAlgDisplay.add(backTwoAlg, consSet(0, 6, 1));
        ButtonHandler backTwoAlgHand = new ButtonHandler(6, this);
        backTwoAlg.addActionListener(backTwoAlgHand);
    }

    //This method is used to set the layout of the buttons on the GUI
    private GridBagConstraints consSet(int x, int y, int width) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        return constraints;
    }
}

class ButtonHandler implements ActionListener {
    final private Opening theApp;
    final private int action;

    ButtonHandler(int action, Opening theApp) {
        this.theApp = theApp;
        this.action = action;
    }

    public void actionPerformed(ActionEvent e) {
        setGameVariables();
        //using the action to determine what the button is meant to do
        //the first action is the first next button on the first screen
        switch (action) {
            case 0:
                playerSelected();
                break;
            //the second action is the next button on the second menu
            case 1:
                //currently just displays a message depending on which option is chosen, will be changed when it is time to implement the algorithms
                algorithmSelect();
                break;
            //the third action is the back button the second menu
            case 2:
                //removes the algorithm panel and loads the title and first panel
                theApp.remove(theApp.algDisplay);
                theApp.add(theApp.titleDisplay, BorderLayout.NORTH);
                theApp.add(theApp.buttDisplay, BorderLayout.CENTER);
                theApp.setPreferredSize(new Dimension(700, 500));
                theApp.pack();
                theApp.repaint();
                break;
            case 3:
                theApp.remove(theApp.buttDisplay);
                theApp.remove(theApp.titleDisplay);
                theApp.add(theApp.helpDisplay);
                theApp.setPreferredSize(new Dimension(700, 500));
                theApp.pack();
                theApp.repaint();
                break;
            case 4:
                theApp.remove(theApp.helpDisplay);
                theApp.add(theApp.titleDisplay, BorderLayout.NORTH);
                theApp.add(theApp.buttDisplay, BorderLayout.CENTER);
                theApp.setPreferredSize(new Dimension(700, 500));
                theApp.pack();
                theApp.repaint();
                break;
            case 5:
                if(theApp.heuristic3.isSelected()){ DrawBoard.blackPlayer = "h";
                }else if(theApp.random3.isSelected()){ DrawBoard.blackPlayer = "r";
                }else if(theApp.ABP3.isSelected()){ DrawBoard.blackPlayer = "a";}

                if(theApp.heuristic2.isSelected()){ DrawBoard.redPlayer = "h";
                }else if(theApp.random2.isSelected()){ DrawBoard.redPlayer = "r";
                }else if(theApp.ABP2.isSelected()){ DrawBoard.redPlayer = "a"; }

                if(theApp.ABP2.isSelected() || theApp.ABP3.isSelected()){
                    Opening.depth = Integer.parseInt(JOptionPane.showInputDialog("Enter the search depth, enter even numbers only (6 is quite high)"));
                }
                theApp.remove(theApp.buttDisplay);
                theApp.remove(theApp.titleDisplay);
                theApp.remove(theApp.twoAlgDisplay);
                theApp.add(theApp.game);
                theApp.setPreferredSize(new Dimension(1000, 750));
                theApp.game.aiPlaying = true;
                theApp.game.twoAiPlayer = true;
                theApp.game.aiPlayer = 1;
                theApp.game.aiPlayer2 = 3;
                theApp.game.currentPlayer = Opening.pieceColour;
                theApp.pack();
                theApp.repaint();
                break;
            case 6:
                theApp.remove(theApp.twoAlgDisplay);
                theApp.add(theApp.titleDisplay, BorderLayout.NORTH);
                theApp.add(theApp.buttDisplay, BorderLayout.CENTER);
                theApp.setPreferredSize(new Dimension(700, 500));
                theApp.pack();
                theApp.repaint();
                break;
        }
    }

    private void algorithmSelect() {
        //Loads the game display and sets it up for a game with AI to be played
        theApp.remove(theApp.buttDisplay);
        theApp.remove(theApp.titleDisplay);
        theApp.remove(theApp.algDisplay);
        theApp.add(theApp.game);
        theApp.setPreferredSize(new Dimension(1000, 750));
        theApp.game.aiPlaying = true;
        theApp.game.currentPlayer = Opening.pieceColour;
        if(theApp.game.currentPlayer == 1){
            theApp.game.aiPlayer = 3;
        }else{
            theApp.game.aiPlayer = 1;
        }
        theApp.pack();
        theApp.repaint();
    }

    private void playerSelected() {
        //playerSelected is used to decide if the game should start in the case of 2 player selected
        //or to show the options for AIs for the user to choose from
        //Each option simply removes the panels that are not wanted and adds the ones that are needed
        //two player being the simplest will just start an instance of the game
        if (theApp.twoPlayer.isSelected()) {
            theApp.remove(theApp.buttDisplay);
            theApp.remove(theApp.titleDisplay);
            theApp.add(theApp.game);
            theApp.setPreferredSize(new Dimension(1000, 750));
            theApp.game.currentPlayer = Opening.pieceColour;
            theApp.pack();
            theApp.repaint();
        }
        //one player loads the next menu for the player to choose which algorithm they will face
        else if (theApp.onePlayer.isSelected()) {
            //removes the main display and loads the title and algorithm displays
            theApp.remove(theApp.buttDisplay);
            theApp.add(theApp.algDisplay, BorderLayout.CENTER);
            theApp.setPreferredSize(new Dimension(700, 500));
            theApp.pack();
            theApp.repaint();
        }
        //zero player will be implemented once the game has been designed
        else if (theApp.zeroPlayer.isSelected()) {
            theApp.remove(theApp.buttDisplay);
            theApp.add(theApp.twoAlgDisplay, BorderLayout.CENTER);
            theApp.setPreferredSize(new Dimension(700, 500));
            theApp.pack();
            theApp.repaint();
        }
    }

    private void setGameVariables() {
        //This is a method that is called that sets the current values of each of the sets of radio buttons
        //so the different classes can use the data to set up the game
        if (theApp.blackPiece.isSelected()) Opening.pieceColour = 1;
        else if (theApp.redPiece.isSelected()) Opening.pieceColour = 3;

        if (theApp.onePlayer.isSelected()) Opening.noPlayers = 1;
        else if (theApp.twoPlayer.isSelected()) Opening.noPlayers = 2;
        else if (theApp.zeroPlayer.isSelected()) Opening.noPlayers = 0;

        if (theApp.heuristic.isSelected()) {
            Opening.algorithm = 1;
            if(Opening.pieceColour == 1){
                DrawBoard.redPlayer = "h";
            }else{
                DrawBoard.blackPlayer = "h";
            }
        }
        else if (theApp.random.isSelected()){
            Opening.algorithm = 2;
            if(Opening.pieceColour == 1){
                DrawBoard.redPlayer = "r";
            }else{
                DrawBoard.blackPlayer = "r";
            }
        }
        else if (theApp.ABP.isSelected()){
            Opening.depth = Integer.parseInt(JOptionPane.showInputDialog("Enter the search depth, enter even numbers only (6 is quite high)"));
            Opening.algorithm = 2;
            if(Opening.pieceColour == 1){
                DrawBoard.redPlayer = "a";
            }else{
                DrawBoard.blackPlayer = "a";
            }
        }
        else {
            Opening.algorithm = 0;
        }
    }
}

/**
 * The opening class is the GUI for the game, it what the user interacts with to choose how they wish the game to run
 * It uses multiple sets of JPanels to show different options and to store the game itself on
 */