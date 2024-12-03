package entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")


public class User extends Node {
    private String url;
    private String location;
    private String professionalCategory;
    private String joinDate;
    private String website;
    private String tweetCount;
    private String followingCount;
    private String followersCount;
    private String description;

    @JsonSerialize(contentUsing = FollowerIdSerializer.class)
    private Set<User> followers;
    private Set<User> following;
     // Bỏ qua danh sách tweets khi serializing
    private Set<Tweet> tweets;

    // Constructor
    public User() {
        super();
    }
    
    public User(String id, String url) {
        super(id);
        this.url = url;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
        this.tweets = new HashSet<>();
    }
    
    public User(String url) {
    	this(generateId(url), url);
    }
    
    private static String generateId(String url) {
        return "user_"+url.substring(url.lastIndexOf("/") + 1); // Tạo id dựa trên timestamp
    }

    // Getter và Setter cho URL
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter và Setter cho Location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter và Setter cho Professional Category
    public String getProfessionalCategory() {
        return professionalCategory;
    }

    public void setProfessionalCategory(String professionalCategory) {
        this.professionalCategory = professionalCategory;
    }

    // Getter và Setter cho Join Date
    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    // Getter và Setter cho Website
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    // Getter và Setter cho Tweet Count
    public String getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(String tweetCount) {
        this.tweetCount = tweetCount;
    }

    // Getter và Setter cho Following Count
    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    // Getter và Setter cho Followers Count
    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    // Getter và Setter cho Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter và Setter cho Followers
    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    // Getter và Setter cho Following
    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    // Getter và Setter cho Tweets
    public Set<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
    }

    // Phương thức thêm follower
    public void addFollower(User follower) {
        followers.add(follower);
    }
    
    public boolean hasFollower(User user) {
        return followers.contains(user);
    }

    // Phương thức thêm following
    public void addFollowing(User followee) {
        following.add(followee);
    }

    // Phương thức kiểm tra xem người dùng có theo dõi người khác không
    public boolean isFollowing(User user) {
        return following.contains(user);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(getId(), user.getId()); // So sánh bằng ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId()); // Sử dụng ID để tạo hash
    }
}

