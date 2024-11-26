package scraper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import data.User;
import graph.GraphModel;

public class FollowerFetcher {
	private final WebDriver driver;
    public final GraphModel graph;

    public FollowerFetcher(WebDriver driver, GraphModel graph) {
        this.driver = driver;
        this.graph = graph;
    }

    // Lấy thông tin một KOL từ URL
    private User getUserFromURL(String linkURL) {
        try {
            String name = linkURL.substring(linkURL.lastIndexOf("/") + 1);
            return new User("user_"+name,linkURL);
        } catch (Exception e) {
            System.err.println("Error fetching KOL info: " + e.getMessage());
            return null;
        }
    }

    // Đào followers của KOL
    public void fetchFollowers(String kolLink, int maxFollowers) {
        try {
            driver.get(kolLink);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Lỗi trong quá trình chờ tải trang KOL: " + e.getMessage());
                Thread.currentThread().interrupt();
            }

            User Kol = getUserFromURL(kolLink);
            graph.addVertex(Kol);

            driver.get(kolLink + "/followers");
            try {
                Thread.sleep(3000); // Chờ tải danh sách followers
            } catch (InterruptedException e) {
                System.out.println("Lỗi trong quá trình chờ tải danh sách followers: " + e.getMessage());
                Thread.currentThread().interrupt();
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            int count = 0;

            while (count < maxFollowers) {
                try {
                    List<WebElement> retryElements = driver.findElements(By.xpath("//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]"));                
                    List<WebElement> followers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("button[data-testid='UserCell']")));

                    long currentTime = Instant.now().getEpochSecond();
                    long lastWriteTime = currentTime;

                    if (!followers.isEmpty() && currentTime - lastWriteTime > 300) {
                        System.out.println("Không có dữ liệu mới trong 5 phút. Chương trình tự động dừng.");
                        break;
                    } else if (followers.isEmpty() || (currentTime - lastWriteTime > 15)) {
                        System.out.println("Không có dữ liệu mới trong 15 giây. Đợi 2 phút để tải thêm users...");
                        try {
                            Thread.sleep(120000); // Đợi 2 phút
                        } catch (InterruptedException e) {
                            System.out.println("Lỗi trong quá trình chờ: " + e.getMessage());
                            Thread.currentThread().interrupt();
                        }

                        if (!retryElements.isEmpty()) {
                            try {
                                retryElements.get(0).click();
                                System.out.println("Đã nhấn nút Retry để tải thêm users.");
                            } catch (Exception e) {
                                System.out.println("Lỗi khi nhấn nút Retry: " + e.getMessage());
                            }
                        }
                    }

                    for (WebElement follower : followers) {
                        if (count >= maxFollowers) break;

                        try {
                            List<WebElement> links = follower.findElements(By.cssSelector("a[href*='/']"));
                            if (!links.isEmpty()) {
                                String userProfileUrl = links.get(0).getAttribute("href");
                                User Follower = getUserFromURL(userProfileUrl);
                                if( graph.addVertex(Follower) == true && graph.addEdge(Follower, Kol) == true) {
                                	count++;
                                }
                                System.out.println("COUNT = " + count);
                            } else {
                                WebElement nameElement = follower.findElement(By.cssSelector("span[dir='ltr']"));
                                if (nameElement != null) {
                                    System.out.println("Không tìm thấy liên kết cho người dùng: " + nameElement.getText());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Không thể lấy thông tin của người dùng này: " + e.getMessage());
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
                } catch (Exception e) {
                    System.out.println("Lỗi trong vòng lặp xử lý followers: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
            graph.saveGraphToFile();
        } catch (Exception e) {
            System.out.println("Lỗi trong phương thức fetchFollowers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
        

