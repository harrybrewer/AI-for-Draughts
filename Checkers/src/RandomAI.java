import java.util.*;
import java.util.concurrent.TimeUnit;

class RandomAI {

    final private Random rand = new Random();

    //This algorithm will choose a completely random move
    //It will be used for testing purposes
    void AIMain(DrawBoard theApp) throws IllegalArgumentException{
        try {
            TimeUnit.MILLISECONDS.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Move> moves = theApp.gameData.getPiecesCanMove(theApp.currentPlayer);
        int ran = rand.nextInt(moves.size());
        theApp.boardMakeMove(moves.get(ran));
    }
}
