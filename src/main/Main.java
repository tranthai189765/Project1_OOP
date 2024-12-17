package main;

import java.io.IOException;
import java.util.Scanner;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.DataFetcherStrategy;
import synchronization.DataSyncManager;
import scraper.SeleniumScraper;

public class Main {
	    private static volatile boolean running = true;
		public static void main(String[] args) {
			
			    ConfigInterface config = new TwitterConfig();
		        DataFetcherStrategy scraper = new SeleniumScraper(config);
		        // Tạo thread lắng nghe lệnh "Stop" từ người dùng
		        Thread commandListener = new Thread(() -> {
		            Scanner scanner = new Scanner(System.in);
		            while (running) {
		                System.out.println("Nhập 'Stop' để dừng chương trình:");
		                String command = scanner.nextLine();
		                if (command.equalsIgnoreCase("Stop")) {
		                    running = false; // Thay đổi trạng thái để thoát vòng lặp
		                    System.out.println("Lệnh Stop nhận được. Chương trình đang dừng...");
		                    DataSyncManager.updateLocalDatabasefromThreads(config,2);
		                    try {
								DataSyncManager.updateLocalFileDataBaseFromThreads(config.getUsersFoundFilePath());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                    System.exit(0);
		                }
		            }
		            scanner.close();
		        });
		        commandListener.start(); // Bắt đầu thread lắng nghe
		
		        System.out.println("Chương trình đang chạy... Nhấn Stop để dừng.");
		        try {
		        	while (running) {
		        	    scraper.fetchUserByHashtagsMultiThreads(2);
		        	    try {
							DataSyncManager.updateLocalFileDataBaseFromThreads(config.getUsersFoundFilePath());
							scraper.fetchProfileMultiThreads(2);
							DataSyncManager.updateLocalDatabasefromThreads(config,2);
							scraper.fetchFollowersMultiThreads(2);
							DataSyncManager.updateLocalDatabasefromThreads(config,2);
							scraper.fetchTweetsMultiThreads(2);
							DataSyncManager.updateLocalDatabasefromThreads(config,2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	//	scraper.fetchFollowersMultiThreads("processed_kol_links.txt",5);
		        	running = false;
		        	Thread.sleep(1000);
		        	}
		        }catch (InterruptedException e) {
		            System.err.println("Thread bị gián đoạn: " + e.getMessage());
		        } finally {
		            System.out.println("Chương trình kết thúc.");
		        }
}
}
