class Move {
    final int currentRow, currentColumn;
    final int newRow, newColumn;

    Move(int currentRow, int currentColumn, int newRow, int newColumn) {
        this.currentRow = currentRow;
        this.currentColumn = currentColumn;
        this.newRow = newRow;
        this.newColumn = newColumn;
    }
}

/**
 * This is the object that stores the information for the movement of each piece
 * Each turn it is used to store the information of each piece that can be moved and where it can be moved to
 */
