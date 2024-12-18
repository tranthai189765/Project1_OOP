package entities;

import java.util.HashSet;
import java.util.Set;

public class Tweet extends Node {
    
    private String authorId;
    private Set<String> commentedBy;
    private String url;
    private String likeCount;
    private String viewCount;
    private String content;
    private String postedDate;
    private String repostCount;
    private String commentCount;

    // Constructor mặc định
    public Tweet() {
        super();
        this.commentedBy = new HashSet<>();
        this.authorId = null;
    }

    // Constructor với ID và authorId
    public Tweet(String id, String authorId) {
        super(id);
        this.authorId = authorId;
        this.commentedBy = new HashSet<>();
    }

    // Constructor sử dụng URL
    public Tweet(String url) {
        this(generateId(url), extractAuthorId(url)); // Gọi constructor với ID được tạo
        this.url = url;
    }

    // Tạo ID cho tweet từ URL
    private static String generateId(String linkUrl) {
        String username = linkUrl.substring(linkUrl.indexOf("https://x.com/") + "https://x.com/".length(), linkUrl.lastIndexOf("/status"));
        String tweetId = linkUrl.substring(linkUrl.lastIndexOf("/") + 1);
        return "tweet_" + username + "_" + tweetId;
    }

    // Lấy authorId từ URL
    private static String extractAuthorId(String linkUrl) {
        if (linkUrl != null && linkUrl.contains("https://x.com/")) {
            return "user_" + linkUrl.substring(linkUrl.indexOf("https://x.com/") + "https://x.com/".length(), linkUrl.lastIndexOf("/status"));
        }
        return null; 
    }

    // Getter và setter
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Set<String> getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(Set<String> commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(String repostCount) {
        this.repostCount = repostCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Chuyển đổi đối tượng thành chuỗi thông tin
    @Override
    public String toString() {
        return "Tweet{" +
               "id='" + getId() + '\'' +
               ", authorId='" + authorId + '\'' +
               ", likeCount='" + likeCount + '\'' +
               ", viewCount='" + viewCount + '\'' +
               ", content='" + content + '\'' +
               ", postedDate='" + postedDate + '\'' +
               ", repostCount='" + repostCount + '\'' +
               ", commentCount='" + commentCount + '\'' +
               ", commentedBy=" + commentedBy.size() + " commented" +
               '}';
    }

    // Thêm userId vào danh sách commentedBy
    public void addCommentedUser(String userId) {
        commentedBy.add(userId);
    }

    // Kiểm tra xem userId có trong danh sách commentedBy không
    public boolean hasCommented(String userId) {
        return commentedBy != null && commentedBy.contains(userId);
    }
}
