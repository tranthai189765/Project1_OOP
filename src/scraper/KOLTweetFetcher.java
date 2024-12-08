package scraper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.ConfigInterface;
import config.TwitterConfig;
import utils.utils;

import entities.User;
import entities.Tweet;

import manager.DataManagerInterface;
import manager.TwitterDataManager;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;

public class KOLTweetFetcher implements DataFetcherStrategy {
	private final WebDriver driver;
	private final DataManagerInterface manager;
	private final int maxTweets;
	private final int maxComments;
	private final FileHandlerInterface fileHandler;

    public KOLTweetFetcher(WebDriver driver, DataManagerInterface manager, int maxTweets, int maxComments, FileHandlerInterface fileHandler) {
        this.driver = driver;
        this.manager = manager;
        this.maxTweets = maxTweets;
        this.maxComments = maxComments;
        this.fileHandler = fileHandler;
    }

	@Override
	public void fetchProfile(User kol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchFollowers(User kol) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void fetchTweets(User kol) {
        System.out.println("Fetching KOL profile...");
        manager.addUserToDataBase(kol);
        try {
            driver.get(kol.getUrl());
            Thread.sleep(3000); // Đợi tải trang
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");

            int count = 0;
            int stagnantScrollCount = 0;
            long lastScrollHeight = 0;
            long lastWriteTime = Instant.now().getEpochSecond();  // Lưu thời gian lần viết gần nhất

            while (count < maxTweets) {
                try {
                    List<WebElement> retryElements = driver.findElements(By.xpath("//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]"));
                    List<WebElement> tweets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article[data-testid='tweet']")));

                    long currentTime = Instant.now().getEpochSecond();

                    if (!tweets.isEmpty() && currentTime - lastWriteTime > 300) {
                        System.out.println("Không có dữ liệu mới trong 5 phút. Chương trình tự động dừng.");
                        break;
                    } else if (tweets.isEmpty() || !retryElements.isEmpty()) {
                        System.out.println("Không có dữ liệu mới trong 15 giây. Đợi 30s để tải thêm tweets...");
                        Thread.sleep(180000); // Đợi thêm để tải tweet mới

                        driver.navigate().refresh();
                    }

                    for (WebElement tweet : tweets) {
                        if (count >= maxTweets) break;

                        try {
                            List<WebElement> links = tweet.findElements(By.cssSelector("a[href*='/status/']"));
                            if (!links.isEmpty()) {
                                String tweetUrl = links.get(0).getAttribute("href");
                                
                                Tweet tweetCollected = new Tweet(tweetUrl);
                                tweetCollected.setUrl(tweetUrl);

                                if (!kol.hasTweet(tweetCollected)) {
                                    kol.addTweet(tweetCollected);
                                    count++;
                                    lastWriteTime = Instant.now().getEpochSecond();  // Cập nhật thời gian viết
                                }
                                System.out.println("COUNT = " + count);
                            }
                        } catch (Exception e) {
                            System.out.println("Không thể lấy tweets của người dùng này: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // Cuộn trang tiếp để tải thêm tweet
                    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                    long currentScrollHeight = (long) jsExecutor.executeScript("return document.body.scrollHeight");
                    jsExecutor.executeScript("window.scrollBy(0, 100)");
                    Thread.sleep(2000);
                    
                    if (currentScrollHeight == lastScrollHeight) {
                        stagnantScrollCount++;
                        if (stagnantScrollCount >= 3) {
                            System.out.println("Không thể cuộn thêm, quay lại trang trước.");
                            driver.navigate().back();
                            Thread.sleep(2000);
                            driver.navigate().refresh();
                            Thread.sleep(2000);
                            break; // Thoát vòng lặp
                        }
                    } else {
                        stagnantScrollCount = 0; // Reset nếu cuộn thành công
                    }
                    lastScrollHeight = currentScrollHeight; // Cập nhật chiều cao cuộn cuối cùng
                } catch (Exception e) {
                    System.out.println("Lỗi trong vòng lặp xử lý tweets: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
            int countDown = 0;
            for (Tweet tweet : kol.getTweets()) {
            	if(countDown == 0) {
            		Thread.sleep(10000);
            		driver.navigate().refresh();
            		Thread.sleep(2000);
            	}
            	if (countDown == 22) {
            		Thread.sleep(60000);
            		driver.navigate().refresh();
            		Thread.sleep(2000);
            		countDown = 1;
            	}
                tweet.setAuthor_id(tweet.getUrl());
                System.out.println("Đã cập nhật authorid: " + tweet.getAuthor_id());
            	extractInfo(tweet);
            	driver.get(tweet.getUrl());
            	Thread.sleep(3000);
            	int numComments = utils.convertTextToInteger(tweet.getCommentCount());
            	if (numComments == 0) {
            		continue;
            	}
            	Set<String> repliers = replierURL(tweet.getUrl(), maxComments, numComments);
            	if (repliers.isEmpty()) {
            		System.out.println("Không hiển thị người trả lời");
            		tweet.addCommentedUser("Không hiển thị");
            	} else {
            		for (String replier : repliers) {
                     	if (tweet.hasCommented(replier.substring(replier.indexOf("https://x.com/") + "https://x.com/".length())) == false) {
                     		tweet.addCommentedUser(replier.substring(replier.indexOf("https://x.com/") + "https://x.com/".length()));
                     		System.out.println("Đã thêm người comment có URL: " + replier);
                     	}
                    }
            	}
            	countDown++;
            	Thread.sleep(6000);
            }

            manager.updatePostsForUser(kol.getId(), kol.getTweets());
            manager.saveToDatabase();
        } catch (Exception e) {
            System.out.println("Lỗi trong phương thức fetchTweet: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	private String returnReply(Tweet tweet) {
		String replyCountText = "0"; // Mặc định là 0 nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        	WebElement replyButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-testid='reply']")));
        	WebElement replyCountElement = replyButton.findElement(By.cssSelector("span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
            replyCountText = replyCountElement.getText(); // Số lượng trả lời
            System.out.println("Số lượng trả lời: " + replyCountText);
        } catch (Exception e) {
            System.out.println("Không tìm thấy số lượng trả lời cho tweet: ");
            replyCountText = "0";
        }
        return replyCountText;
	}
	
	private String returnRepost(Tweet tweet) {
		// Lấy số lượng retweet
        String retweetCountText = "0"; // Mặc định là 0 nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        	WebElement retweetButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-testid='retweet']")));
        	WebElement repostCountElement = retweetButton.findElement(By.cssSelector("span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
            retweetCountText = repostCountElement.getText(); // Số lượng retweet
            System.out.println("Số lượng retweet: " + retweetCountText);
        } catch (Exception e) {
            System.out.println("Không tìm thấy số lượng retweet cho tweet: ");
            retweetCountText = "0";
        }
        return retweetCountText;
	}
	
	private String returnLike(Tweet tweet) {
		// Lấy số lượng like
        String likeCountText = "0"; // Mặc định là 0 nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        	WebElement likeButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-testid='like']")));
        	WebElement likeCountElement = likeButton.findElement(By.cssSelector("span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
            likeCountText = likeCountElement.getText(); // Số lượng like
            System.out.println("Số lượng like: " + likeCountText);
        } catch (Exception e) {
            System.out.println("Không tìm thấy số lượng like cho tweet: ");
            likeCountText = "0";
        }
		return likeCountText;
	}
	
	private String returnView(Tweet tweet) {
		// Lấy số lượt view
        String viewCountText = "0"; // Mặc định là 0 nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        	WebElement viewCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(
        			By.cssSelector("span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3 > div > span > span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3")));
            viewCountText = viewCountElement.getText(); // Lấy số lượt view
            System.out.println("Số lượt view: " + viewCountText);
        } catch (Exception e) {
            System.out.println("Không tìm thấy số lượt view cho tweet: ");
            viewCountText = "0";
        }
        return viewCountText;
	}
	
	private String returnContent(Tweet tweet) {
		// Lấy nội dung tweet
        String content = ""; // Mặc định là chuỗi rỗng nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        	WebElement tweetElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-testid='tweetText']")));
        	if (!tweetElement.isDisplayed()) {
        		driver.navigate().refresh();
        		tweetElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-testid='tweetText']")));
        	}
        	content = tweetElement.getText();
            System.out.println("Nội dung tweet: " + content); 
        } catch (Exception e) {
            System.out.println("Không tìm thấy nội dung tweet cho tweet: ");
            content = "Tweet không có nội dung";
        }
        return content;
	}
	
	private String returnTime(Tweet tweet) {
		// Lấy ngày đăng tweet
        String tweetDate = ""; // Mặc định là chuỗi rỗng nếu không tìm thấy
        try {
        	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement timeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'css-146c3p1')]//a//time")));
            tweetDate = timeElement.getAttribute("datetime"); // Lấy giá trị thuộc tính datetime
            System.out.println("Ngày đăng tweet: " + tweetDate);
        } catch (Exception e) {
            System.out.println("Không tìm thấy ngày đăng tweet cho tweet: ");
            tweetDate = "Error";
        } 
        return tweetDate;
	}
	
    private void scrollUntilElementsVisible() {
        while (true) {
            try {
            	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                // Kiểm tra xem phần tử có hiển thị trên trang hay không
                WebElement replyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[data-testid='reply'] span.css-1jxf684")));

                // Nếu tất cả các phần tử đã hiển thị, dừng cuộn
                if (replyElement.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                // Nếu các phần tử chưa xuất hiện, cuộn trang
            	((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 100)");
                try {
                    Thread.sleep(2000); // Đợi thêm thời gian cho trang tải các phần tử mới
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
	
	private void extractInfo(Tweet tweet) {
		driver.get(tweet.getUrl());
		List<WebElement> retryElements = driver.findElements(By.xpath("//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]"));
    	
    	if (!retryElements.isEmpty()) {
            try {
                driver.navigate().back();
                System.out.println("Đã nhấn nút Retry để tải nội dung.");
            } catch (Exception e) {
                System.out.println("Lỗi khi nhấn nút Retry: " + e.getMessage());
            }
        }
    	
		tweet.setContent(returnContent(tweet));
		scrollUntilElementsVisible();
		tweet.setCommentCount(returnReply(tweet));
		tweet.setLikeCount(returnLike(tweet));
		tweet.setRepostCount(returnRepost(tweet));
		tweet.setViewCount(returnView(tweet));
		tweet.setPostedDate(returnTime(tweet));
	}
	
	private Set<String> replierURL(String tweetUrl, int maxComments, int numComments) {
	    Set<String> replierLinks = new HashSet<>();
	    driver.get(tweetUrl); // Mở trang tweet
	    try {
	        Thread.sleep(3000);  // Đợi 3 giây
	    } catch (InterruptedException e) {
	        System.out.println("Lỗi khi đợi thời gian: " + e.getMessage());
	        Thread.currentThread().interrupt();  // Giữ lại trạng thái gián đoạn
	    }

	    int needComments = Math.min(maxComments, numComments); // Số comment cần lấy
	    int count = 0;
	    int stagnantScrollCount = 0; // Đếm số lần cuộn không tìm thấy phần tử mới
	    int previousTweetCount = 0; // Số lượng tweet trước lần cuộn cuối
	    
	    try {
	    	while (count < needComments) {
	    		// Cuộn xuống để tải thêm người comment
	            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1000)");

	            // Tìm các bài viết comment
	            List<WebElement> repliers = driver.findElements(By.cssSelector("article[data-testid='tweet']"));
	            if (repliers.isEmpty()) {
	            	driver.navigate().back();
		            try {
		                Thread.sleep(3000);  // Đợi 3 giây
		            } catch (InterruptedException e) {
		                System.out.println("Lỗi khi đợi thời gian: " + e.getMessage());
		                Thread.currentThread().interrupt();  // Giữ lại trạng thái gián đoạn
		            }
		            return replierLinks;  // Quay lại nếu tweet đã bị xoá
	            }
	            
	            // Kiểm tra nếu không có thêm phần tử mới
	            if (repliers.size() == previousTweetCount) {
	                stagnantScrollCount++;
	                if (stagnantScrollCount >= 2) { // Nếu không tìm thấy mới sau 2 lần cuộn
	                    System.out.println("Không tìm thấy thêm người comment, dừng cuộn.");
	                    break;
	                }
	            } else {
	                stagnantScrollCount = 0; // Reset nếu tìm thấy phần tử mới
	            }

	            previousTweetCount = repliers.size(); // Cập nhật số lượng tweet trước
	            
	            // Duyệt qua từng replier và lấy URL
	            for (WebElement replier : repliers) {
	                if (count >= needComments) {
	                    break;
	                }
	                try {
	                    List<WebElement> links = replier.findElements(By.cssSelector("a[href*='/']"));
	                    if (!links.isEmpty()) {
	                        String userProfileUrl = links.get(0).getAttribute("href");
	                        if (!replierLinks.contains(userProfileUrl)) {
	                            replierLinks.add(userProfileUrl);
	                            count++;
	                            System.out.println("Đã thêm người dùng: " + userProfileUrl);
	                        }
	                    }
	                } catch (Exception e) {
	                    System.out.println("Không thể lấy URL của người comment: " + e.getMessage());
	                }
	            }
	    	}
	    } catch (Exception e) {
	    	System.out.println("Lỗi trong quá trình lấy danh sách người comment: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        driver.navigate().back(); // Quay lại trang trước
	        try {
	            Thread.sleep(2000); // Chờ trang tải lại
	        } catch (InterruptedException e) {
	            System.out.println("Lỗi trong quá trình chờ quay lại: " + e.getMessage());
	            Thread.currentThread().interrupt();
	        }
	    }
	    return replierLinks;
	}
		
	@Override
	public void fetchProfileFromKOLFile(String filepath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchFollowersFromKOLFile(String filepath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchTweetsFromKOLFile(String filepath) {
		int countUp = 0;
		// TODO Auto-generated method stub
		Set<String> kolLinks = fileHandler.readElementsFromFile(filepath);
		if (kolLinks.isEmpty()) {
    		return;
    	}else {
    		for (String kolLink : kolLinks) {
    			if(countUp == 6) {
    				System.out.println("Đã đủ 7 KOLs, đăng nhập lại.");
    				return;
    			}
    			User processKOL = new User(kolLink);
    			if (!manager.hasUser(processKOL.getId())) {
    				manager.addUserToDataBase(processKOL);
    			}
    			processKOL = manager.getUserById(processKOL.getId());
    			Set<Tweet> tweets = processKOL.getTweets();
    			if (tweets.size() < maxTweets) {
    				fetchTweets(processKOL);
    				countUp++;
    			}else {
    				System.out.println("Đã đủ ?");
    				continue;
    			}
    			try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    			
    		}
    	}
	}
}