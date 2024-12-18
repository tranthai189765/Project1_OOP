package main;

import config.ConfigInterface;
import config.TwitterConfig;
import scraper.DataFetcherStrategy;
import scraper.SeleniumScraper;
import view.DataSyncGUI;
import view.DataViewer;

import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
public class Main {
    public static void main(String[] args) {
    	 // Tạo config và scraper
        ConfigInterface config = new TwitterConfig();
        DataFetcherStrategy scraper = new SeleniumScraper(config);

        // Khởi chạy giao diện đầu tiên
        DataSyncGUI dataSyncGUI = new DataSyncGUI(config, scraper);

        // Thêm WindowListener để lắng nghe sự kiện đóng cửa sổ của DataSyncGUI
        JFrame frame = dataSyncGUI.getFrame(); // Giả sử DataSyncGUI có phương thức getFrame() trả về JFrame
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Khi cửa sổ đầu tiên đóng, khởi chạy giao diện thứ hai
                SwingUtilities.invokeLater(() -> new DataViewer().createAndShowGUI(config));
            }
        });
    }
}

