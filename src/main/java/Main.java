import java.sql.SQLException;

void main(String[] args) throws SQLException {
    if (args.length != 2) {
        System.err.println("Zu wenige Parameter übergeben");
        System.exit(1);
    }
    HigherLowerGame higherLowerGame = new HigherLowerGame(args[0], Boolean.parseBoolean(args[1]));
    higherLowerGame.startNewGame();
}
