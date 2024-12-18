package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import config.ConfigInterface;
import synchronization.DataSyncManager;
import scraper.DataFetcherStrategy;

public class DataSyncGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chỉ giải phóng JFrame này
        frame.setLayout(new CardLayout());

        // Panel màn hình chính
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("DataSyncGUI is closing (windowClosing).");
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("DataSyncGUI is closed (windowClosed).");
            }
        });
        
        mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Analyze Twitter KOL Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel(new GridLayout(1, 2));

        // Cột 1: Users đã tìm được
        JPanel usersPanel = new JPanel(new BorderLayout());
        JLabel usersLabel = new JLabel("Các users đã tìm được", SwingConstants.CENTER);
        usersPanel.add(usersLabel, BorderLayout.NORTH);

        JTextArea usersTextArea = new JTextArea();
        usersTextArea.setEditable(false);
        JScrollPane usersScrollPane = new JScrollPane(usersTextArea);
        usersPanel.add(usersScrollPane, BorderLayout.CENTER);

        try {
            String usersContent = new String(Files.readAllBytes(Paths.get(config.getUsersFoundFilePath())));
            usersTextArea.setText(usersContent);
        } catch (IOException e) {
            usersTextArea.setText("Không thể tải dữ liệu từ file.");
        }

        // Cột 2: Hashtags liên quan đến Blockchain
        JPanel hashtagsPanel = new JPanel(new BorderLayout());
        JLabel hashtagsLabel = new JLabel("Các hashtags liên quan tới lĩnh vực Blockchain", SwingConstants.CENTER);
        hashtagsPanel.add(hashtagsLabel, BorderLayout.NORTH);

        JTextArea hashtagsTextArea = new JTextArea();
        hashtagsTextArea.setEditable(false);
        JScrollPane hashtagsScrollPane = new JScrollPane(hashtagsTextArea);
        hashtagsPanel.add(hashtagsScrollPane, BorderLayout.CENTER);

        try {
            String hashtagsContent = new String(Files.readAllBytes(Paths.get(config.getHashTagsFilePath())));
            hashtagsTextArea.setText(hashtagsContent);
        } catch (IOException e) {
            hashtagsTextArea.setText("Không thể tải dữ liệu từ file.");
        }

        dataPanel.add(usersPanel);
        dataPanel.add(hashtagsPanel);

        mainPanel.add(dataPanel, BorderLayout.CENTER);

        JPanel questionPanel = new JPanel();
        JLabel questionLabel = new JLabel("Bạn có muốn tìm thêm users không?");
        JButton yesButton = new JButton("Có");
        JButton noButton = new JButton("Không");

        questionPanel.add(questionLabel);
        questionPanel.add(yesButton);
        questionPanel.add(noButton);
        mainPanel.add(questionPanel, BorderLayout.SOUTH);

        // Action khi chọn Có
        yesButton.addActionListener(e -> configureUserSearch());

        // Action khi chọn Không
        noButton.addActionListener(e -> proceedToNextStep());

        frame.add(mainPanel, "main");
        initializeProcessUI();
        frame.setVisible(true);
    }

    public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	private void initializeProcessUI() {
        // Panel giao diện xử lý
        processPanel = new JPanel(new BorderLayout());

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

    private void configureUserSearch() {
        String maxUsersInput = JOptionPane.showInputDialog(frame, "Nhập số lượng users tối đa cho mỗi hashtag:");
        try {
            int maxUsers = Integer.parseInt(maxUsersInput);
            if (maxUsers <= 0) throw new NumberFormatException();
            config.setMaxUsers(maxUsers);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập một số nguyên dương hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String threadsInput = JOptionPane.showInputDialog(frame, "Nhập số luồng bạn muốn sử dụng (Cảnh báo: nhiều luồng sẽ ảnh hưởng CPU):");
        try {
            numberOfThreads = Integer.parseInt(threadsInput);
            if (numberOfThreads <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Vui lòng nhập một số nguyên dương hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switchToProcessUI();
    }

    private void proceedToNextStep() {
        currentStep = 1; // Bước tiếp theo
        switchToProcessUI();
    }

    private void switchToProcessUI() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "process");
        executeNextStep();
    }

    private void displayFileContent() {
        try {
            String filePath;
            if (currentStep == 1) {
                filePath = config.getUsersFoundFilePath();
            } else {
                filePath = config.getLocalManager().getDatabasefilepath();
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            textArea.setText(content);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Lỗi khi đọc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeNextStep() {
        textArea.setText(""); // Xóa nội dung cũ
        String title = ""; // Tiêu đề của bước hiện tại
        String filePath = ""; // Đường dẫn file dữ liệu tương ứng

        // Lấy dữ liệu phù hợp với bước hiện tại
        try {
            switch (currentStep) {
                case 0:
                    title = "users từ hashtags";
                    filePath = config.getUsersFoundFilePath();
                    break;
                case 1:
                    title = "Profile của các users thu thập được";
                    filePath = config.getLocalManager().getDatabasefilepath();
                    break;
                case 2:
                    title = "Followers của các KOL";
                    filePath = config.getLocalManager().getDatabasefilepath();
                    break;
                case 3:
                    title = "Tweets của các KOL";
                    filePath = config.getLocalManager().getDatabasefilepath();
                    break;
                default:
                	 // Hiển thị thông báo hoàn thành
                    statusLabel.setText("Hoàn thành tất cả các bước!");
                    JOptionPane.showMessageDialog(frame, 
                        "Bạn sẽ được chuyển qua kết quả sau khi phân tích dữ liệu.", 
                        "Hoàn thành", 
                        JOptionPane.INFORMATION_MESSAGE);

                    // Đóng giao diện sau 2 giây
                    Timer timer = new Timer(2000, e -> {
                        frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING));
                    });
                    timer.setRepeats(false);
                    timer.start();
                    return;
            }

            // Hiển thị dữ liệu trong textArea
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            textArea.setText(content);
        } catch (IOException ex) {
            textArea.setText("Không thể tải dữ liệu từ file: " + ex.getMessage());
        }

        // Cập nhật trạng thái và kích hoạt nút "Tiếp tục"
        statusLabel.setText("Bạn vui lòng nhấn 'Tiếp tục'.");
        viewDataButton.setEnabled(false);
        continueButton.setEnabled(true);

        // Xóa các listener cũ trước khi thêm mới
        for (ActionListener al : continueButton.getActionListeners()) {
            continueButton.removeActionListener(al);
        }

        // Thêm listener mới
        String finalTitle = title; // Dùng biến cố định để truyền vào lambda
        continueButton.addActionListener(e -> showConfirmationDialog(finalTitle));
    }

    private void showConfirmationDialog(String title) {
        // Hiển thị hộp thoại xác nhận
        int userChoice = JOptionPane.showConfirmDialog(
            frame,
            "Bạn có muốn tiếp tục thu thập dữ liệu về " + title + "?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (userChoice == JOptionPane.NO_OPTION) {
            // Người dùng chọn Không -> chuyển sang bước tiếp theo
            currentStep++;
            executeNextStep();
            return;
        }

        // Người dùng chọn Có -> Bắt đầu xử lý dữ liệu cho bước hiện tại
        statusLabel.setText("Đang thực hiện bước: " + title + "...");
        new Thread(() -> {
            try {
                switch (currentStep) {
                    case 0:
                        scraper.fetchUserByHashtagsMultiThreads(numberOfThreads);
                        DataSyncManager.updateLocalFileDatabaseFromThreads(config.getUsersFoundFilePath());
                        break;
                    case 1:
                        int numberOfThreadsProfile = Integer.parseInt(JOptionPane.showInputDialog(frame, "Nhập số luồng cho bước này:"));
                        scraper.fetchProfileMultiThreads(numberOfThreadsProfile);
                        DataSyncManager.updateLocalDatabaseFromThreads(config, numberOfThreadsProfile);
                        break;
                    case 2:
                        int numberOfThreadsFollowers = Integer.parseInt(JOptionPane.showInputDialog(frame, "Nhập số luồng cho bước này:"));
                        int maxFollowers = Integer.parseInt(JOptionPane.showInputDialog(frame, "Nhập số lượng followers tối đa:"));
                        config.setMaxFollowers(maxFollowers);
                        scraper.fetchFollowersMultiThreads(numberOfThreadsFollowers);
                        DataSyncManager.updateLocalDatabaseFromThreads(config, numberOfThreadsFollowers);
                        break;
                    case 3:
                        int numberOfThreadsTweets = Integer.parseInt(JOptionPane.showInputDialog(frame, "Nhập số luồng cho bước này:"));
                        int maxTweets = Integer.parseInt(JOptionPane.showInputDialog(frame, "Nhập số lượng tweets tối đa:"));
                        config.setMaxFollowers(maxTweets);
                        scraper.fetchTweetsMultiThreads(numberOfThreadsTweets);
                        DataSyncManager.updateLocalDatabaseFromThreads(config, numberOfThreadsTweets);
                        break;
                }

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Bước " + (currentStep + 1) + " hoàn thành.");
                    currentStep++;
                    executeNextStep(); // Chuyển sang bước tiếp theo
                });
            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Lỗi khi thực hiện bước " + (currentStep + 1) + ": " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }



}
