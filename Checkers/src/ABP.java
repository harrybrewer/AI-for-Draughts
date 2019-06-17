import java.util.*;
import java.util.concurrent.TimeUnit;

public class ABP {
    private int maxPlayer, minPlayer;
    private boolean localCapture, localFirstCapture;
    private int count;
    static private final int
            empty = 0,
            black = 1,
            blackKing = 2,
            red = 3,
            redKing = 4;

    void AIMain(DrawBoard theApp) {
        try {
            TimeUnit.MILLISECONDS.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int maxDepth = Opening.depth;
        if(maxDepth % 2 != 0){
            maxDepth++;
        }
        Piece[][] localBoard = theApp.gameData.gamePieces;
        maxPlayer = theApp.currentPlayer;
        if (maxPlayer == 1) {
            minPlayer = 3;
        } else {
            minPlayer = 1;
        }
        Tree decisionTree = new Tree(localBoard, null, null);
        List<Move> moves = new ArrayList<>();
        moves = getMoves(maxPlayer, moves, localBoard);
        makeDecisionTree(decisionTree, moves, 0, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        Tree best = null;
        count = 0;
        if (decisionTree.getChildren().size() == 1) {
            //System.out.println("1 child");
            theApp.boardMakeMove(decisionTree.getChild(0).getMove());
        } else {
            decisionTree = searchTree(0, decisionTree);
            int max = Integer.MIN_VALUE;
            if (decisionTree != null) {
                for(Tree t : decisionTree.getChildren()){
                    if (max == Integer.MIN_VALUE) {
                        max = t.getScore();
                        best = t;
                    } else if (t.getScore() >= max) {
                        max = t.getScore();
                        best = t;
                    }
                }
            }
            System.out.println(count);
            if(best != null){
                theApp.boardMakeMove(best.getMove());
            }else{
                System.out.println("Best is Null");
            }
        }
    }

    private Tree searchTree(int depth, Tree tree){
        count++;
        if(tree.getChildren().size() < 1){
            return null;
        }
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for(Tree t : tree.getChildren()){
            searchTree(depth+1,t);
            if(depth % 2 == 0){
                //Max
                if (max == Integer.MIN_VALUE) {
                    max = t.getScore();
                } else if (t.getScore() >= max) {
                    max = t.getScore();
                }
            }else{
                //min
                if (min == Integer.MAX_VALUE) {
                    min = t.getScore();
                } else if (t.getScore() <= min) {
                    min = t.getScore();
                }
            }
        }
        if(depth % 2 == 0) {
            tree.setScore(max);
        }else{
            tree.setScore(min);
        }
        return tree;
    }

    private void makeDecisionTree(Tree layer, List<Move> moves, int depth, int depthLimit, int alpha, int beta) {
        for (Move move : moves) {
            Piece[][] temp = dupBoard(layer.getBoard());
            //Generate the next GameBoard after the current is made
            temp = fakeMove(move, temp);
            List<Move> nextMoves = new ArrayList<>();
            //If it is a even depth, i.e. maximum depth
            if(depth % 2 == 0){
                //Generate a new tree and its score
                Tree nextLayer = new Tree(temp, move, scoreBoard(temp, maxPlayer));
                nextMoves = getMoves(minPlayer, nextMoves, temp);
                //If the score just generated is more than Alpha overwrite it
                if (nextLayer.getScore() > alpha) alpha = nextLayer.getScore();
                // Alpha is more than Beta don't go any deeper
                if (alpha >= beta){
                    layer.addChild(nextLayer);
                    continue;
                }
                if (depth < depthLimit) {
                    //If the depth limit hasn't been reached continue going deeper
                    makeDecisionTree(nextLayer, nextMoves, depth+1, depthLimit, alpha, beta);
                }
                //Add the current tree being made to the one above
                layer.addChild(nextLayer);
            }
            //Else it is a odd depth, i.e. minimum depth
            //Works the same as above but Alpha and Beta are swapped
            else {
                Tree nextLayer = new Tree(temp, move, scoreBoard(temp, minPlayer));
                nextMoves = getMoves(maxPlayer, nextMoves, temp);
                //If the score just generated is less than Beta overwrite it
                if (nextLayer.getScore() < beta) beta = nextLayer.getScore();
                if (alpha >= beta){
                    layer.addChild(nextLayer);
                    continue;
                }
                if (depth < depthLimit) {
                    makeDecisionTree(nextLayer, nextMoves, depth+1, depthLimit, alpha, beta);
                }
                layer.addChild(nextLayer);
            }
        }
    }

    private List<Move> getMoves(int currentPlayer, List<Move> points, Piece[][] localBoard) {
        localCapture = false;
        localFirstCapture = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Get the enemy next turn to find which pieces can be captured
                findMoves(currentPlayer, points, i, j, localBoard);
            }
        }
        return points;
    }

