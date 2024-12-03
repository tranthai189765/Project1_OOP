package scraper;

import entities.User;

public interface DataFetcherStrategy {
    void fetchProfile(User kol);
    void fetchFollowers(User kol);
    void fetchTweets(User kol);
    void fetchProfileFromKOLFile(String filepath);
    void fetchFollowersFromKOLFile(String filepath);
    void fetchTweetsFromKOLFile(String filepath);
}