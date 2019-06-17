class AIManager {
    final private Heuristic Heuristic = new Heuristic();
    final private RandomAI RandomAI = new RandomAI();
    final private ABP ABP = new ABP();

    //This method is used so the right AI is called to play
    void runAlg(DrawBoard theApp) {
        String currentAlgorithm;
        if(theApp.currentPlayer == 1){
            currentAlgorithm = DrawBoard.blackPlayer;
        }else{
            currentAlgorithm = DrawBoard.redPlayer;
        }
        switch (currentAlgorithm) {
            case "h":
                Heuristic.AIMain(theApp);
                break;
            case "r":
                RandomAI.AIMain(theApp);
                break;
            case "a":
                ABP.AIMain(theApp);
                break;
        }
    }
}
