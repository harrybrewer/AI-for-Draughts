import java.util.*;

class GameData {
    final Piece[][] gamePieces = new Piece[8][8];
    private boolean capture, firstCapture;
    boolean captureAi;
    private Integer blackLost = 0, redLost = 0;
    static private final int
            empty = 0,
            black = 1,
            blackKing = 2,
            red = 3,
            redKing = 4;

    //Sets up the game pieces, that sets up the locations of the pieces and makes each one an object
    void pieceSetUp() {
        int[][] counters = new int[][]{
                {2, 0, 2, 0, 2, 0, 2, 0},
                {0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0},
                {0, 3, 0, 3, 0, 3, 0, 3},
                {3, 0, 3, 0, 3, 0, 3, 0},
                {0, 4, 0, 4, 0, 4, 0, 4}};
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gamePieces[i][j] = new Piece(counters[i][j], null);
            }
        }
    }

    //This function is called so that the game can highlight which
    // pieces of the current player have possible moves this turn
    List<Move> getPiecesCanMove(int currentPlayer) {
        capture = false;
        firstCapture = false;
        captureAi = false;
        List<Move> points = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                findMoves(currentPlayer, points, i, j);
            }
        }
        return points;
    }

    //When given a specific piece it will return all locations that piece can be moved to
    List<Move> getAvailableMoves(Move selected, int currentPlayer) {
        capture = false;
        firstCapture = false;
        captureAi = false;
        List<Move> moves = new ArrayList<>();
        findMoves(currentPlayer, moves, selected.currentRow, selected.currentColumn);
        return moves;
    }

    //Finds all moves that a specific piece can do and returns them
    //This method is used in different ways by different methods
    private void findMoves(int currentPlayer, List<Move> points, int i, int j) {
        //Saves the value of the current players king
        int king;
        if (currentPlayer == black) king = blackKing;
        else king = redKing;

        //Checks for the captures first as if there is a capture it doesn't need to check for movement
        //Checks if the current piece it is looking at is owned by the current player
        if (gamePieces[i][j].getPieceVal() == currentPlayer || gamePieces[i][j].getPieceVal() == king) {
            //For each of the 4 directions a piece can move, the 4 diagonals
            //It runs the capture check, by supplying the current tile and the next two diagonal tiles in one of the directions
            //Also checks that the piece is allowed to move that direction
            if (isCapture(currentPlayer, i, j, i + 1, j + 1, i + 2, j + 2) && gamePieces[i][j].getPieceVal() != 3) {
                //If this is the first capture found, reset the list of points to remove all the just moves
                if (!firstCapture) {
                    points.clear();
                    firstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j + 2));
                capture = true;
                captureAi = true;
            }
            if (isCapture(currentPlayer, i, j, i + 1, j - 1, i + 2, j - 2) && gamePieces[i][j].getPieceVal() != 3) {
                if (!firstCapture) {
                    points.clear();
                    firstCapture = true;
                }
                points.add(new Move(i, j, i + 2, j - 2));
                capture = true;
                captureAi = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j + 1, i - 2, j + 2) && gamePieces[i][j].getPieceVal() != 1) {
                if (!firstCapture) {
                    points.clear();
                    firstCapture = true;
                }
                points.add(new Move(i, j, i - 2, j + 2));
                capture = true;
                captureAi = true;
            }
            if (isCapture(currentPlayer, i, j, i - 1, j - 1, i - 2, j - 2) && gamePieces[i][j].getPieceVal() != 1) {
                if (!firstCapture) {
                    points.clear();
                    firstCapture = true;
                }
                points.add(new Move(i, j, i - 2, j - 2));
                capture = true;
                captureAi = true;
            }
        }

        //If there have been no captures so far
        if (!capture) {
            //Run a similar method that checks if the spaces around the current piece are empty
            if (gamePieces[i][j].getPieceVal() == currentPlayer || gamePieces[i][j].getPieceVal() == king) {
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

    //Check method to see if a piece can take a piece in the direction provided
    private boolean isCapture(int player, int currentRow, int currentColumn, int jumpPieceRow, int jumpPieceColumn, int landRow, int landColumn) {
        //Checks to see if the direction is off the board
        if (landRow < 0 || landRow >= 8 || landColumn < 0 || landColumn >= 8) {
            return false;
        }
        //Checks if location is occupied by another piece
        if (gamePieces[landRow][landColumn].getPieceVal() != empty) {
            return false;
        }

        if (player == black) {
            //If the piece is black and the jump would lead backwards it cant do so
            if (gamePieces[currentRow][currentColumn].getPieceVal() == black && landRow < currentRow) {
                return false;
            }
            //Check if the piece it wants to jump is an opponent piece
            return gamePieces[jumpPieceRow][jumpPieceColumn].getPieceVal() == red || gamePieces[jumpPieceRow][jumpPieceColumn].getPieceVal() == redKing;
        } else {
            //if the piece is red and the jump would lead backwards it cant do so
            if (gamePieces[currentRow][currentColumn].getPieceVal() == red && landRow > currentRow) {
                return false;
            }
            //Check if the piece it wants to jump is an opponent piece
            return gamePieces[jumpPieceRow][jumpPieceColumn].getPieceVal() == black || gamePieces[jumpPieceRow][jumpPieceColumn].getPieceVal() == blackKing;
        }
    }

    //check method to see if the piece can move in the direction provided
    private boolean isMove(int piece, int currentRow, int currentColumn, int destinationRow, int destinationColumn) {
        //Checks to see if direction is off the board
        if (destinationRow < 0 || destinationRow >= 8 || destinationColumn < 0 || destinationColumn >= 8) {
            return false;
        }
        //If the place it wants to move to isn't empty it cant move there
        if (gamePieces[destinationRow][destinationColumn].getPieceVal() != empty) {
            return false;
        }
        //Check to see if the piece is able to move due to the rules of the game
        if (piece == black) {
            return gamePieces[currentRow][currentColumn].getPieceVal() != black || destinationRow >= currentRow;
        } else {
            return gamePieces[currentRow][currentColumn].getPieceVal() != red || destinationRow <= currentRow;
        }
    }


    //Updates the game board with the current move
    void makeMove(int currentRow, int currentColumn, int newRow, int newColumn) {
        //First moves the piece to its new position and replaces the old spot with a blank object
        gamePieces[newRow][newColumn] = gamePieces[currentRow][currentColumn];
        gamePieces[currentRow][currentColumn] = new Piece(0, null);

        //If the move was a capture
        if ((currentRow - newRow == 2) || (currentRow - newRow == -2)) {
            int captureRow = (currentRow + newRow) / 2;
            int captureColumn = (currentColumn + newColumn) / 2;

            //Add to the piece lost totals
            if (gamePieces[captureRow][captureColumn].getPieceVal() == black ||
                    gamePieces[captureRow][captureColumn].getPieceVal() == blackKing) {
                blackLost++;
            } else if (gamePieces[captureRow][captureColumn].getPieceVal() == red ||
                    gamePieces[captureRow][captureColumn].getPieceVal() == redKing) {
                redLost++;
            }
            //Delete the piece that was captured
            gamePieces[captureRow][captureColumn] = new Piece(0, null);
        }

        //Check to see if the piece that was moved should be made into a king and do so if necessary
        if (newRow == 0 && gamePieces[newRow][newColumn].getPieceVal() == red) {
            gamePieces[newRow][newColumn].setPieceVal(redKing);
        }
        if (newRow == 7 && gamePieces[newRow][newColumn].getPieceVal() == black) {
            gamePieces[newRow][newColumn].setPieceVal(blackKing);
        }
    }

    //Getter for the lost pieces
    int getPieceCount(int colour) {
        if (colour == 1) {
            return blackLost;
        } else return redLost;
    }

    void resetPieceCount() {
        blackLost = 0;
        redLost = 0;
    }
}
