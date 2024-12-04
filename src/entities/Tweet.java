package entities;



import java.util.HashSet;
import java.util.Set;


public class Tweet extends Node {

  
    private String author_id;
    
    //Url
    // User author = new User(Url)
    // author_id = author.id;
    // link : "x/abc.com" -> author_id = "user_abc"

    private Set<String> commentedBy;
    
    private String likeCount;
    private String viewCount;
    private String content;
    private String postedDate;
    private String repostCount;
    private String commentCount;
    
    public Tweet() {
        super();
        this.commentedBy = new HashSet<>();
    }

    public Tweet(String id, String author_id) {
        super(id);
        this.author_id = author_id;
        this.commentedBy = new HashSet<>();
    }

    public String getAuthorId() {
        return author_id;
    }

    public void setAuthorId(String author_id) {
        this.author_id = author_id;
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
