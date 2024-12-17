package filehandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	public void writeListStringToFile(String filePath, Set<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String element : content) {
                writer.write(element);
                writer.newLine();
            }
            writer.flush();
            System.out.println("Đã ghi danh sách nội dung vào file: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi danh sách nội dung vào file " + filePath);
            e.printStackTrace();
        }
    }

	@Override
	public String getProcessedDataFilePath() {
		// TODO Auto-generated method stub
		return ALL_KOLS_FILE_PATH;
	}
	
    public static List<String> splitFile(String inputFile, int n) throws IOException {
        // Đọc tất cả các dòng từ file đầu vào
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        // Kiểm tra điều kiện chia file
        if (n <= 0 || lines.size() < n) {
            throw new IllegalArgumentException("Số lượng file con phải nhỏ hơn hoặc bằng số dòng trong file gốc và lớn hơn 0.");
        }

        // Tính toán số dòng mỗi file nhỏ (phần dư sẽ được phân bổ đều)
        int linesPerFile = lines.size() / n;
        int extraLines = lines.size() % n;

        // Lấy timestamp hiện tại
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Danh sách lưu đường dẫn các file con
        List<String> filePaths = new ArrayList<>();

        // Chia file thành n file nhỏ
        int currentIndex = 0;
        for (int i = 0; i < n; i++) {
            // Số dòng trong file hiện tại
            int currentFileLines = linesPerFile + (i < extraLines ? 1 : 0);

            // Lấy các dòng cho file nhỏ
            List<String> subLines = lines.subList(currentIndex, currentIndex + currentFileLines);

            // Tạo tên file nhỏ
            String outputFileName = timestamp + "_subfilefrom_" + new File(inputFile).getName();
            outputFileName = outputFileName.replace(".txt", "_part" + (i + 1) + ".txt");

            // Đường dẫn file nhỏ
            File outputFile = new File(outputFileName);

            // Ghi các dòng vào file nhỏ
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                for (String subLine : subLines) {
                    writer.write(subLine);
                    writer.newLine();
                }
            }

            // Thêm đường dẫn vào danh sách
            filePaths.add(outputFile.getAbsolutePath());

            // Cập nhật chỉ số dòng hiện tại
            currentIndex += currentFileLines;
        }

        // Trả về danh sách đường dẫn các file nhỏ
        return filePaths;
    }
    
    public Map<String, String> getCredentialsFromFile(String filePath) {
        Map<String, String> credentials = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Bỏ khoảng trắng thừa
                if (line.startsWith("username:")) {
                    credentials.put("username", line.substring("username:".length()).trim());
                } else if (line.startsWith("password:")) {
                    credentials.put("password", line.substring("password:".length()).trim());
                } else if (line.startsWith("email:")) {
                    credentials.put("email", line.substring("email:".length()).trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        }

        return credentials;
    }
}