    private void findMoves(int currentPlayer, List<Move> points, int i, int j, Piece[][] localBoard) {
        //Saves the value of the current players king
        int king;
        if (currentPlayer == black) king = blackKing;
        else king = redKing;

        if (localBoard[i][j].getPieceVal() == currentPlayer || localBoard[i][j].getPieceVal() == king) {
            if (isCapture(currentPlayer, i, j, i + 1, j + 1, i + 2, j + 2, localBoard) && localBoard[i][j].getPieceVal() != 3) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j + 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i + 1, j - 1, i + 2, j - 2, localBoard) && localBoard[i][j].getPieceVal() != 3) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j - 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j + 1, i - 2, j + 2, localBoard) && localBoard[i][j].getPieceVal() != 1) {
                if (!localFirstCapture) {
                    points.clear();
                    localFirstCapture = true;
                }
                points.add(new Move(i, j, i - 2, j + 2));
                localCapture = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j - 1, i - 2, j - 2, localBoard) && localBoard[i][j].getPieceVal() != 1) {
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
                if (isMove(currentPlayer, i, j, i + 1, j + 1, localBoard)) {
                    points.add(new Move(i, j, i + 1, j + 1));
                }
                if (isMove(currentPlayer, i, j, i + 1, j - 1, localBoard)) {
                    points.add(new Move(i, j, i + 1, j - 1));
                }
                if (isMove(currentPlayer, i, j, i - 1, j + 1, localBoard)) {
                    points.add(new Move(i, j, i - 1, j + 1));
                }
                if (isMove(currentPlayer, i, j, i - 1, j - 1, localBoard)) {
                    points.add(new Move(i, j, i - 1, j - 1));
                }
            }
        }
    }

    private boolean isCapture(int player, int currentRow, int currentColumn, int jumpPieceRow, int jumpPieceColumn, int landRow, int landColumn, Piece[][] localBoard) {
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

    private boolean isMove(int piece, int currentRow, int currentColumn, int destinationRow, int destinationColumn, Piece[][] localBoard) {
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

    private Piece[][] fakeMove(Move move, Piece[][] localGame) {
        //Method that moves a piece on the local version of the board
        int currentRow = move.currentRow;
        int currentColumn = move.currentColumn;
        int newRow = move.newRow;
        int newColumn = move.newColumn;

        if (currentRow - newRow == -1 || currentRow - newRow == 1) {
            localGame[newRow][newColumn] = localGame[currentRow][currentColumn];
            localGame[currentRow][currentColumn] = new Piece(0, null);
        } else {
            localGame[newRow][newColumn] = localGame[currentRow][currentColumn];
            localGame[currentRow][currentColumn] = new Piece(0, null);
            int row = (currentRow + newRow) / 2;
            int col = (currentColumn + newColumn) / 2;
            localGame[row][col] = new Piece(0, null);
        }

        return localGame;
    }

    private int scoreBoard(Piece[][] localBoard, int player) {
        int black = 0, red = 0;
        int redCount = 0, redKingCount = 0, blackCount = 0, blackKingCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(localBoard[i][j].getPieceVal() == 1){
                    blackCount+=10;
                    if (j == 0 || j == 7) {
                        black += 5;
                    }
                }else if(localBoard[i][j].getPieceVal() == 2){
                    blackKingCount+=10;
                    if (j == 0 || j == 7) {
                        black += 5;
                    }
                }else if(localBoard[i][j].getPieceVal() == 3){
                    redCount+=10;
                    if (j == 0 || j == 7) {
                        red += 5;
                    }
                }else if(localBoard[i][j].getPieceVal() == 4){
                    redKingCount+=10;
                    if (j == 0 || j == 7) {
                        red += 5;
                    }
                }
            }
        }

        red +=  redCount + (3 * redKingCount);
        black += blackCount + (3 * blackKingCount);

        if (player == 1) {
            return black - red;
        } else {
            return red - black;
        }
    }

    private Piece[][] dupBoard(Piece[][] localBoard) {
        Piece[][] temp = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                temp[i][j] = new Piece(localBoard[i][j].getPieceVal(), null);
            }
        }
        return temp;
    }
}