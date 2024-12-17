package config;

import java.util.ArrayList;
import java.util.List;

import filehandler.FileHandlerInterface;
import filehandler.TwitterFileHandler;
import manager.DataManagerInterface;
import manager.TwitterDataManager;

public class TwitterConfig implements ConfigInterface {
	
	private final String baseName = "Twitter";
	private final String loginURL = "https://twitter.com/login";
	private final String exploreURL = "https://x.com/explore";
	private final String peopleTabXpath = "//*[@id='react-root']/div/div/div[2]/main/div/div/div/div[1]/div/div[1]/div[1]/div[2]/nav/div/div[2]/div/div[3]/a/div/div/span";
	private final String retryButtonXpath = "//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]";
	private final String loginButtonXpath = "//span[text()='Log in']";
	private final String nextButtonXpath = "//span[text()='Next']";
	
	private int maxUsers = 2;
	private int maxFollowers = 5;
	private int maxTweets = 2;
	private int maxComments = 30;
	private String graphFilePath =  "new_graph.gexf"; 
	private DataManagerInterface localManager = new TwitterDataManager("new_database.json");
	private String hashtagsFilePath = "hashtags.txt";
	private String kolFilePath = "new_kol_links.txt";
	private String completedHashtagsFilePath = "new_completed_hashtags.txt";
	private String usersFoundFilePath = "new_all_user_links.txt";

	@Override
	public String getBaseName() {
		// TODO Auto-generated method stub
		return this.baseName;
	}

	@Override
	public String getLoginUrl() {
		// TODO Auto-generated method stub
		return this.loginURL;
	}

	@Override
	public String getExploreUrl() {
		// TODO Auto-generated method stub
		return this.exploreURL;
	}

	@Override
	public String getPeopleTabXpath() {
		// TODO Auto-generated method stub
		return this.peopleTabXpath;
	}

	@Override
	public String getRetryButtonXpath() {
		// TODO Auto-generated method stub
		return this.retryButtonXpath;
	}

	@Override
	public String getLoginButtonXpath() {
		// TODO Auto-generated method stub
		return this.loginButtonXpath;
	}

	@Override
	public String getNextButtonXpath() {
		// TODO Auto-generated method stub
		return this.nextButtonXpath;
	}

	@Override
	public List<String> getPathToFollowers(String url) {
		List<String> paths = new ArrayList<String>();
		String firstPath = url + "/verified_followers";
		String secondPath = url + "/followers";
		paths.add(firstPath);
		paths.add(secondPath);
		return paths;
	}

	@Override
	public List<String> getPathToFollowees(String url) {
		List<String> paths = new ArrayList<String>();
		String firstPath = url + "/following";
		paths.add(firstPath);
		return paths;
	}

	@Override
	public DataManagerInterface newManager(String databasefilepath) {
		// TODO Auto-generated method stub
		return new TwitterDataManager(databasefilepath);
	}

	@Override
	public FileHandlerInterface newFileHandler() {
		// TODO Auto-generated method stub
		return new TwitterFileHandler();
	}

	@Override
	public String getCompletedHashtagsFilePath() {
		// TODO Auto-generated method stub
		return this.completedHashtagsFilePath;
	}

	@Override
	public String getUsersFoundFilePath() {
		// TODO Auto-generated method stub
		return this.usersFoundFilePath;
	}

	@Override
	public int getMaxUsers() {
		// TODO Auto-generated method stub
		return this.maxUsers;
	}

	@Override
	public int getMaxFollowers() {
		// TODO Auto-generated method stub
		return this.maxFollowers;
	}

	@Override
	public int getMaxTweets() {
		// TODO Auto-generated method stub
		return this.maxTweets;
	}

	@Override
	public int getMaxComments() {
		// TODO Auto-generated method stub
		return this.maxComments;
	}

	public void setMaxFollowers(int maxFollowers) {
		this.maxFollowers = maxFollowers;
	}

	public void setMaxTweets(int maxTweets) {
		this.maxTweets = maxTweets;
	}

	public void setMaxComments(int maxComments) {
		this.maxComments = maxComments;
	}

	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}

	@Override
	public String getGraphFilePath() {
		// TODO Auto-generated method stub
		return this.graphFilePath;
	}

	@Override
	public DataManagerInterface getLocalManager() {
		// TODO Auto-generated method stub
		return this.localManager;
	}

	public void setGraphFilePath(String graphFilePath) {
		this.graphFilePath = graphFilePath;
	}

	public void setLocalManager(DataManagerInterface localManager) {
		this.localManager = localManager;
	}

	@Override
	public String getHashTagsFilePath() {
		// TODO Auto-generated method stub
		return hashtagsFilePath;
	}

	public void setHashtagsFilePath(String hashtagsFilePath) {
		this.hashtagsFilePath = hashtagsFilePath;
	}

	public String getKolFilePath() {
		return kolFilePath;
	}

	public void setKolFilePath(String kolFilePath) {
		this.kolFilePath = kolFilePath;
	}

	public void setUsersFoundFilePath(String usersFoundFilePath) {
		this.usersFoundFilePath = usersFoundFilePath;
	}
	
}
