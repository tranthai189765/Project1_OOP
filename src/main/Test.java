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

public class Test {
public static void main( String[] args) {
		
        // Thông tin đăng nhập Twitter
        String username = "Tranthaiabcabc";
        String password = "det@i1OOP2024";
        String email = "tranthai18976543@gmail.com";
        ConfigInterface config = new TwitterConfig();
        DataManagerInterface manager = new TwitterDataManager();

        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
   
        
        try {
            // Tạo đối tượng TwitterKOLFinder1
            TwitterLogin login = new TwitterLogin(username, password, email, config);

            // Tìm kiếm KOLs với hashtag #Blockchain
            login.login(driver);
            
            KOLFollowerFetcher fetch = new KOLFollowerFetcher(driver, manager, 20);
            KOLBasicInfoFetcher fetch1 = new KOLBasicInfoFetcher(driver, manager);
            User Kol1 = new User("https://x.com/CitizenBitcoin");
            User Kol2 = new User("https://x.com/DrBitcoinMD");
            fetch1.fetchProfile(Kol1);
            fetch1.fetchProfile(Kol2);
            fetch.fetchFollowers(Kol1);
            fetch.fetchFollowers(Kol2);
           // fetch.graph.display();

        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
	

}
