package scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.ConfigInterface;

import java.time.Duration;

public class TwitterSearch {

    private WebDriver driver;
    private final ConfigInterface config;

    public TwitterSearch(WebDriver driver, ConfigInterface config) {
        this.driver = driver;
        this.config = config;
    }

    public void searchHashtag(String hashtag) {
        driver.get(config.getExploreUrl());

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            
            // Tìm kiếm ô nhập hashtag
            WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[aria-label='Search query']"))
            );
            searchBox.sendKeys(hashtag);
            searchBox.submit();

            // Chuyển đến tab "People"
            WebElement peopleTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(config.getPeopleTabXpath())));
            peopleTab.click();

            // Kiểm tra URL để xác nhận điều hướng thành công
            wait.until(ExpectedConditions.urlContains("f=user"));
            System.out.println("Tìm kiếm với hashtag " + hashtag + " thành công.");

        } catch (Exception e) {
            System.out.println("Không tìm thấy ô tìm kiếm hoặc xảy ra lỗi trong quá trình tìm kiếm.");
            e.printStackTrace();
        }
    }
}
