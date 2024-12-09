package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.TwitterLogin;


import manager.DataManagerInterface;
import manager.TwitterDataManager;
import scraper.KOLBasicInfoFetcher;
import scraper.KOLFollowerFetcher;
import scraper.KOLTweetFetcher;
import entities.User;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;
import scraper.KOLTweetFetcher;

public class Test2 {
public static void main( String[] args) {
		
        // Thông tin đăng nhập Twitter
	    WebDriver driver = new ChromeDriver();
        String username = "Tranthaiabcabc";
        String password = "det@i1OOP2024";
        String email = "tranthai18976543@gmail.com";
        ConfigInterface config = new TwitterConfig();
        DataManagerInterface manager = new TwitterDataManager("te1.json");
        FileHandlerInterface filehandler = new TwitterFileHandler();
        manager.loadFromDatabase();
        try {
        	TwitterLogin lo = new TwitterLogin(username, password, email, config);
        	lo.login(driver);
        	KOLTweetFetcher fetch = new KOLTweetFetcher(driver, manager, 40, 30, filehandler);
        	driver.get("https://x.com/I_amDorris/status/1864678894982295880");
        	System.out.println(fetch.replierURL(new User(), "https://x.com/I_amDorris/status/1864678894982295880", 40, 40));

        	
        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
	

}
