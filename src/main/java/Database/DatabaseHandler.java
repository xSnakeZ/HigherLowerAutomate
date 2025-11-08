package Database;

import Entity.Item;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseHandler {
    final Connection connection;
   public DatabaseHandler() throws SQLException {
      this.connection = HigherLowerDB.connect();
   }

   public void close() throws SQLException {
      if (connection != null && !connection.isClosed()) {
         connection.close();
      }
   }

   public void addIfNotExistent(Item item) throws SQLException {
         if (!itemExists(item.getSearchTerm())) {
              var query = "INSERT INTO items (search_term, monthly_searches) VALUES (?, ?);";
              try (var pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, item.getSearchTerm());
                pstmt.setInt(2, item.getMonthlySearches());
                pstmt.executeUpdate();
              }
         }
   }

   public boolean itemExists(String searchTerm) throws SQLException {
       var query = "SELECT 1 FROM items WHERE search_term = ?;";
       try (var pstmt = connection.prepareStatement(query)) {
           pstmt.setString(1, searchTerm);
           var rs = pstmt.executeQuery();
           return rs.next();
       }
   }

   public Item getItemBySearchTerm(String searchTerm) throws SQLException {
       var query = "SELECT search_term, monthly_searches FROM items WHERE search_term = ?;";
       try (var pstmt = connection.prepareStatement(query)) {
           pstmt.setString(1, searchTerm);
           var rs = pstmt.executeQuery();
           if (rs.next()) {
               int monthlySearches = rs.getInt("monthly_searches");
               return new Item(searchTerm, String.valueOf(monthlySearches));
           } else {
               return null;
           }
       }
   }

   public void createTableIfNotExists() throws SQLException {
       var query = "CREATE TABLE IF NOT EXISTS items (" +
               "id SERIAL PRIMARY KEY," +
               "search_term VARCHAR(255) UNIQUE NOT NULL," +
               "monthly_searches INT NOT NULL" +
               ");";
       try (var stmt = connection.createStatement()) {
           stmt.execute(query);
       }
   }
}
