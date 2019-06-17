import java.util.*;
import java.util.concurrent.TimeUnit;

class Heuristic {

    final private Random rand = new Random();
    private Piece[][] localBoard;
    private boolean localCapture, localFirstCapture;
    private int aiPlayer, enemyPlayer, humanKing;
    private List<Move> moves;
    static private final int
            empty = 0,
            black = 1,
            blackKing = 2,
            red = 3,
            redKing = 4;

    void AIMain(DrawBoard theApp) {
        //Depending which player the ai is set as, change the base variables
        aiPlayer = theApp.currentPlayer;
        if (aiPlayer == 1) {
            enemyPlayer = 3;
            humanKing = 4;
        } else {
            enemyPlayer = 1;
            humanKing = 2;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Set up the local board and move variables
        localBoard = theApp.gameData.gamePieces;
        moves = theApp.gameData.getPiecesCanMove(aiPlayer);
        if (moves.size() == 1) {
            theApp.boardMakeMove(moves.get(0));
        } else {
            Boolean returned;
            //Rules are laid out by calling them in order
            //If a rule returns true then it has made a move
            //Otherwise continue onto the next rule till a move is made
            for (int i = 0; i <= 1; i++) {
                if (theApp.gameData.captureAi) {

                    //Run rule 1
                    returned = captureEnemyKing(theApp);
                    if (returned) {
                        break;
                    }

                    //Run rule 2
                    returned = doubleMove(theApp);
                    if (returned) {
                        break;
                    }
                }
                //Run rule 3
                returned = movePieceInDanger(theApp);
                if (returned) {
                    break;
                }

                //Run rule 4
                List<Move> temp = moveToSafePos(moves);
                if (temp != null) {
                    moves = temp;
                }
                //Run rule 5
                returned = makeKing(theApp);
                if(returned){
                    break;
                }

                returned = moveAllButBackRow(theApp);
                if (returned) {
                    break;
                }
            }
        }
    }

    //Rule methods
    private Boolean captureEnemyKing(DrawBoard theApp) {
        /** If can capture human king**/
        boolean kingCapture = false;
        Move kingCaptureMove = new Move(0, 0, 0, 0);
        for (Move m : moves) {
            //Finds if the piece that the current move will take is a king
            int row = (m.currentRow + m.newRow) / 2;
            int col = (m.currentColumn + m.newColumn) / 2;
            if (localBoard[row][col].getPieceVal() == humanKing) {
                kingCapture = true;
                kingCaptureMove = m;
            }
        }
        if (kingCapture) {
            theApp.boardMakeMove(kingCaptureMove);
            return true;
        } else {
            return false;
        }
    }

    private Boolean doubleMove(DrawBoard theApp) {
        /**Finds double jumps**/
        Move doubleMove = null;
        boolean doubleMoveBool = false;
        for (Move m : moves) {
            //Checks if current capture can make another capture, if so gives priority to this move
            //Runs the fake move
            localBoard = fakeMove(m, localBoard);
            List<Move> localPoints = new ArrayList<>();
            //Finds the new moves for the piece in question
            findMoves(aiPlayer, localPoints, m.newRow, m.newColumn);
            //If there is a capture found
            if (localCapture) {
                doubleMove = m;
                doubleMoveBool = true;
                localBoard = undoFakeMove(m, localBoard);
                break;
            }
            //Always undo the fake move, or it fucks the game board up
            localBoard = undoFakeMove(m, localBoard);
        }
        if (doubleMoveBool) {
            theApp.boardMakeMove(doubleMove);
            return true;
        } else {
            return false;
        }
    }

    private Boolean movePieceInDanger(DrawBoard theApp) {
        /**Find pieces that are in danger and try to move them to safety**/
        List<Move> localPoints = new ArrayList<>();
        localCapture = false;
        localFirstCapture = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Get the enemy next turn to find which pieces can be captured
                findMoves(enemyPlayer, localPoints, i, j);
            }
        }
        List<Move> piecesInDanger = new ArrayList<>();
        if (localCapture) {
            //Means human can localCapture on their turn
            for (Move m : localPoints) {
                //Add a new move of the piece that is in danger, the move is just to locate the piece later on
                if (m.newRow > m.currentRow) {
                    //moving down the board
                    if (m.newColumn > m.currentColumn) {
                        //Moving right
                        piecesInDanger.add(new Move(m.newRow - 1, m.newColumn - 1, 0, 0));
                    } else if (m.newColumn < m.currentColumn) {
                        //moving left
                        piecesInDanger.add(new Move(m.newRow - 1, m.newColumn + 1, 0, 0));
                    }
                } else if (m.newRow < m.currentRow) {
                    //moving up the board
                    if (m.newColumn > m.currentColumn) {
                        //Moving right
                        piecesInDanger.add(new Move(m.newRow + 1, m.newColumn - 1, 0, 0));
                    } else if (m.newColumn < m.currentColumn) {
                        //moving left
                        piecesInDanger.add(new Move(m.newRow + 1, m.newColumn + 1, 0, 0));
                    }
                }
            }
        }
        if (piecesInDanger.size() > 0) {
            //If the piece that is danger can move this turn add it to a list
            List<Move> newList = new ArrayList<>();
            for (Move m : moves) {
                for (Move m2 : piecesInDanger) {
                    if (m.currentRow == m2.currentRow && m.currentColumn == m2.currentColumn) {
                        newList.add(m);
                    }
                }
            }
            if (newList.size() > 0) {
                List<Move> safeMoves = moveToSafePos(newList);
                if (safeMoves != null) {
                    int ran = rand.nextInt(safeMoves.size());
                    theApp.boardMakeMove(safeMoves.get(ran));
                    return true;
                } else {
                    Boolean result;
                    for (Move move : piecesInDanger) {
                        Piece currentPiece = localBoard[move.currentRow][move.currentColumn];
                        result = protectPiece(currentPiece, move, theApp);
                        if (result) {
                            return true;
                        }
                    }
                    return false;
                    //Piece in danger can move but not to safety
                }
            } else {
                Boolean result;
                for (Move move : piecesInDanger) {
                    Piece currentPiece = localBoard[move.currentRow][move.currentColumn];
                    result = protectPiece(currentPiece, move, theApp);
                    if (result) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            return false;
        }
    }

    private List<Move> moveToSafePos(List<Move> currentMoves) {
        List<Move> nonLethal = new ArrayList<>();
        for (Move m : currentMoves) {
            if (m.newColumn == 7 || m.newColumn == 0 || m.newRow == 7 || m.newRow == 0) {
                nonLethal.add(m);
            } else {
                localBoard = fakeMove(m, localBoard);
                List<Move> enemyMoves = new ArrayList<>();
                localCapture = false;
                localFirstCapture = false;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        //Get the enemy next turn to find which pieces can be captured
                        findMoves(enemyPlayer, enemyMoves, i, j);
                    }
                }
                if (!localCapture) {
                    localBoard = undoFakeMove(m, localBoard);
                    nonLethal.add(m);
                } else {
                    Boolean match = false;
                    for (Move EM : enemyMoves) {
                        int friRow = (EM.currentRow + EM.newRow) / 2;
                        int friCol = (EM.currentColumn + EM.newColumn) / 2;
                        if (m.newRow == friRow && m.newColumn == friCol) {
                            match = true;
                        }
                    }
                    if (!match) {
                        nonLethal.add(m);
                    }
                    localBoard = undoFakeMove(m, localBoard);
                }
            }
        }
        if (nonLethal.size() == 0) {
            return null;
        } else {
            return nonLethal;
        }
    }

    private Boolean makeKing(DrawBoard theApp){
        for(Move m : moves){
            if(theApp.currentPlayer == 1){
                if(m.currentRow == 7){
                    theApp.boardMakeMove(m);
                    return true;
                }
            }else if(theApp.currentPlayer == 3){
                if(m.currentRow == 0){
                    theApp.boardMakeMove(m);
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean moveAllButBackRow(DrawBoard theApp) {
        //Checking if there moves other than on the back row
        int pieceCount = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (localBoard[i][j].getPieceVal() == aiPlayer || localBoard[i][j].getPieceVal() == aiPlayer + 1) {
                    pieceCount++;
                }
            }
        }
        boolean notBackRow = false;
        List<Move> subList = new ArrayList<>();
        if (pieceCount < 5) {
            for (Move m : moves) {
                //Find a piece on the back row that isn't a king
                if (aiPlayer == 3) {
                    if (m.currentRow != 7 || localBoard[m.currentRow][m.currentColumn].getPieceVal() == 4) {
                        notBackRow = true;
                        subList.add(m);
                    }
                } else {
                    if (m.currentRow != 0 || localBoard[m.currentRow][m.currentColumn].getPieceVal() == 2) {
                        notBackRow = true;
                        subList.add(m);
                    }
                }
            }
        }
        //If there are pieces that aren't on the back row then move one of them, else just choose a random move
        if (notBackRow) {
            int ran = rand.nextInt(subList.size());
            theApp.boardMakeMove(subList.get(ran));
            return true;
        } else {
            int ran = rand.nextInt(moves.size());
            theApp.boardMakeMove(moves.get(ran));
            return true;
        }
    }

    //Methods the rules use
    private Piece[][] fakeMove(Move move, Piece[][] localGame) {
        //Method that moves a piece on the local version of the board
        int currentRow = move.currentRow;
        int currentColumn = move.currentColumn;
        int newRow = move.newRow;
        int newColumn = move.newColumn;

        localGame[newRow][newColumn] = localGame[currentRow][currentColumn];
        localGame[currentRow][currentColumn] = new Piece(0, null);

        return localGame;
    }

    private Piece[][] undoFakeMove(Move move, Piece[][] localGame) {
        //Undoes the move made on the local board
        //Is needed or when the ai makes a move,
        //any fake moves that have been made will also be made on the real game board
        int currentRow = move.currentRow;
        int currentColumn = move.currentColumn;
        int newRow = move.newRow;
        int newColumn = move.newColumn;

        localGame[currentRow][currentColumn] = localGame[newRow][newColumn];
        localGame[newRow][newColumn] = new Piece(0, null);
        return localGame;
    }

    private void findMoves(int currentPlayer, List<Move> points, int i, int j) {
        //Saves the value of the current players king
        int king;
        if (currentPlayer == black) king = blackKing;
        else king = redKing;

        if (localBoard[i][j].getPieceVal() == currentPlayer || localBoard[i][j].getPieceVal() == king) {
            if (isCapture(currentPlayer, i, j, i + 1, j + 1, i + 2, j + 2) && localBoard[i][j].getPieceVal() != 3) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j + 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i + 1, j - 1, i + 2, j - 2) && localBoard[i][j].getPieceVal() != 3) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j - 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j + 1, i - 2, j + 2) && localBoard[i][j].getPieceVal() != 1) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i - 2, j + 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j - 1, i - 2, j - 2) && localBoard[i][j].getPieceVal() != 1) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i - 2, j - 2));
                localCapture = true;
            }
        }

        if (!localCapture) {
            if (localBoard[i][j].getPieceVal() == currentPlayer || localBoard[i][j].getPieceVal() == king) {
                if (isMove(currentPlayer, i, j, i + 1, j + 1)) {
                    points.add(new Move(i, j, i + 1, j + 1));
                }
                if (isMove(currentPlayer, i, j, i + 1, j - 1)) {
                    points.add(new Move(i, j, i + 1, j - 1));
                }
                if (isMove(currentPlayer, i, j, i - 1, j + 1)) {
                    points.add(new Move(i, j, i - 1, j + 1));
                }
                if (isMove(currentPlayer, i, j, i - 1, j - 1)) {
                    points.add(new Move(i, j, i - 1, j - 1));
                }
            }
        }
    }

    private boolean isCapture(int player, int currentRow, int currentColumn, int jumpPieceRow, int jumpPieceColumn, int landRow, int landColumn) {
        if (landRow < 0 || landRow >= 8 || landColumn < 0 || landColumn >= 8) {
            return false;
        }
        if (localBoard[landRow][landColumn].getPieceVal() != empty) {
            return false;
        }
        if (player == black) {
            return (localBoard[currentRow][currentColumn].getPieceVal() != black || landRow >= currentRow) && (localBoard[jumpPieceRow][jumpPieceColumn].getPieceVal() == red || localBoard[jumpPieceRow][jumpPieceColumn].getPieceVal() == redKing);
        } else {
            return (localBoard[currentRow][currentColumn].getPieceVal() != red || landRow <= currentRow) && (localBoard[jumpPieceRow][jumpPieceColumn].getPieceVal() == black || localBoard[jumpPieceRow][jumpPieceColumn].getPieceVal() == blackKing);
        }
    }

    private boolean isMove(int piece, int currentRow, int currentColumn, int destinationRow, int destinationColumn) {
        if (destinationRow < 0 || destinationRow >= 8 || destinationColumn < 0 || destinationColumn >= 8) {
            return false;
        }
        if (localBoard[destinationRow][destinationColumn].getPieceVal() != empty) {
            return false;
        }
        if (piece == black) {
            return localBoard[currentRow][currentColumn].getPieceVal() != black || destinationRow >= currentRow;
        } else {
            return localBoard[currentRow][currentColumn].getPieceVal() != red || destinationRow <= currentRow;
        }
    }

    private boolean protectPiece(Piece currentPiece, Move move, DrawBoard theApp) {
        int enemyPiece, enemyKing;
        Move protectPieceLocation;
        if (currentPiece.getPieceVal() == 1 || currentPiece.getPieceVal() == 2) {
            enemyPiece = 3;
            enemyKing = 4;
        } else {
            enemyPiece = 1;
            enemyKing = 2;
        }
        List<String> direction = new ArrayList<>();
        if (localBoard[move.currentRow + 1][move.currentColumn + 1].getPieceVal() == enemyPiece || localBoard[move.currentRow + 1][move.currentColumn + 1].getPieceVal() == enemyKing) {
            direction.add("d");
            direction.add("r");
            protectPieceLocation = new Move(move.currentRow - 1, move.currentColumn - 1, 0, 0);
        } else if (localBoard[move.currentRow + 1][move.currentColumn - 1].getPieceVal() == enemyPiece || localBoard[move.currentRow + 1][move.currentColumn - 1].getPieceVal() == enemyKing) {
            direction.add("d");
            direction.add("l");
            protectPieceLocation = new Move(move.currentRow - 1, move.currentColumn + 1, 0, 0);
        } else if (localBoard[move.currentRow - 1][move.currentColumn + 1].getPieceVal() == enemyPiece || localBoard[move.currentRow - 1][move.currentColumn + 1].getPieceVal() == enemyKing) {
            direction.add("u");
            direction.add("r");
            protectPieceLocation = new Move(move.currentRow + 1, move.currentColumn - 1, 0, 0);
        } else if (localBoard[move.currentRow - 1][move.currentColumn - 1].getPieceVal() == enemyPiece || localBoard[move.currentRow - 1][move.currentColumn - 1].getPieceVal() == enemyKing) {
            direction.add("u");
            direction.add("l");
            protectPieceLocation = new Move(move.currentRow + 1, move.currentColumn + 1, 0, 0);
        } else {
            protectPieceLocation = new Move(0, 0, 0, 0); //Should never happen
        }
        List<Move> checkLocations = new ArrayList<>();
        if (direction.get(0).equals("u")) {
            checkLocations.add(new Move(move.currentRow + 2, move.currentColumn, 0, 0));
            if (direction.get(1).equals("r")) {
                checkLocations.add(new Move(move.currentRow + 2, move.currentColumn - 2, 0, 0));
                checkLocations.add(new Move(move.currentRow, move.currentColumn - 2, 0, 0));
            } else if (direction.get(1).equals("l")) {
                checkLocations.add(new Move(move.currentRow + 2, move.currentColumn + 2, 0, 0));
                checkLocations.add(new Move(move.currentRow, move.currentColumn + 2, 0, 0));
            }
        } else if (direction.get(0).equals("d")) {
            checkLocations.add(new Move(move.currentRow - 2, move.currentColumn, 0, 0));
            if (direction.get(1).equals("r")) {
                checkLocations.add(new Move(move.currentRow - 2, move.currentColumn - 2, 0, 0));
                checkLocations.add(new Move(move.currentRow, move.currentColumn - 2, 0, 0));
            } else if (direction.get(1).equals("l")) {
                checkLocations.add(new Move(move.currentRow - 2, move.currentColumn + 2, 0, 0));
                checkLocations.add(new Move(move.currentRow, move.currentColumn + 2, 0, 0));
            }
        }
        List<Move> checkLocations2 = new ArrayList<>();
        for (Move m : checkLocations) {
            if (m.currentColumn > 7 || m.currentRow > 7 || m.currentColumn < 0 || m.currentRow < 0) {
                continue;
            }
            if (localBoard[m.currentRow][m.currentColumn].getPieceVal() != enemyPiece && localBoard[m.currentRow][m.currentColumn].getPieceVal() != enemyKing && localBoard[m.currentRow][m.currentColumn].getPieceVal() != 0) {
                checkLocations2.add(m);
            }
        }

        if (checkLocations2.size() == 0) {
            return false;
            //No way to block, move on to next piece/rule
        }
        List<Move> newList = new ArrayList<>();
        for (Move m : moves) {
            for (Move m2 : checkLocations2) {
                if (m.currentRow == m2.currentRow && m.currentColumn == m2.currentColumn &&
                        m.newRow == protectPieceLocation.currentRow && m.newColumn == protectPieceLocation.currentColumn) {
                    newList.add(m);
                }
            }
        }
        if (newList.size() > 0) {
            int ran = rand.nextInt(newList.size());
            theApp.boardMakeMove(newList.get(ran));
            return true;
        } else {
            return false;
        }
    }
}