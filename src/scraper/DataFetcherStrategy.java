package scraper;

public interface DataFetcherStrategy {
	void fetchUserByHashtagsMultiThreads(int threadCount);
    void fetchProfileMultiThreads(int threadCount);
    void fetchFollowersMultiThreads(int threadCount);
    void fetchTweetsMultiThreads(int threadCount);
}

//-> 4 phương thức cơ bản để giải quyết bài toán


//-> Selenium 
//-> API 