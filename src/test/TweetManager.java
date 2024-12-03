package test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import entities.Tweet;
import entities.User;

public class TweetManager {
    private Map<String, List<User>> dataBase;

    public TweetManager() {
        this.dataBase = new HashMap<>();
    }

    public void addUserToDataBase(String dict, User user) {
        dataBase.computeIfAbsent(dict, k -> new ArrayList<>());
        
        // Kiểm tra nếu User đã có trong danh sách
        if (!dataBase.get(dict).contains(user)) {
            dataBase.get(dict).add(user);
            System.out.println("Đã thêm User: " + user.getUrl());
        } else {
            System.out.println("User đã tồn tại: " + user.getUrl());
        }
    }

    public void updateTweetsForUser(String dict, String userName, Set<Tweet> tweets) {
        // Tìm User trong database và cập nhật danh sách Tweet
        if (dataBase.containsKey(dict)) {
            Optional<User> userOpt = dataBase.get(dict).stream()
                .filter(user -> user.getUrl().equals(userName))
                .findFirst();

            if (userOpt.isPresent()) {
                userOpt.get().setTweets(tweets);
                System.out.println("Danh sách Tweet đã được cập nhật cho User: " + userName);
            } else {
                System.out.println("Không tìm thấy User: " + userName);
            }
        } else {
            System.out.println("Không tìm thấy từ điển: " + dict);
        }
    }

    public void saveToJsonFile(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(filePath), dataBase);
            System.out.println("Dữ liệu đã được lưu vào file JSON: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file JSON: " + e.getMessage());
        }
    }

    public void loadFromJsonFile(String filePath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(filePath);
            if (file.exists()) {
                dataBase = mapper.readValue(file, new TypeReference<Map<String, List<User>>>() {});
                System.out.println("Dữ liệu đã được tải từ file JSON.");
            } else {
                System.out.println("File JSON không tồn tại, tạo mới.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tải file JSON: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TweetManager manager = new TweetManager();

        // Tải dữ liệu từ file JSON
        String filePath = "test.json";
        manager.loadFromJsonFile(filePath);

        // Thêm User
        User user1 = new User("https://x.com/CitizenBitcoin");
        manager.addUserToDataBase("blockchain", user1);

        // Tạo danh sách Tweet
        Set<Tweet> tweets = new HashSet<>();
        tweets.add(new Tweet("1", user1));
        tweets.add(new Tweet("2", user1));
        tweets.add(new Tweet("3", user1));

        // Cập nhật danh sách Tweet
        manager.updateTweetsForUser("blockchain", "https://x.com/CitizenBitcoin", tweets);

        // Lưu vào file JSON
        manager.saveToJsonFile(filePath);
    }
}
