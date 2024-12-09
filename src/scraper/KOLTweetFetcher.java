package scraper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.utils;

import entities.User;
import entities.Tweet;

import manager.DataManagerInterface;
import filehandler.FileHandlerInterface;

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
   //     manager.addUserToDataBase(kol);
        try {
            driver.get(kol.getUrl());
            Thread.sleep(6000); // Đợi tải trang
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");

            int count = 0;
            int stagnantScrollCount = 0;
            int previousTweetCount = 0;
            Set<String> tweetLinks = new HashSet<>();

            while (tweetLinks.size() < maxTweets) {
                try {
                    List<WebElement> tweets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article[data-testid='tweet']")));

                    if (tweets.isEmpty()) {
                        System.out.println("Không có dữ liệu mới trong 15 giây. Đợi 30s để tải thêm tweets...");
                        Thread.sleep(6000); // Đợi thêm để tải tweet mới

                        driver.navigate().refresh();
                    }

                    for (WebElement tweet : tweets) {
                        if (count >= maxTweets) break;

                        try {
                            List<WebElement> links = tweet.findElements(By.cssSelector("a[href*='/status/']"));
                            if (!links.isEmpty()) {
                                String tweetUrl = links.get(0).getAttribute("href");
                                if(!tweetLinks.contains(tweetUrl)) {
                                	tweetLinks.add(tweetUrl);
                                	System.out.println("Đã ghi nhận: "+tweetUrl);
                                }
                                System.out.println("COUNT = " + tweetLinks.size());
                            }
                        } catch (Exception e) {
                            System.out.println("Không thể lấy tweets của người dùng này: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    
                    if(tweetLinks.size() == previousTweetCount) {
                    	stagnantScrollCount++;
                    	System.out.println("Cuộn check lần thứ: "+stagnantScrollCount);
                    	if (stagnantScrollCount >= 50) {
                    		System.out.println("Hết dữ liệu");
                    		break;
                    	}
                    }else {
                    	stagnantScrollCount = 0;
                    }
                    
                    previousTweetCount = tweetLinks.size();

                    // Cuộn trang tiếp để tải thêm tweet
                    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                    jsExecutor.executeScript("window.scrollBy(0, 800)");
                    Thread.sleep(6000);
                } catch (Exception e) {
                    System.out.println("Lỗi trong vòng lặp xử lý tweets: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
            for(String tweetLink : tweetLinks) {
            	System.out.println("Đang xử lý cho: " + tweetLink);
            	String tweetId = tweetLink.substring(tweetLink.lastIndexOf("/") + 1);
            	String username = tweetLink.substring(tweetLink.indexOf("https://x.com/") + "https://x.com/".length(), tweetLink.lastIndexOf("/status"));
            	Tweet tweet = new Tweet(tweetId, username);
            	System.out.println(tweet.getAuthor_id());
            	tweet.setUrl(tweetLink);
            	if (!kol.hasTweet(tweet)) {
            		kol.addTweet(tweet);
            	}
            }
            proceedTweets(kol);
            manager.updatePostsForUser(kol.getId(), kol.getTweets());
            manager.saveToDatabase();
        } catch (Exception e) {
            System.out.println("Lỗi trong phương thức fetchTweet: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	private void scrollUntilGetRepliers() {
		while (true) {
            try {
            	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                // Kiểm tra xem phần tử có hiển thị trên trang hay không
                WebElement repliers = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article[data-testid='tweet']")));

                // Nếu tất cả các phần tử đã hiển thị, dừng cuộn
                if (repliers.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                // Nếu các phần tử chưa xuất hiện, cuộn trang
            	((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 100)");
                try {
                    Thread.sleep(10000); // Đợi thêm thời gian cho trang tải các phần tử mới
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
	}
	
	private void proceedTweets(User kol) {
		Set<Tweet> tweets = kol.getTweets();
		int countRespawn = 1;
		for (Tweet tweet : tweets) {
			System.out.println("Đang xử lý tweet cho user: " + kol.getId());
			driver.get(tweet.getUrl());
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (countRespawn % 20 == 0) {
				driver.navigate().refresh();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				countRespawn = 1;
			}
			countRespawn++;
			System.out.println("CountRespawn: " + countRespawn);
			extractInfo(tweet);
			int numComments = utils.convertTextToInteger(tweet.getCommentCount());
        	if (numComments == 0) {
        		continue;
        	}
			scrollUntilGetRepliers();
			Set<String> repliers = replierURL(kol, tweet.getUrl(), maxComments, numComments);
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
        	try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
                driver.navigate().refresh();
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
	
	public Set<String> replierURL(User kol, String tweetUrl, int maxComments, int numComments) {
	    Set<String> replierLinks = new HashSet<>();
	    int needComments = Math.min(maxComments, numComments); // Số comment cần lấy
	    int count = 0;
	    int stagnantScrollCount = 0; // Đếm số lần cuộn không tìm thấy phần tử mới
	    int previousTweetCount = 0; // Số lượng bài viết trước lần cuộn cuối
	    boolean isFirstElementSkipped = false; // Đánh dấu nếu đã bỏ qua phần tử đầu tiên
	    WebElement firstUserElement = null; // Lưu phần tử đầu tiên
	    int numSeenDicoverMore = 0;

	    try {
	    
	        while (count < needComments) {
	            // Kiểm tra và nhấn nút "Show more" nếu có
	            // Cuộn xuống để tải thêm nội dung
	            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800)");
	            Thread.sleep(3000);
	            List<WebElement> showMoreButtons = driver.findElements(By.xpath("//button[contains(@class, 'css-175oi2r') and contains(., 'Show')]"));
	            if (!showMoreButtons.isEmpty()) {
	                try {
	                    WebElement showMoreButton = showMoreButtons.get(0); // Lấy nút đầu tiên
	                    showMoreButton.click();
	                    System.out.println("Đã nhấn nút 'Show more' để tải thêm repliers.");
	                    Thread.sleep(3000); // Chờ nội dung tải xong
	                } catch (Exception e) {
	                    System.out.println("Không thể nhấn nút 'Show more': " + e.getMessage());
	                }
	            }

	            WebElement discoverMoreHeading = null;
	            try {
	                discoverMoreHeading = driver.findElement(By.xpath("//h2[contains(., 'Discover more')]"));
	                System.out.println("Thấy Discover More rồi nhe !!!");
	                numSeenDicoverMore ++;
	            } catch (Exception e) {
	                System.out.println("Không tìm thấy phần tử 'Discover more'. Bỏ qua bước này.");
	            }

	            List<WebElement> articles;
	            if (discoverMoreHeading != null) {
	                articles = driver.findElements(By.xpath("//h2[contains(., 'Discover more')]/preceding::article"
	                ));
	                System.out.println("Bug ở load user sau khi thấy Discover More");
	            } else {
	                articles = driver.findElements(By.xpath("//article[starts-with(@aria-labelledby, '')]"));
	            }

	            if (articles.size() == previousTweetCount) {
	                stagnantScrollCount++;
	                if (stagnantScrollCount >= 7) {
	                    System.out.println("Không tìm thấy thêm người comment, dừng cuộn.");
	                    break;
	                }
	            } else {
	                stagnantScrollCount = 0; // Reset nếu tìm thấy phần tử mới
	            }

	            previousTweetCount = articles.size(); // Cập nhật số lượng bài viết trước

	            // Lấy URL của từng replier
	            System.out.println("Bắt đầu duyệt nhé !!!");
	            for (WebElement article : articles) {
	                if (count >= needComments) {
	                    break;
	                }
	                try {
	                	
	                	boolean isAd = !article.findElements(By.xpath(".//*[contains(text(), 'Ad ')]")).isEmpty();
	                	if (isAd) {
	                	    System.out.println("Bỏ qua quảng cáo.");
	                	    continue;
	                	}
	                    
	                    WebElement avatar = article.findElement(By.xpath(".//*[@data-testid='Tweet-User-Avatar']"));
	                    String userProfileUrl = avatar.findElement(By.tagName("a")).getAttribute("href");
	                    

	                    if (!isFirstElementSkipped) {
	                        // Lưu phần tử đầu tiên
	                        firstUserElement = article;
	                        isFirstElementSkipped = true;
	                        System.out.println("Bỏ qua người dùng đầu tiên: " + userProfileUrl);
	                        continue;
	                    }

	                    // Kiểm tra nếu phần tử hiện tại trùng với phần tử đầu tiên
	                    if (article.equals(firstUserElement)) {
	                        System.out.println("Người dùng này trùng với người đầu tiên, bỏ qua.");
	                        continue;
	                    }

	                    if (replierLinks.add(userProfileUrl)) { // Chỉ thêm nếu chưa tồn tại
	                        count++;
	                        System.out.println("Đã thêm người dùng: " + userProfileUrl);
	                    }

	                } catch (NoSuchElementException e) {
	                    System.out.println("Không thể tìm thấy avatar trong bài viết này.");
	                } catch (Exception e) {
	                    System.out.println("Lỗi khi lấy URL người comment: " + e.getMessage());
	                }
	            }
	            if (numSeenDicoverMore==2) {
	                System.out.println("Thấy rồi, dừng cuộn nhé !");
	                break;
	            }
	        }
	    } catch (InterruptedException e) {
	        System.out.println("Lỗi trong quá trình xử lý: " + e.getMessage());
	        Thread.currentThread().interrupt(); // Giữ lại trạng thái gián đoạn
	    } finally {
	        System.out.println("Kết thúc quá trình thu thập danh sách người comment.");
	    }
	    manager.saveToDatabase();
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
    			if(countUp == 7) {
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
    				continue;
    			}
    			try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    			
    		}
    	}
	}
	public boolean isElementPresent(String xpath) {
	    try {
	        driver.findElement(By.xpath(xpath));
	        return true; // Phần tử tồn tại
	    } catch (Exception e) {
	        return false; // Phần tử không tồn tại
	    }
	}
}