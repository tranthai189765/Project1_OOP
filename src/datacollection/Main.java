package datacollection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
    
    public static void main(String[] args) {
        
        // Thông tin đăng nhập Twitter
        String username = "Tranthaiabcabc";
        String password = "det@i1OOP2024";
        String email = "tranthai18976543@gmail.com";

        // Khởi tạo WebDriver
        WebDriver driver = new ChromeDriver();
        
        try {
            // Tạo đối tượng TwitterKOLFinder1
            TwitterKOLFinder finder = new TwitterKOLFinder(driver, username, password, email);

            // Tìm kiếm KOLs với hashtag #Blockchain
            finder.runWithHashtagsFromFile(2000);

        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
}
