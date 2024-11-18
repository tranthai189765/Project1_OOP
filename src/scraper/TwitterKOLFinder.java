package scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.ConfigInterface;
import filehandler.FileHandlerInterface;

public class TwitterKOLFinder {
 //   private static final String ALL_LINKS_FILE_PATH = "all_user_links.txt";
//    private static final String HASHTAGS_FILE_PATH = "hashtags.txt";
    private WebDriver driver;
    private TwitterLogin twitterLogin;
    private TwitterSearch twitterSearch;
    private final FileHandlerInterface fileHandler;

    // Constructor nhận vào driver và thông tin đăng nhập
    public TwitterKOLFinder(WebDriver driver, String username, String password, String email, ConfigInterface config, FileHandlerInterface fileHandler) {
        this.driver = driver;
        this.twitterLogin = new TwitterLogin(username, password, email, config);
        this.twitterSearch = new TwitterSearch(driver, config);
        this.fileHandler = fileHandler;
    }

    public void findKOLs(String hashtag, int maxUsers) {
        twitterLogin.login(driver);
        twitterSearch.searchHashtag(hashtag);
        fileHandler.notice(fileHandler.getTotalDataFilePath(), hashtag, "start");
        Set<String> links = collectUserLinks(hashtag,maxUsers);
        fileHandler.writeElementsToFile(fileHandler.getTotalDataFilePath(), links);
        fileHandler.notice(fileHandler.getTotalDataFilePath(), hashtag, "end");
    }
    
    public void runWithHashtagsFromFile(int maxUsers) {
        Set<String> hashtags = fileHandler.readElementsFromFile(fileHandler.getModelFilePath());
        if (hashtags.isEmpty()) {
            System.out.println("Không tìm thấy hashtag nào trong file.");
            return;
        }

        for (String hashtag : hashtags) {
            System.out.println("Đang xử lý hashtag: " + hashtag);
            findKOLs(hashtag, maxUsers);
        }
    }

    private Set<String> collectUserLinks(String hashtag, int maxUsers) {
        Set<String> collectedLinks = fileHandler.readElementsFromFile(fileHandler.getTotalDataFilePath());
        File dailyFile = fileHandler.createDailyFile(hashtag);
        Set<String> recordedLinks = new HashSet<>();
        long lastWriteTime = Instant.now().getEpochSecond();
        long currentTime = Instant.now().getEpochSecond();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dailyFile))) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            int count = 0;

            while (count < maxUsers) {
            	List<WebElement> retryElements = driver.findElements(By.xpath("//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]"));

                List<WebElement> users = wait.until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(By.cssSelector("button[data-testid='UserCell']")));
                
                currentTime = Instant.now().getEpochSecond();

                if (!users.isEmpty() && currentTime - lastWriteTime > 300) {
                    System.out.println("Không có dữ liệu mới trong 5 phút. Chương trình tự động dừng.");
                    break;
                }
                else if (users.isEmpty() || (currentTime - lastWriteTime > 15)) {
                    System.out.println("Không có dữ liệu mới trong 15 giây. Đợi 2 phút để tải thêm users...");
                    try {
                        Thread.sleep(120000); // Đợi 2 phút
                    } catch (InterruptedException e) {
                        System.out.println("Lỗi trong quá trình chờ: " + e.getMessage());
                        Thread.currentThread().interrupt();
                    }

                    // Nhấn nút Retry nếu thấy
                    if (!retryElements.isEmpty()) {
                        try {
                            retryElements.get(0).click();
                            System.out.println("Đã nhấn nút Retry để tải thêm users.");
                        } catch (Exception e) {
                            System.out.println("Lỗi khi nhấn nút Retry: " + e.getMessage());
                        }
                    } 
                }

                for (WebElement user : users) {
                    if (count >= maxUsers) break;

                    try {
                        List<WebElement> links = user.findElements(By.cssSelector("a[href*='/']"));
                        if (!links.isEmpty()) {
                            String userProfileUrl = links.get(0).getAttribute("href");

                            if (!collectedLinks.contains(userProfileUrl) && !recordedLinks.contains(userProfileUrl)) {
                                writer.write(userProfileUrl);
                                writer.newLine();
                                recordedLinks.add(userProfileUrl);
                                collectedLinks.add(userProfileUrl);
                                count++;
                                lastWriteTime = Instant.now().getEpochSecond();
                                System.out.println("Count: " + count + "  Đã ghi liên kết của người dùng: " + userProfileUrl);
                            } else {
                                System.out.println("Phát hiện người dùng trùng: " + userProfileUrl);
                            }
                        } else {
                            WebElement nameElement = user.findElement(By.cssSelector("span[dir='ltr']"));
                            if (nameElement != null) {
                                System.out.println("Không tìm thấy liên kết cho người dùng: " + nameElement.getText());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Không thể lấy thông tin của người dùng này.");
                        e.printStackTrace();
                    }
                }

                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800)");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    System.out.println("Lỗi trong quá trình chờ tải trang: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            writer.flush();
            System.out.println("Đã thu thập và ghi thông tin người dùng thành công.");
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
            e.printStackTrace();
        }
        return recordedLinks;
    }
}