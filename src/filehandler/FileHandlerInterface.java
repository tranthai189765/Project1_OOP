package filehandler;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FileHandlerInterface {
    // Đọc các dòng từ file
    Set<String> readElementsFromFile(String filePath);

    // Ghi các dòng vào file
    void writeElementsToFile(String filePath, Set<String> elements);
    void writeStringtoFile(String filePath, String content);

    // Tạo file mới cho ngày hôm nay với định dạng YYYY-MM-DD_HH-MM-SS
    File createDailyFile(String model);

    // Ghi thông báo về quá trình làm việc vào file
    void writeNoticeToFile(String filePath, String message);
    void notice(String filePath, String model, String status);

	String getTotalDataFilePath();
	
	String getModelFilePath();
	
	String getProcessedDataFilePath();
	public static List<String> splitFile(String inputFile, int n) {
		return null;
	}
	public Map<String, String> getCredentialsFromFile(String filePath);
	public void writeListStringToFile(String filePath, Set<String> recordedLinks);

}
