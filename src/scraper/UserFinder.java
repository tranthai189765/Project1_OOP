package scraper;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.ConfigInterface;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;

public class UserFinder implements DataFetcherStrategy {
    private ConfigInterface config;
    private int maxUsers;
    // Constructor nhận vào driver và thông tin đăng nhập
    private FileHandlerInterface fileHandler;
    private String hashtagsFilePath;
    public UserFinder( ConfigInterface config) {
        this.config = config;
        this.maxUsers = config.getMaxUsers();
        this.fileHandler = config.newFileHandler();
        this.hashtagsFilePath = config.getHashTagsFilePath();
    }

    public void findKOLs(WebDriver driver, String hashtag, String outputFilepath) {
        //twitterLogin.login(driver);
        searchHashtag(driver, hashtag);
        //fileHandler.notice(outputFilepath, hashtag, "start");
        Set<String> links = collectUserLinks(driver, hashtag, maxUsers, outputFilepath);
        fileHandler.writeListStringToFile(outputFilepath, links);
        fileHandler.writeStringtoFile(config.getCompletedHashtagsFilePath(), hashtag);
   
        //fileHandler.notice(outputFilepath, hashtag, "end");
    }
    
    public void runWithHashtagsFromFile(WebDriver driver, String filepath, String outputFilepath) {
        Set<String> hashtags = fileHandler.readElementsFromFile(filepath);
        Set<String> completedHashtags = fileHandler.readElementsFromFile(config.getCompletedHashtagsFilePath());
        if (hashtags.isEmpty()) {
            System.out.println("Không tìm thấy hashtag nào trong file.");
            return;
        }

        for (String hashtag : hashtags) {
        	if(completedHashtags.contains(hashtag) == false) {
        		System.out.println("Đang xử lý hashtag: " + hashtag);
                findKOLs(driver, hashtag, outputFilepath);
        	}
        	else {
        		System.out.println("Hashtag "+ hashtag + " đã được xử lý từ trước rồi");
        	}
        }
    }
    
    public void searchHashtag(WebDriver driver, String hashtag) {
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

    private Set<String> collectUserLinks(WebDriver driver, String hashtag, int maxUsers, String ouputFilepath) {
        Set<String> collectedLinks = fileHandler.readElementsFromFile(config.getUsersFoundFilePath());
        //File dailyFile = fileHandler.createDailyFile(hashtag);
        Set<String> recordedLinks = new HashSet<>();
        long lastWriteTime = Instant.now().getEpochSecond();
        long currentTime = Instant.now().getEpochSecond();

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
		    else if (users.isEmpty() || (currentTime - lastWriteTime > 30)) {
		        System.out.println("Không có dữ liệu mới trong 30 giây. Đợi 2 phút để tải thêm users...");
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

		    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1100)");
		    try {
		        Thread.sleep(3000);
		    } catch (InterruptedException e) {
		        System.out.println("Lỗi trong quá trình chờ tải trang: " + e.getMessage());
		        Thread.currentThread().interrupt();
		    }
		}

		
		System.out.println("Đã thu thập và ghi thông tin người dùng thành công.");
        return recordedLinks;
    }

	@Override
	public void fetchUserByHashtagsMultiThreads(int threadCount) {
		// TODO Auto-generated method stub
		String filepath = this.hashtagsFilePath;
		try {
			System.out.println("Đang ở đây");
			List<String> subFilePaths = TwitterFileHandler.splitFile(filepath, threadCount);
			// Tạo thread pool với số threads xác định
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            // Tạo task cho mỗi thread
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                String subFilePath = subFilePaths.get(i);
                executor.submit(() -> {
                    try {
                        // Đọc thông tin đăng nhập cho thread
                        String loginInfoPath = threadIndex + "_logininfo.txt";
                        System.out.println(loginInfoPath);
                        FileHandlerInterface filehandler = config.newFileHandler();
                        Map<String, String> credentials = filehandler.getCredentialsFromFile(loginInfoPath);
                        String username = credentials.get("username");
                        String password = credentials.get("password");
                        String email = credentials.get("email");
                        TwitterLogin twitterLogin = new TwitterLogin(username, password, email, config);
                        WebDriver driver = new ChromeDriver();
                        // Đăng nhập
                        twitterLogin.login(driver);
                       // String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
          
                        // Đăng nhập
                        // Đào users từ file nhỏ
                        String subfilepath = threadIndex + "_UsersFromHashtags.txt";
                        System.out.println("FILE : " + subfilepath);
                        runWithHashtagsFromFile(driver, subFilePath, subfilepath);

                        driver.quit();

                    } catch (Exception e) {
                        System.err.println("Thread " + threadIndex + " gặp lỗi: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            // Đóng ExecutorService sau khi hoàn tất
            executor.shutdown();
            boolean allTasksFinished = executor.awaitTermination(1, TimeUnit.HOURS); // Chờ tối đa 1 giờ
            if (!allTasksFinished) {
                System.err.println("Không phải tất cả các threads hoàn thành trong thời gian quy định.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void fetchProfileMultiThreads(int threadCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchFollowersMultiThreads(int threadCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchTweetsMultiThreads(int threadCount) {
		// TODO Auto-generated method stub
		
	}

	

}