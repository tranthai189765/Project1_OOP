package entities;

import java.util.HashSet;
import java.util.Set;


public class Tweet extends Node {

  
    private final String author_id;
    
    //Url
    // User author = new User(Url)
    // author_id = author.id;
    // link : "x/abc.com" -> author_id = "user_abc"

    // set author_id (String url)
    // {
    //    this.author_id = extract(url);
    //
    
    // set author_id (String id)
    //{  this.author_id = id;
    //
    private Set<String> commentedBy;
    private String url;
    
    private String likeCount;
    private String viewCount;
    private String content;
    private String postedDate;
    private String repostCount;
    private String commentCount;
    
    public Tweet() {
        super();
        this.commentedBy = new HashSet<>();
        this.author_id = null;
    }

    public Tweet(String id, String author_id) {
        super(id);
        this.author_id = author_id;
        this.commentedBy = new HashSet<>();
    }
    
    // Tạo ID cho tweet từ URL
    private static String generateID(String linkURL) {
        String username = linkURL.substring(linkURL.indexOf("https://x.com/") + "https://x.com/".length(), linkURL.lastIndexOf("/status"));
        String tweetId = linkURL.substring(linkURL.lastIndexOf("/") + 1);
        return "tweet_" + username + "_" + tweetId;
    }

    // Constructor sử dụng URL
    public Tweet(String url) {
        this(generateID(url), extractAuthor_id(url)); // Gọi constructor với ID được tạo
    }

    // Lấy author_id từ URL
    private static String extractAuthor_id(String linkURL) {
        if (linkURL != null && linkURL.contains("https://x.com/")) {
            return "user_" + linkURL.substring(linkURL.indexOf("https://x.com/") + "https://x.com/".length(), linkURL.lastIndexOf("/status"));
        }
        return null; // Trả về null nếu URL không hợp lệ
    }

	public String getAuthor_id() {
		return author_id;
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

	@Override
	public String toString() {
	    return "Tweet{" +
	           "id='" + getId() + '\'' +
	           ", author_id='" + author_id + '\'' +
	           ", likeCount='" + likeCount + '\'' +
	           ", viewCount='" + viewCount + '\'' +
	           ", content='" + content + '\'' +
	           ", postedDate='" + postedDate + '\'' +
	           ", repostCount='" + repostCount + '\'' +
	           ", commentCount='" + commentCount + '\'' +
	           ", commentedBy=" + commentedBy.size() + " commented" +
	           '}';
	}

	// Phương thức thêm user_id vào danh sách commentedBy
	public void addCommentedUser(String userId) {
	    commentedBy.add(userId);
	}

	// Phương thức kiểm tra xem user_id có trong danh sách commentedBy không
	public boolean hasCommented(String userId) {
	    return commentedBy != null && commentedBy.contains(userId);
	}

}
