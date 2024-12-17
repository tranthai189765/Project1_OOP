package main;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.DataFetcherStrategy;
import scraper.SeleniumScraper;
import view.DataSyncGUI;

public class Test {
	 public static void main(String[] args) {
	        // Tạo config và scraper
		    ConfigInterface config = new TwitterConfig();
	        DataFetcherStrategy scraper = new SeleniumScraper(config);

	        // Khởi chạy giao diện
	        new DataSyncGUI(config, scraper);
	    }
}

