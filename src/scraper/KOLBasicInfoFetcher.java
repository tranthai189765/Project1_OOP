package scraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import entities.User;
import filehandler.FileHandlerInterface;
import manager.DataManagerInterface;
import utils.utils;


public class KOLBasicInfoFetcher implements DataFetcherStrategy {
	private WebDriver driver;
    private DataManagerInterface manager;
    private FileHandlerInterface filehandler;

    public KOLBasicInfoFetcher(WebDriver driver, DataManagerInterface manager, FileHandlerInterface filehandler ) {
        this.driver = driver;
        this.manager = manager;
        this.filehandler = filehandler;
       
    }


	@Override
	public void fetchProfile(User kol) {
	    // TODO Auto-generated method stub
	    System.out.println("Fetching KOL profile...");
	    driver.get(kol.getUrl());
	    try {
	        Thread.sleep(10000); // Tạm dừng 3 giây
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
	    
	    try {
	        // Lấy số lượng bài đăng
	        WebElement postCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'posts')]")));
	        String postCountText = postCountElement.getText();
	        //System.out.println("Số lượng bài đăng: " + postCountText);
	        kol.setTweetCount(postCountText);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy số lượng bài đăng.");
	    }

	    try {
	        // Tìm phần tử mô tả
	        WebElement introElement = driver.findElement(By.cssSelector("div[data-testid='UserDescription']"));

	        // Lấy toàn bộ nội dung của phần mô tả (bao gồm cả văn bản và liên kết)
	        String introText = introElement.getText().replace("\n", " ");
	        kol.setDescription(introText);
	    } catch (Exception e) {
	        System.out.println("Có lỗi xảy ra khi xử lý phần mô tả.");
	    }


	    try {
	        // Lấy công ty của user
	        WebElement companyElement = driver.findElement(By.cssSelector("span[data-testid='UserProfessionalCategory'] button span"));
	        String companyText = companyElement.getText();
	        //System.out.println("Công ty của user: " + companyText);
	        kol.setProfessionalCategory(companyText);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy công ty của user.");
	    }

	    try {
	        // Lấy vị trí của user
	        WebElement locationElement = driver.findElement(By.cssSelector("span[data-testid='UserLocation'] span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
	        String locationText = locationElement.getText();
	        //System.out.println("Vị trí của user: " + locationText);
	        kol.setLocation(locationText);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy vị trí của user.");
	    }

	    try {
	        // Lấy website của user
	        WebElement websiteElement = driver.findElement(By.cssSelector("a[data-testid='UserUrl']"));
	        String websiteUrl = websiteElement.getAttribute("href");
	        //System.out.println("Website của user: " + websiteUrl);
	        kol.setWebsite(websiteUrl);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy website của user.");
	    }

	    try {
	        // Lấy ngày tham gia của user
	        WebElement joinDateElement = driver.findElement(By.cssSelector("span[data-testid='UserJoinDate']"));
	        String joinDate = joinDateElement.getText();
	        //System.out.println("Ngày tham gia của user: " + joinDate);
	        kol.setJoinDate(joinDate);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy ngày tham gia của user.");
	    }

	    try {
	        // Lấy số lượng following của user
	        WebElement followingCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='/following'] span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3")));
	        String followingCountText  = followingCountElement.getText();
	        int followingCount = utils.convertTextToInteger(followingCountText);
	        kol.setFollowingCount(followingCount);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy số lượng following của user.");
	        
	    }

	    try {
	        // Lấy số lượng followers của user
	    	WebElement followersCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='/verified_followers'] span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3")));
	        String followersCountText = followersCountElement.getText();
	        int followersCount = utils.convertTextToInteger(followersCountText);
	        kol.setFollowersCount(followersCount);
	        if(followersCount >= 5000) {
	        	
	        	filehandler.writeStringtoFile(filehandler.getProcessedDataFilePath(), kol.getUrl());
	        	System.out.println("Đã ghi link: "+ kol.getUrl() + "vào " + filehandler.getProcessedDataFilePath());
	        	
	        }else {
	        	System.out.println("Số lượng following không đủ. "+ followersCount + " Dừng xử lý user này: " + kol.getUrl());
	        	return;
	
	        }
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy số lượng followers của user.");
	    }
	    kol.setKolType();
	    if (!kol.getKolType().equals("Non-KOL")) {
	    manager.addUserToDataBase(kol);
	    manager.saveToDatabase();
	    }
	    else {
	    	System.out.println("Không thực hiện cho user vào DataBase : " + kol.getUrl() );
	    }
	}


	@Override
	public void fetchFollowers(User kol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchTweets(User kol) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fetchProfileFromKOLFile(String filepath) {
		// TODO Auto-generated method stub
        System.out.println("Đọc các liên kết từ file: " + filepath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("https")) { // Kiểm tra nếu là đường dẫn hợp lệ
                    System.out.println("Xử lý URL: " + line);
                    User user = new User(line); // Tạo đối tượng User từ URL
                    if(manager.hasUser(user.getId())==true) {
                    	user = manager.getUserById(user.getId());
                    	if(user.getTweetCount() == null || user.getTweetCount().isEmpty()) {
                    		// Gọi fetchProfile để xử lý User
                        	fetchProfile(user); 
                        }else {
                        	System.out.println("Thông tin cơ bản về User "+ user.getId() + " đã có trong database");
                        }
                    }else {
                    	// Gọi fetchProfile để xử lý User
                    	System.out.println("Chưa có thông tin gì về User " + user.getId());
                    	fetchProfile(user); 
                    }
                    
                            
                } else {
                    System.out.println("Bỏ qua dòng không hợp lệ: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Có lỗi xảy ra khi đọc file: " + e.getMessage());
        }
		
	}


	@Override
	public void fetchFollowersFromKOLFile(String filepath) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fetchTweetsFromKOLFile(String filepath) {
		// TODO Auto-generated method stub
		
	}

}
