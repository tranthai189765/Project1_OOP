package utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class utils {
    public static int convertTextToInteger(String text) {
        // Loại bỏ khoảng trắng (nếu có)
        text = text.trim();
        
        // Loại bỏ dấu phẩy (nếu có)
        text = text.replace(",", "");
        
        // Kiểm tra ký tự cuối cùng để xác định hệ số nhân
        char lastChar = text.charAt(text.length() - 1);
        double multiplier = 1;

        switch (lastChar) {
            case 'M': // Hệ số triệu
                multiplier = 1_000_000;
                text = text.substring(0, text.length() - 1);
                break;
            case 'K': // Hệ số nghìn
                multiplier = 1_000;
                text = text.substring(0, text.length() - 1);
                break;
            case 'B': // Hệ số tỷ (tuỳ chọn thêm nếu cần)
                multiplier = 1_000_000_000;
                text = text.substring(0, text.length() - 1);
                break;
        }

        // Chuyển đổi phần số thập phân thành số double
        double value = Double.parseDouble(text);

        // Nhân với hệ số và chuyển thành số nguyên
        return (int) (value * multiplier);
    }
    public static void mergeJsonFiles(String[] inputFilePaths, String outputFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mergedJson = objectMapper.createObjectNode();

        // Lặp qua từng file input
        for (String filePath : inputFilePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                JsonNode jsonNode = objectMapper.readTree(file);

                // Lặp qua từng key trong file JSON
                Iterator<String> fieldNames = jsonNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode value = jsonNode.get(fieldName);

                    // Kiểm tra key trùng lặp
                    if (mergedJson.has(fieldName)) {
                        System.out.println("Debug: Key bị trùng phát hiện - " + fieldName + " trong file: " + filePath);
                    } else {
                        // Chỉ thêm key nếu nó chưa tồn tại trong JSON tổng hợp
                        mergedJson.set(fieldName, value);
                    }
                }
            } else {
                System.out.println("File không tồn tại: " + filePath);
            }
        }

        // Ghi JSON tổng hợp ra file output
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), mergedJson);
        System.out.println("Hợp nhất thành công! File kết quả: " + outputFilePath);
    }
    public static void extractUrlsToFile(String inputJsonFilePath, String outputTxtFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(inputJsonFilePath));

        try (FileWriter writer = new FileWriter(outputTxtFilePath)) {
            // Lặp qua các trường trong JSON gốc
            Iterator<String> fieldNames = rootNode.fieldNames();
            while (fieldNames.hasNext()) {
                String key = fieldNames.next();
                JsonNode arrayNode = rootNode.get(key);

                // Lặp qua từng phần tử trong mảng
                if (arrayNode.isArray()) {
                    for (JsonNode node : arrayNode) {
                        JsonNode urlNode = node.get("url");
                        if (urlNode != null) {
                            String url = urlNode.asText();
                            writer.write(url + System.lineSeparator());
                        }
                    }
                }
            }
        }

        System.out.println("Các URL đã được trích xuất vào file: " + outputTxtFilePath);
    }
    public static void removeDuplicates(String inputFilePath, String outputFilePath) throws IOException {
        // Sử dụng HashSet để lưu trữ các link không trùng lặp
        Set<String> uniqueLinks = new HashSet<>();

        // Đọc file input
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Thêm link vào Set nếu nó chưa tồn tại
                if (uniqueLinks.add(line)) {
                    // Ghi link vào file output
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        System.out.println("Đã loại bỏ các link trùng lặp và ghi vào file: " + outputFilePath);
    }


    public static void main(String[] args) {
        // Ví dụ sử dụng
        System.out.println(convertTextToInteger("23.4M"));  // Output: 23400000
        System.out.println(convertTextToInteger("15.7K"));  // Output: 15700
        System.out.println(convertTextToInteger("1.2B"));   // Output: 1200000000
        System.out.println(convertTextToInteger("500"));    // Output: 500
        System.out.println(convertTextToInteger("3,776"));  // Output: 3776
    }
}
