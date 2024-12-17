package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import config.ConfigInterface;
import synchronization.DataSyncManager;
import scraper.SeleniumScraper;
import scraper.DataFetcherStrategy;

public class DataSyncGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel processPanel;
    private JTextArea textArea;
    private JButton viewDataButton;
    private JButton continueButton;
    private JLabel statusLabel;

    private ConfigInterface config;
    private DataFetcherStrategy scraper;

    private int currentStep = 0; // Theo dõi bước hiện tại
    private int numberOfThreads = 2; // Số luồng mặc định

    public DataSyncGUI(ConfigInterface config, DataFetcherStrategy scraper) {
        this.config = config;
        this.scraper = scraper;
        initializeMainUI();
    }

    private void initializeMainUI() {
        // Tạo JFrame chính
        frame = new JFrame("Analyze Twitter KOL Data");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());

        // Panel màn hình chính
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Analyze Twitter KOL Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        JLabel threadInputLabel = new JLabel("Hãy nhập số luồng mà bạn muốn sử dụng:", SwingConstants.CENTER);
        threadInputLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(threadInputLabel);

        JTextField threadInputField = new JTextField();
        inputPanel.add(threadInputField);

        JLabel warningLabel = new JLabel("Lưu ý: Sử dụng nhiều luồng có thể ảnh hưởng tới CPU của bạn", SwingConstants.CENTER);
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        warningLabel.setForeground(Color.RED);
        inputPanel.add(warningLabel);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(startButton, BorderLayout.SOUTH);

        // Action khi nhấn Start
        startButton.addActionListener(e -> {
            try {
                numberOfThreads = Integer.parseInt(threadInputField.getText());
                if (numberOfThreads <= 0) {
                    JOptionPane.showMessageDialog(frame, "Số luồng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                switchToProcessUI();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập một số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(mainPanel, "main");
        initializeProcessUI();
        frame.setVisible(true);
    }

    private void initializeProcessUI() {
        // Panel giao diện xử lý
        processPanel = new JPanel();
        processPanel.setLayout(new BorderLayout());

        // JLabel trạng thái
        statusLabel = new JLabel("Chương trình bắt đầu...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        processPanel.add(statusLabel, BorderLayout.NORTH);

        // JTextArea hiển thị nội dung file
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        processPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel();
        viewDataButton = new JButton("Xem dữ liệu đã cập nhật");
        continueButton = new JButton("Tiếp tục");

        viewDataButton.setEnabled(false);
        continueButton.setEnabled(false);

        buttonPanel.add(viewDataButton);
        buttonPanel.add(continueButton);
        processPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action cho nút "Xem dữ liệu đã cập nhật"
        viewDataButton.addActionListener(e -> displayFileContent());

        // Action cho nút "Tiếp tục"
        continueButton.addActionListener(e -> executeNextStep());

        frame.add(processPanel, "process");
    }

    private void switchToProcessUI() {
        // Chuyển từ màn hình chính sang màn hình xử lý
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "process");
        executeNextStep();
    }

    private void displayFileContent() {
        try {
            String filePath;
            if (currentStep == 1) {
                filePath = config.getUsersFoundFilePath(); // File TXT bước đầu tiên
            } else {
                filePath = config.getLocalManager().getDatabasefilepath(); // File JSON các bước sau
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            textArea.setText(content);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Lỗi khi đọc dữ liệu: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeNextStep() {
        viewDataButton.setEnabled(false);
        continueButton.setEnabled(false);
        textArea.setText("");
        statusLabel.setText("Waiting... Đang thực hiện bước " + (currentStep + 1));

        new Thread(() -> {
            try {
                switch (currentStep) {
                    case 0:
                        scraper.fetchUserByHashtagsMultiThreads(numberOfThreads);
                        DataSyncManager.updateLocalFileDataBaseFromThreads(config.getUsersFoundFilePath());
                        break;
                    case 1:
                        scraper.fetchProfileMultiThreads(numberOfThreads);
                        DataSyncManager.updateLocalDatabasefromThreads(config, numberOfThreads);
                        break;
                    case 2:
                        scraper.fetchFollowersMultiThreads(numberOfThreads);
                        DataSyncManager.updateLocalDatabasefromThreads(config, numberOfThreads);
                        break;
                    case 3:
                        scraper.fetchTweetsMultiThreads(numberOfThreads);
                        DataSyncManager.updateLocalDatabasefromThreads(config, numberOfThreads);
                        break;
                    default:
                        statusLabel.setText("Hoàn thành tất cả các bước.");
                        return;
                }

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Bước " + (currentStep + 1) + " hoàn thành.");
                    viewDataButton.setEnabled(true);
                    continueButton.setEnabled(true);
                });

                currentStep++;
            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame,
                        "Lỗi khi thực hiện bước " + (currentStep + 1) + ": " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
