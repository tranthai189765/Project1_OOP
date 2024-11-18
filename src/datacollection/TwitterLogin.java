package datacollection;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TwitterLogin {

    private String username;
    private String password;
    private String email;

    // Constructor nhận thông tin đăng nhập từ ngoài
    public TwitterLogin(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void login(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Mở trang đăng nhập của Twitter
            driver.get("https://twitter.com/login");

            // Nhập tên đăng nhập
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("text")));
            emailField.sendKeys(username);

            // Nhấn nút 'Next'
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
            nextButton.click();

            // Kiểm tra nếu có yêu cầu nhập email
            try {
                WebElement emailField1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("text")));
                emailField1.sendKeys(email);
                
                WebElement nextButtonAfterEmail = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
                nextButtonAfterEmail.click();
            } catch (Exception e) {
                System.out.println("Không yêu cầu nhập email.");
            }

            // Nhập mật khẩu
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(password);

            // Nhấn nút 'Log in'
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Log in']")));
            loginButton.click();

            // Kiểm tra tiêu đề trang để xác nhận đăng nhập thành công
            wait.until(ExpectedConditions.titleContains("Home"));
            String pageTitle = driver.getTitle();
            if (pageTitle.contains("Home") || pageTitle.contains("Twitter")) {
                System.out.println("Đăng nhập thành công!");
            } else {
                System.out.println("Đăng nhập không thành công.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Các phương thức getter và setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
