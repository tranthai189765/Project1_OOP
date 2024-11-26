package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.TwitterLogin;
import graph.GraphModel;
import scraper.FollowerFetcher;

public class Test {
public static void main( String[] args) {
		
        // Thông tin đăng nhập Twitter
        String username = "Tranthaiabcabc";
        String password = "det@i1OOP2024";
        String email = "tranthai18976543@gmail.com";
        ConfigInterface config = new TwitterConfig();

        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        GraphModel graph = new GraphModel("TwitterGraph.graphml");
        
        try {
            // Tạo đối tượng TwitterKOLFinder1
            TwitterLogin login = new TwitterLogin(username, password, email, config);

            // Tìm kiếm KOLs với hashtag #Blockchain
            login.login(driver);
            
            FollowerFetcher fetch = new FollowerFetcher(driver, graph);
            fetch.fetchFollowers("https://x.com/Sanemavcil", 20);
            fetch.graph.display();

        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
	

}
