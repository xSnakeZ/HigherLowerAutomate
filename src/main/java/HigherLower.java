import Database.DatabaseHandler;
import LLM.DecisionModel;
import Entity.Item;
import UI.SeleniumHelper;

import java.sql.SQLException;
import java.util.List;

public class HigherLower {

    private final SeleniumHelper seleniumHelper;
    private final DecisionModel decisionModel;
    private final DatabaseHandler databaseHandler;

    public HigherLower() throws SQLException {
        this.seleniumHelper = new SeleniumHelper();
        this.decisionModel = new DecisionModel();
        this.databaseHandler = new DatabaseHandler();
    }

    public void startNewGame() throws SQLException {
        int score = 0;
        boolean gameOver = false;

        System.out.println("Starting new round of HigherLower ...");
        databaseHandler.createTableIfNotExists();
        seleniumHelper.startGame();
        decisionModel.startModel();
        while (!gameOver) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                gameOver = seleniumHelper.isGameOver();
                playRound();
            } catch (RuntimeException e) {
                gameOver = true;
                e.printStackTrace();
            }

            if (!gameOver) {
                score += 1;
                System.out.println("Current score: " + score);
            }
        }
        System.out.println("Game Over! Your final score is: " + score);
        seleniumHelper.close();
        databaseHandler.close();
    }

    private void playRound() throws SQLException {
        boolean isHigher;
        List<Item> items = seleniumHelper.fetchCurrentItems();
        if(items.size() < 2){
            throw new RuntimeException("Not enough items fetched to play a round.");
        }
        databaseHandler.addIfNotExistent(items.getFirst());
        boolean itemExists = databaseHandler.itemExists(items.get(1).getSearchTerm());
        System.out.println("Second Item exists in DB: " + itemExists);
        if (itemExists) {
            System.out.println("Skip decision model");
            Item dbItem = databaseHandler.getItemBySearchTerm(items.get(1).getSearchTerm());
            isHigher = dbItem.getMonthlySearches() > items.get(0).getMonthlySearches();
        } else {
            isHigher = decisionModel.isHigher(items.get(0), items.get(1));
        }
        seleniumHelper.clickItem(isHigher);
    }
}
