package synchronization;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import config.ConfigInterface;
import entities.User;
import manager.DataManagerInterface;
public class DataSyncManager {
    public static void syncFromRemote(DataManagerInterface localManager, DataManagerInterface remoteManager) {
    	remoteManager.loadFromDatabase();
    	localManager.loadFromDatabase();
    	List<String> userIds = remoteManager.getUserIds(); 
    	for(String id : userIds) {
    		if(localManager.hasUser(id) == true) {
    	
    			User userLocal = localManager.getUserById(id);
    			User userRemote = remoteManager.getUserById(id);
    			
    			if (userRemote.getTweets() != null && !userRemote.getTweets().isEmpty()) {
    			    userLocal.setTweets(userRemote.getTweets());
    			}
    			if (userRemote.getFollowers() != null && !userRemote.getFollowers().isEmpty()){
    				userLocal.setFollowers(userRemote.getFollowers());
    			}
    			if (userRemote.getDescription() != null) {
    				userLocal.setDescription(userRemote.getDescription());
    			}
    			if (userRemote.getFollowersCount() != 0) {
    				userLocal.setFollowingCount(userRemote.getFollowingCount());
    			}
    			if (userRemote.getFollowingCount() != 0) {
    				userLocal.setFollowingCount(userRemote.getFollowingCount());
    			}
    			if (userRemote.getFollowing() != null && !userRemote.getFollowing().isEmpty()) {
    				userLocal.setFollowing(userRemote.getFollowing());
    			}
    			if (userRemote.getJoinDate() != null) {
    				userLocal.setJoinDate(userRemote.getJoinDate());
    			}
    			if (userRemote.getKolType() != null) {
    				userLocal.setKolType();
    			}
    			if (userRemote.getDescription() != null) {
    				userLocal.setDescription(userRemote.getDescription());
    			}
    			if (userRemote.getLocation() != null) {
    				userLocal.setLocation(userRemote.getLocation());
    			}
    			if (userRemote.getProfessionalCategory()!= null) {
    				userLocal.setProfessionalCategory(userRemote.getProfessionalCategory());
    			}
    			if (userRemote.getTweetCount() != null) {
    				userLocal.setTweetCount(userRemote.getTweetCount());
    			}
    			if (userRemote.getUrl()!= null) {
    				userLocal.setUrl(userRemote.getUrl());
    			}
    			if (userRemote.getWebsite() != null) {
    				userLocal.setWebsite(userRemote.getWebsite());
    			}
    		}
    		else {
    			User userRemote = remoteManager.getUserById(id);
    			localManager.addUserToDataBase(userRemote);
    		}
    	}
    	localManager.saveToDatabase();
    	System.out.println("Đồng bộ dữ liệu từ remote database hoàn tất.");
    }
    public static void updateLocalDatabasefromThreads(ConfigInterface config, int ThreadCount) {
    	DataManagerInterface localManager = config.getLocalManager();
    	for(int index = 0; index < ThreadCount; index ++) {
    		String filepathThreads = index + "_database.json";
    		DataManagerInterface remoteManager = config.newManager(filepathThreads);
    		remoteManager.loadFromDatabase();
    		syncFromRemote(localManager, remoteManager);
    	}
    }
    public static void updateLocalFileDataBase(String sourceFilePath, String targetFilePath) throws IOException {
        // Sử dụng try-with-resources để tự động đóng luồng khi hoàn thành
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath, true))) {
             
            String line;
            // Đọc từng dòng từ file nguồn và ghi vào file đích
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine(); // Thêm dòng mới để đảm bảo format giống file nguồn
            }
        }
    }
    public static void updateLocalFileDataBaseFromThreads(String targetFilePath) throws IOException {
    	for(int index = 0; index <= 5;index ++) {
    		String filePath = index + "_UsersFromHashtags.txt";
    		updateLocalFileDataBase(filePath, targetFilePath);
    	}
    	removeDuplicatesInPlace(targetFilePath);
    	
    }


    public static void removeDuplicatesInPlace(String filePath) throws IOException {
        // Sử dụng Set để lưu trữ các dòng duy nhất
        Set<String> uniqueLines = new LinkedHashSet<>();
        
        // Đọc tất cả nội dung từ file và lưu vào Set
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                uniqueLines.add(line.trim()); // Trim để loại bỏ khoảng trắng thừa
            }
        }

        // Ghi lại file ban đầu với các dòng duy nhất
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : uniqueLines) {
                writer.write(line);
                writer.newLine();
            }
        }

        System.out.println("Đã loại bỏ các dòng trùng lặp trực tiếp trên file: " + filePath);
    }

}
