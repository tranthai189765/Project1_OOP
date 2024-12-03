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

import entities.User;

import manager.DataManagerInterface;

public class KOLFollowerFetcher implements DataFetcherStrategy{
	private final WebDriver driver;
	private final DataManagerInterface manager;
	private final int maxFollowers;

    public KOLFollowerFetcher(WebDriver driver, DataManagerInterface manager, int maxFollowers) {
        this.driver = driver;
        this.manager = manager;
        this.maxFollowers = maxFollowers;
    }

	@Override
	public void fetchProfile(User kol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchFollowers(User kol) {
		System.out.println("Fetching KOL followers...");
		manager.addUserToDataBase(kol);
		 try {
	            driver.get(kol.getUrl());
	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	                System.out.println("Lỗi trong quá trình chờ tải trang KOL: " + e.getMessage());
	                Thread.currentThread().interrupt();
	            }
	            
	            driver.get(kol.getUrl() + "/followers");
	            
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
	                                User user = new User(userProfileUrl);
	                  
	          
	                                if( kol.hasFollower(user) == false) {
	                                	kol.addFollower(user);
	                                	count++;
	                                	lastWriteTime = Instant.now().getEpochSecond();
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
	            manager.updateFollowersForUser(kol.getId(), kol.getFollowers());
	            manager.saveToJsonFile("te.json");
	        } catch (Exception e) {
	            System.out.println("Lỗi trong phương thức fetchFollowers: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	@Override
	public void fetchTweets(User kol) {
		// TODO Auto-generated method stub
		
	}


}
