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
import entities.User;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;

public class Test {
public static void main( String[] args) {
		
        // Thông tin đăng nhập Twitter
	    WebDriver driver = new ChromeDriver();
        String username = "Tranthaiabcabc";
        String password = "det@i1OOP2024";
        String email = "tranthai18976543@gmail.com";
        ConfigInterface config = new TwitterConfig();
        DataManagerInterface manager = new TwitterDataManager("te.json");
        FileHandlerInterface filehandler = new TwitterFileHandler();
        manager.loadFromDatabase();

        // Khởi tạo WebDriver
        
   
        
        try {
        	TwitterLogin login = new TwitterLogin(username,password,email,config);
        	login.login(driver);
        	KOLBasicInfoFetcher fetch = new KOLBasicInfoFetcher(driver, manager, filehandler);
        	fetch.fetchProfileFromKOLFile("all_user_links.txt");
          
        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
	

}
