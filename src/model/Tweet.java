package model;

import java.util.HashSet;
import java.util.Set;

public class Tweet {
    private String id;
    private User author;
    private String content;
    private Set<User> retweetedBy;
    private Set<User> commentedBy;  

    // Constructor
    public Tweet(String id, User author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.retweetedBy = new HashSet<>();
        this.commentedBy = new HashSet<>();  
    }

    // Getter và Setter cho id, author và content
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter và Setter cho retweetedBy và commentedBy
    public Set<User> getRetweetedBy() {
        return retweetedBy;
    }

    public void setRetweetedBy(Set<User> retweetedBy) {
        this.retweetedBy = retweetedBy;
    }

    public Set<User> getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(Set<User> commentedBy) {
        this.commentedBy = commentedBy;
    }

    // Phương thức thêm người retweet
    public void addRetweet(User user) {
        retweetedBy.add(user);
    }

    // Phương thức thêm người comment
    public void addComment(User user) {
        commentedBy.add(user);
    }

    // Phương thức kiểm tra xem người dùng đã retweet tweet này chưa
    public boolean isRetweetedBy(User user) {
        return retweetedBy.contains(user);
    }

    // Phương thức kiểm tra xem người dùng đã comment tweet này chưa
    public boolean isCommentedBy(User user) {
        return commentedBy.contains(user);
    }
}
