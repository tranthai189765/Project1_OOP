package scraper;

import java.time.Duration;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import entities.User;
import manager.DataManagerInterface;


public class KOLBasicInfoFetcher implements DataFetcherStrategy {
    private WebDriver driver;
    private DataManagerInterface manager;

    public KOLBasicInfoFetcher(WebDriver driver, DataManagerInterface manager ) {
        this.driver = driver;
        this.manager = manager;
    }


	@Override
	public void fetchProfile(User kol) {
	    // TODO Auto-generated method stub
	    System.out.println("Fetching KOL profile...");
	    manager.addUserToDataBase(kol);
	    driver.get(kol.getUrl());
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    
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
	    	WebElement followingCountElement = driver.findElement(By.cssSelector("a[href$='/following'] span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
	        String followingCountText  = followingCountElement.getText();
	        //System.out.println("Số lượng following của KOL: " + followingCount);
	        kol.setFollowingCount(followingCountText);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy số lượng following của user.");
	    }

	    try {
	        // Lấy số lượng followers của user
	    	WebElement followersCountElement = driver.findElement(By.cssSelector("a[href$='/verified_followers'] span.css-1jxf684.r-bcqeeo.r-1ttztb7.r-qvutc0.r-poiln3"));
	        String followersCountText = followersCountElement.getText();
	        kol.setFollowersCount(followersCountText);
	    } catch (Exception e) {
	        System.out.println("Không tìm thấy số lượng followers của user.");
	    }
	    manager.updateBasicInfoForUser(kol.getId(), kol);
	    manager.saveToDatabase();
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
