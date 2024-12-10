package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.TwitterLogin;
import java.io.File;
import java.time.Instant;

import manager.DataManagerInterface;
import manager.TwitterDataManager;
import scraper.KOLBasicInfoFetcher;
import scraper.KOLFollowerFetcher;
import scraper.KOLTweetFetcher;
import entities.User;
import entities.Tweet;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;

public class Test5 {
    public static void main(String[] args) {
        // Thông tin tài khoản
        String[][] accounts = {     
                {"Tranthaiabcabc", "det@i1OOP2024", "tranthai18976543@gmail.com"},
                {"@ThiTrn600349781", "det@i1OOP2024", "tranthai189765@gmail.com"},
                {"@QucThiTrn174803", "det@i1OOP2024", "nocturnett7@gmail.com"},
                {"@ThuyLinh62474", "det@i1OOP2024", "thuylinhtran311@gmail.com"}
        };

        // Khởi tạo và chạy 2 thread
        for (String[] account : accounts) {
            Thread thread = new Thread(new TwitterDataFetcher1(account[0], account[1], account[2]));
            thread.start();
        }
    }
}

// Lớp Runnable cho mỗi thread
class TwitterDataFetcher1 implements Runnable {
    private String username;
    private String password;
    private String email;

    public TwitterDataFetcher1(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public void run() {
        // In ra console để biết thread nào đang chạy
        System.out.println("Thread bắt đầu cho tài khoản: " + username);

        while (true) {
        	WebDriver driver = null;
        	try {
            // Bắt đầu chu kỳ làm việc 2 tiếng
            driver = new ChromeDriver();
            ConfigInterface config = new TwitterConfig();
            DataManagerInterface manager = new TwitterDataManager("te7_" + username + ".json");
            FileHandlerInterface fileHandler = new TwitterFileHandler();
            manager.loadFromDatabase();
                System.out.println("Đang đăng nhập tài khoản: " + username);
                TwitterLogin login = new TwitterLogin(username, password, email, config);
                login.login(driver);
                System.out.println("Đăng nhập thành công: " + username);

                KOLTweetFetcher fetch = new KOLTweetFetcher(driver, manager, 40, 30, fileHandler);
                System.out.println("Bắt đầu thu thập dữ liệu cho: " + username);

                fetch.fetchTweetsFromKOLFile("kol_links_" + username + ".txt");
                System.out.println("Thu thập dữ liệu hoàn tất cho: " + username);
                driver.quit();
                
            } catch (Exception e) {
                System.err.println("Lỗi xảy ra với tài khoản: " + username);
                e.printStackTrace();
            } finally {
                // Đóng trình duyệt và nghỉ trước khi chạy lại
                System.out.println("Kết thúc chu kỳ 2 tiếng cho tài khoản: " + username);
                try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}