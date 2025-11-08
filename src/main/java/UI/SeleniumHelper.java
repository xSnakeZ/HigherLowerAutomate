package UI;

import Entity.Item;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumHelper {

    private final WebDriver driver;

    public SeleniumHelper(){
        this.driver = new FirefoxDriver();
    }

    public List<Item> fetchCurrentItems() {
        System.out.println("Fetching Current Items");
        List<Item> items = new ArrayList<>();
        List<WebElement> itemElements = this.driver.findElements(By.cssSelector(".pack-term__wrapper"));
        for(WebElement itemElement : itemElements){
            String searchTerm = itemElement.findElement(By.cssSelector(".term-keyword__keyword")).getText();
            String monthlySearches = itemElement.findElement(By.cssSelector(".term-volume__volume")).getText();
            if(!searchTerm.isEmpty()){
                items.add(new Item(searchTerm, monthlySearches));
            }
        }
        if (items.size() >= 2) {
            System.out.println("Fetched Items: " + items.get(0).getSearchTerm() + " (" + items.get(0).getMonthlySearches() + "), " +
                    items.get(1).getSearchTerm() + " (unknown)");
        }

        return items;
    }

    public void clickItem(final boolean isHigher){
        System.out.println("Is higher: " + isHigher);
        WebDriver driver = this.driver;
        if(isHigher){
            WebElement higherButton = driver.findElement(By.cssSelector(".game-button.term-actions__button.term-actions__button--higher"));
            higherButton.click();
        } else {
            WebElement lowerButton = driver.findElement(By.cssSelector(".game-button.term-actions__button.term-actions__button--lower"));
            lowerButton.click();
        }
    }

    public void close(){
        System.out.println("Closing Driver");
        this.driver.quit();
    }

    public boolean isGameOver(){
        WebDriver driver = this.driver;
        List<WebElement> gameElement = driver.findElements(By.cssSelector(".game"));
        return gameElement.isEmpty();
    }

    public void startGame(){
        WebDriver driver = this.driver;
        driver.get("https://www.higherlowergame.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(7000));
        System.out.println("Searching for Start Button ...");
        WebElement startButton = driver.findElement(By.cssSelector(".game-button.game-button--start"));
        startButton.click();
        System.out.println("Start Button Clicked");
    }
}
