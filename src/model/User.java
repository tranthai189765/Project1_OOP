package model;

import java.util.HashSet;
import java.util.Set;

public class User {
	private String id;
    private String name;
    private Set<User> followers;
    private Set<User> following;

    // Constructor
    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    // Getter và Setter cho id và name
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Phương thức thêm follower
    public void addFollower(User follower) {
        followers.add(follower);
    }

    // Phương thức thêm following
    public void addFollowing(User followee) {
        following.add(followee);
    }

    // Getter và Setter cho followers và following
    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    // Phương thức kiểm tra xem người dùng có theo dõi người khác không
    public boolean isFollowing(User user) {
        return following.contains(user);
    }
}
