package config;

import java.util.List;

import filehandler.FileHandlerInterface;
import manager.DataManagerInterface;
public interface ConfigInterface {
	String getBaseName();
	String getLoginUrl();
    String getExploreUrl();
    String getPeopleTabXpath();
    String getRetryButtonXpath();
    String getLoginButtonXpath();
    String getNextButtonXpath();
    List<String> getPathToFollowers(String url);
    List<String> getPathToFollowees(String url);
    DataManagerInterface newManager(String databasefilepath);
    FileHandlerInterface newFileHandler();
    String getCompletedHashtagsFilePath();
    String getUsersFoundFilePath();
    int getMaxUsers();
    int getMaxFollowers();
    int getMaxTweets();
    int getMaxComments();
    String getGraphFilePath();
    DataManagerInterface getLocalManager();
    String getHashTagsFilePath();
    String getKolFilePath();
}
