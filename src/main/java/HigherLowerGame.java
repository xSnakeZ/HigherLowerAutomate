import Database.DatabaseHandler;
import LLM.DecisionModel;
import Entity.Item;
import LLM.OllamaClient;
import UI.SeleniumHelper;

import java.sql.SQLException;
import java.util.List;

public class HigherLowerGame {

    private final SeleniumHelper seleniumHelper;
    private final DecisionModel decisionModel;
    private final DatabaseHandler databaseHandler;
    private final Boolean useDatabase;

    public HigherLowerGame(final String model, final boolean useDatabase) throws SQLException {
        this.seleniumHelper = new SeleniumHelper();
        this.decisionModel = new DecisionModel(new OllamaClient(), model);
        this.databaseHandler = new DatabaseHandler();
        this.useDatabase = useDatabase;
    }

    public void startNewGame() throws SQLException {
        int score = 0;
        boolean gameOver = false;

        if (useDatabase) {
            System.out.println("Using Database for previous decisions");
            System.out.println("Starting Database Initialization ...");
            databaseHandler.createTableIfNotExists();
        } else {
            System.out.println("Not using Database for previous decisions");
        }
        System.out.println("Starting Decision Model ...");
        decisionModel.startModel();
        System.out.println("Starting new round of HigherLower ...");
        seleniumHelper.startGame();
        while (!gameOver) {
            try {
                Thread.sleep(5000);
                playRound(useDatabase);
                gameOver = seleniumHelper.isGameOver();
            } catch (RuntimeException | InterruptedException e) {
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
        if (useDatabase) {
            databaseHandler.close();
        }
    }

    private void playRound(final boolean useDatabase) throws SQLException {
        boolean isHigher;
        boolean itemExists = false;
        List<Item> items = seleniumHelper.fetchCurrentItems();
        if(items.size() < 2){
            throw new RuntimeException("Not enough items fetched to play a round.");
        }
        if (useDatabase) {
            databaseHandler.addIfNotExistent(items.getFirst());
            itemExists = databaseHandler.itemExists(items.get(1).getSearchTerm());
            System.out.println("Second Item exists in DB: " + itemExists);
        }
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
