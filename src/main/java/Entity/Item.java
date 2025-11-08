package Entity;

public class Item {
    private final int monthlySearches;
    private final String searchTerm;

    public Item(final String searchTerm, final String monthlySearches){
        this.searchTerm = searchTerm.substring(1, searchTerm.length() - 1);
        this.monthlySearches = parseMonthlySearches(monthlySearches);
    }

    public int getMonthlySearches() {
        return monthlySearches;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    private int parseMonthlySearches(String monthlySearchesStr) {
        if(monthlySearchesStr.equals("")){
            return 0;
        }
        String cleaned = monthlySearchesStr.replaceAll("[^\\d]", "");
        return Integer.parseInt(cleaned);
    }
}
