import java.awt.geom.Ellipse2D;

class Piece {
    private int pieceVal;
    private Ellipse2D oval;

    Piece(int pieceVal, Ellipse2D oval) {

        this.pieceVal = pieceVal;
        this.oval = oval;
    }

    int getPieceVal() {
        return pieceVal;
    }

    Ellipse2D getOval() {
        return oval;
    }

    void setOval(Ellipse2D oval) {
        this.oval = oval;
    }

    void setPieceVal(int pieceVal) {
        this.pieceVal = pieceVal;
    }
}


/**
 * This object is used to store data on each game piece
 * It stores what piece it is, zero is empty, one is black, two is black king, three is red and four is red king
 * When the board is drawn it also stores the Ellipse of each piece so that the mouse listener can use it as a reference
 * of where the user can click when selecting pieces
 * There are getters and setters for each of the values
 * <p>
 * Originally I used more values that stored the physical x and y of each piece before I added in the Ellipse that did this job better
 * I also stored the location of each piece compared to the board but instead changed this to using a 2d list to store the objects
 */