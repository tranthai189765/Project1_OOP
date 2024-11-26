package data;

import java.util.HashSet;
import java.util.Set;

public class User extends Node {
    private String url;
    private Set<User> followers;
    private Set<User> following;

    // Constructor
    public User(String id, String url) {
    	super(id);
        this.url = url;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    // Getter và Setter cho name
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
