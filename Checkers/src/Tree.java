import java.util.*;

class Tree {
    private Piece[][] board;
    private Move move;
    private Integer score;
    private List<Tree> children;

    Tree(Piece[][] board, Move move, Integer score, Tree ... children) {
        this.board = board;
        this.move = move;
        this.score = score;
        this.children = new ArrayList<>(Arrays.asList(children));
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Move getMove() {
        return move;
    }

    public Integer getScore() {
        return score;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public Tree getChild(int Index){
        return children.get(Index);
    }

    public void addChild(Tree child){
        children.add(child);
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
