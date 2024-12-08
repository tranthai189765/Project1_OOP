package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.TwitterLogin;
import manager.DataManagerInterface;
import manager.TwitterDataManager;
import scraper.KOLBasicInfoFetcher;
import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;

public class Test1 {
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
            Thread thread = new Thread(new TwitterDataFetcher(account[0], account[1], account[2]));
            thread.start();
        }
    }
}

// Lớp Runnable cho mỗi thread
class TwitterDataFetcher implements Runnable {
    private String username;
    private String password;
    private String email;

    public TwitterDataFetcher(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public void run() {
        // In ra console để biết thread nào đang chạy
        System.out.println("Thread bắt đầu cho tài khoản: " + username);

        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        ConfigInterface config = new TwitterConfig();
        DataManagerInterface manager = new TwitterDataManager("te1_" + username + ".json");
        FileHandlerInterface fileHandler = new TwitterFileHandler();

        try {
            // Đăng nhập
            System.out.println("Đang đăng nhập tài khoản: " + username);
            TwitterLogin login = new TwitterLogin(username, password, email, config);
            login.login(driver);
            System.out.println("Đăng nhập thành công: " + username);

            // Thu thập dữ liệu KOL
            KOLBasicInfoFetcher fetcher = new KOLBasicInfoFetcher(driver, manager, fileHandler);
            System.out.println("Bắt đầu thu thập dữ liệu cho: " + username);
            fetcher.fetchProfileFromKOLFile("all_user_links_" + username + ".txt");
            System.out.println("Thu thập dữ liệu hoàn tất cho: " + username);
        } catch (Exception e) {
            System.err.println("Lỗi xảy ra với tài khoản: " + username);
            e.printStackTrace();
        } finally {
            // Đóng trình duyệt
            driver.quit();
            System.out.println("Thread kết thúc cho tài khoản: " + username);
        }
    }
}
