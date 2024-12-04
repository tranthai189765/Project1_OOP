package filehandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TwitterFileHandler implements FileHandlerInterface {
	
    private static final String ALL_LINKS_FILE_PATH = "all_user_links.txt";
    private static final String HASHTAGS_FILE_PATH = "hashtags.txt";
    private static final String ALL_KOLS_FILE_PATH = "kol_links.txt";

	@Override
	public Set<String> readElementsFromFile(String filePath) {
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

	@Override
	public void writeElementsToFile(String filePath, Set<String> elements) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String element : elements) {
                writer.write(element);
                writer.newLine();
            }
            writer.flush();
            System.out.println("Đã ghi các liên kết vào file: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi vào file " + filePath);
            e.printStackTrace();
        }
	}

	@Override
	public File createDailyFile(String model) {
        String today = java.time.LocalDate.now().toString(); 
        String currentTime = java.time.LocalTime.now().toString().replace(":", "-");
        File dailyFile = new File(today + "_" + currentTime + "_" + model + "_" + ".txt");

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
	
	@Override
	public void writeNoticeToFile(String filePath, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi thông báo vào file tổng: " + e.getMessage());
            e.printStackTrace();
        }
		
	}

	@Override
	public void notice(String filePath, String model, String status) {
	    // Kiểm tra chỉ thực hiện nếu status là "start" hoặc "end"
	    if (status.equals("start") || status.equals("end")) {
	        String message = (status.equals("start"))
	                ? "Đang thu thập dữ liệu với hashtag: " + model
	                : "Đã thu thập xong dữ liệu với hashtag: " + model;

	        writeNoticeToFile(filePath, message);
	    } else {
	        // Nếu status không phải "start" hoặc "end", thông báo lỗi
	        System.err.println("Lỗi: Trạng thái không hợp lệ. Trạng thái phải là 'start' hoặc 'end'.");
	    }
	}

	public String getTotalDataFilePath() {
		return ALL_LINKS_FILE_PATH;
	}

	public String getModelFilePath() {
		return HASHTAGS_FILE_PATH;
	}

	@Override
	public void writeStringtoFile(String filePath, String content) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
	        writer.write(content);
	        writer.newLine();
	        writer.flush();
	        System.out.println("Đã ghi nội dung vào file: " + filePath);
	    } catch (IOException e) {
	        System.out.println("Lỗi khi ghi nội dung vào file " + filePath);
	        e.printStackTrace();
	    }
		
	}

	@Override
	public String getProcessedDataFilePath() {
		// TODO Auto-generated method stub
		return ALL_KOLS_FILE_PATH;
	}
}
