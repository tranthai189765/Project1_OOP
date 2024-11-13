package datacollection;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHandler {

    // Đọc các liên kết người dùng từ file
    public Set<String> readLinksFromFile(String filePath) {
        Set<String> links = new HashSet<>();
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    links.add(line.trim());
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi đọc file " + filePath);
                e.printStackTrace();
            }
        }
        return links;
    }

    // Ghi các liên kết vào file
    public void writeLinksToFile(String filePath, Set<String> links) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String link : links) {
                writer.write(link);
                writer.newLine();
            }
            writer.flush();
            System.out.println("Đã ghi các liên kết vào file: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi vào file " + filePath);
            e.printStackTrace();
        }
    }

    // Tạo file mới cho ngày hôm nay với định dạng YYYY-MM-DD_HH-MM-SS
    public File createDailyFile() {
        String today = java.time.LocalDate.now().toString(); 
        String currentTime = java.time.LocalTime.now().toString().replace(":", "-");
        File dailyFile = new File(today + "_" + currentTime + "_user_links.txt");

        if (!dailyFile.exists()) {
            try {
                dailyFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Lỗi khi tạo file hôm nay.");
                e.printStackTrace();
            }
        }

        System.out.println("Đường dẫn tuyệt đối của file hôm nay: " + dailyFile.getAbsolutePath());
        return dailyFile;
    }

    // Đọc danh sách hashtag từ file
    public List<String> readHashtagsFromFile(String hashtagFilePath) {
        List<String> hashtags = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(hashtagFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    hashtags.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file hashtags: " + e.getMessage());
            e.printStackTrace();
        }
        return hashtags;
    }

    // Ghi thông báo về hashtag đang thu thập vào file
    public void noticeStartHashtag(String filePath, String hashtag) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("Đang thu thập dữ liệu với hashtag: " + hashtag);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi thông báo vào file tổng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Ghi thông báo thu thập xong hashtag vào file
    public void noticeEndHashtag(String filePath, String hashtag) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("Đã thu thập xong dữ liệu với hashtag: " + hashtag);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi thông báo vào file tổng: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
