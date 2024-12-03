package manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import entities.Tweet;
import entities.User;

public class TwitterDataManager implements DataManagerInterface {
	private Map<String, List<User>> data;
	public TwitterDataManager() {
        this.data = new HashMap<>();
    }

	@Override
	public void addUserToDataBase(User user) {
		// TODO Auto-generated method stub
		String dict = user.getId();
        data.computeIfAbsent(dict, k -> new ArrayList<>());
        
        // Kiểm tra nếu User đã có trong danh sách
        if (!data.get(dict).contains(user)) {
            data.get(dict).add(user);
            System.out.println("Đã thêm User: " + user.getUrl());
        } else {
            System.out.println("User đã tồn tại: " + user.getUrl());
        }
	}

	@Override
	public void updatePostsForUser(String userId, Set<Tweet> tweets) {
		// TODO Auto-generated method stub
		// Tìm User trong database và cập nhật danh sách Tweet
		String dict = userId;
        if (data.containsKey(dict)) {
            Optional<User> userOpt = data.get(dict).stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();

            if (userOpt.isPresent()) {
                userOpt.get().setTweets(tweets);
                System.out.println("Danh sách Tweet đã được cập nhật cho User: " + userId);
            } else {
                System.out.println("Không tìm thấy User: " + userId);
            }
        } else {
            System.out.println("Không tìm thấy từ điển: " + dict);
        }
	}
	
	public void updateFollowersForUser(String userId, Set<User> followers) {
		// TODO Auto-generated method stub
		// Tìm User trong database và cập nhật danh sách Tweet
		String dict = userId;
        if (data.containsKey(dict)) {
            Optional<User> userOpt = data.get(dict).stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();

            if (userOpt.isPresent()) {
                userOpt.get().setFollowers(followers);
                
                System.out.println("Danh sách Followers đã được cập nhật cho User: " + userId);
            } else {
                System.out.println("Không tìm thấy User: " + userId);
            }
        } else {
            System.out.println("Không tìm thấy từ điển: " + dict);
        }
	}

	@Override
	public void saveToJsonFile(String filePath) {
		// TODO Auto-generated method stub
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(filePath), data);
            System.out.println("Dữ liệu đã được lưu vào file JSON: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file JSON: " + e.getMessage());
        }
    }

	@Override
	public void loadFromJsonFile(String filePath) {
		// TODO Auto-generated method stub
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(filePath);
            if (file.exists()) {
                data = mapper.readValue(file, new TypeReference<Map<String, List<User>>>() {});
                System.out.println("Dữ liệu đã được tải từ file JSON.");
            } else {
                System.out.println("File JSON không tồn tại, tạo mới.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tải file JSON: " + e.getMessage());
        }
	}

}
