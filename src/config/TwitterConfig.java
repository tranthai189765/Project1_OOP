package config;

public class TwitterConfig implements ConfigInterface {
	
	private final String baseName = "Twitter";
	private final String loginURL = "https://twitter.com/login";
	private final String exploreURL = "https://x.com/explore";
	private final String peopleTabXpath = "//*[@id='react-root']/div/div/div[2]/main/div/div/div/div[1]/div/div[1]/div[1]/div[2]/nav/div/div[2]/div/div[3]/a/div/div/span";
	private final String retryButtonXpath = "//span[contains(text(),'Retry') and contains(@class, 'css-1jxf684')]";
	private final String loginButtonXpath = "//span[text()='Log in']";
	private final String nextButtonXpath = "//span[text()='Next']";
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

}
