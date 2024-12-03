package manager;

import entities.Tweet;
import entities.User;

import java.util.Set;

public interface DataManagerInterface {
    void addUserToDataBase(User user);
    void updatePostsForUser(String userId, Set<Tweet> tweets);
    void updateFollowersForUser(String userId, Set<User> followers);
    void saveToJsonFile(String filePath);
    void loadFromJsonFile(String filePath);
    
}
