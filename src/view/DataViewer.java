package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Stack;
import java.awt.event.KeyAdapter;

import org.json.JSONTokener;



import org.json.JSONObject;

public class DataViewer {
	    private JTabbedPane tabbedPane;
	    private Stack<Integer> tabHistory;

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> new DataViewer().createAndShowGUI());
	    }

	    private void createAndShowGUI() {
	        JFrame frame = new JFrame("Data Viewer");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(900, 700);

	        tabbedPane = new JTabbedPane();
	        tabHistory = new Stack<>();

	        // Tạo bảng chính
	        JPanel mainPanel = createMainPanel();
	        tabbedPane.addTab("Main", mainPanel);

	        // Thêm điều hướng tab
	        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        JButton closeButton = new JButton("Close Tab");
	        closeButton.addActionListener(e -> closeCurrentTab());
	        navigationPanel.add(closeButton);

	        frame.add(navigationPanel, BorderLayout.SOUTH);
	        frame.add(tabbedPane, BorderLayout.CENTER);
	        frame.setVisible(true);
	    }


	    private JPanel createMainPanel() {
	        JPanel panel = new JPanel(new BorderLayout());

	        JTable table = new JTable();
	        
	        DefaultTableModel tableModel = new DefaultTableModel() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };
	        table.setModel(tableModel);
	        table.setAutoCreateRowSorter(true);
	        table.setRowHeight(25);
	        table.setFont(new Font("Arial", Font.PLAIN, 14));
	        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
	        
	        
	        JScrollPane scrollPane = new JScrollPane(table);
	        
	        

	        // Load dữ liệu mặc định
	        loadDataFromFile(tableModel, "PageRankResults.txt");
	        //table.setToolTipText("Double click on NodeID to see more details.");
	        
	        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("NodeID")).setCellRenderer(new GreenBackgroundRenderer());
	        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Rank")).setCellRenderer(new BlueBackgroundRenderer());
	        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Score")).setCellRenderer(new RedBackgroundRenderer());

	        // Xử lý sự kiện double click
	        table.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                if (e.getClickCount() == 2) {
	                    int row = table.getSelectedRow();
	                    int col = table.getSelectedColumn();

	                    if (row != -1 && col != -1 && table.getColumnName(col).equals("NodeID")) {
	                        String nodeId = table.getValueAt(row, col).toString();
	                        showNodeDetails(nodeId);
	                    }
	                }
	            }
	        });

	        // Thanh tìm kiếm
	        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        JLabel searchLabel = new JLabel("Search NodeID: ");
	        JTextField searchField = new JTextField(15);
	        searchField.addKeyListener(new KeyAdapter() {
	            @Override
	            public void keyReleased(KeyEvent e) {
	                String query = searchField.getText();
	                filterTable(query, tableModel, table);
	            }
	        });
	        searchPanel.add(searchLabel);
	        searchPanel.add(searchField);

	        // Thêm note vào searchPanel
	        JLabel userNote = new JLabel("Double click on NodeID to see more details.", SwingConstants.LEFT);
	        userNote.setFont(new Font("Arial", Font.ITALIC, 15));
	        userNote.setForeground(Color.GRAY);
	        searchPanel.add(userNote);

	        // Thanh sắp xếp
	        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        JLabel sortLabel = new JLabel("Sort By: ");
	        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"NodeID", "Rank"});
	        JButton sortButton = new JButton("Sort");
	        JCheckBox ascendingCheckBox = new JCheckBox("Ascending", true);

	        sortButton.addActionListener(e -> {
	            String columnName = (String) sortComboBox.getSelectedItem();
	            boolean ascending = ascendingCheckBox.isSelected();
	            sortTable(columnName, table, ascending);
	        });

	        sortPanel.add(sortLabel);
	        sortPanel.add(sortComboBox);
	        sortPanel.add(ascendingCheckBox);
	        sortPanel.add(sortButton);

	        // Kết hợp Search và Sort
	        JPanel controlsPanel = new JPanel(new BorderLayout());
	        controlsPanel.add(searchPanel, BorderLayout.WEST);
	        controlsPanel.add(sortPanel, BorderLayout.EAST);

	        // Thêm vào panel chính
	        panel.add(controlsPanel, BorderLayout.NORTH);
	        panel.add(scrollPane, BorderLayout.CENTER);

	        return panel;
	    }

	    private void loadDataFromFile(DefaultTableModel tableModel, String filePath) {
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            tableModel.setRowCount(0);
	            tableModel.setColumnCount(0);

	            if ((line = br.readLine()) != null) {
	                String[] columns = line.trim().split("\\s{2,}");
	                for (String column : columns) {
	                    tableModel.addColumn(column);
	                }
	            }

	            while ((line = br.readLine()) != null) {
	                String[] data = line.trim().split("\\s{2,}");
	                tableModel.addRow(data);
	            }
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(null, "Error loading file: " + ex.getMessage());
	        }
	    }

	    private void showNodeDetails(String nodeId) {
	        String jsonFilePath = "database.json";
	        try (FileReader reader = new FileReader(jsonFilePath)) {
	            JSONTokener tokener = new JSONTokener(reader);
	            JSONObject jsonObject = new JSONObject(tokener);

	            if (jsonObject.has(nodeId)) {
	                JSONObject nodeDetails = jsonObject.getJSONArray(nodeId).getJSONObject(0);

	                JTextArea textArea = new JTextArea(nodeDetails.toString(4));
	                textArea.setEditable(false);
	                textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
	                JScrollPane scrollPane = new JScrollPane(textArea);

	                JPanel detailPanel = new JPanel(new BorderLayout());
	                detailPanel.add(scrollPane, BorderLayout.CENTER);

	                int tabIndex = tabbedPane.getTabCount();
	                tabbedPane.addTab("Details: " + nodeId, detailPanel);
	                tabHistory.push(tabbedPane.getSelectedIndex());
	                tabbedPane.setSelectedIndex(tabIndex);
	            } else {
	                JOptionPane.showMessageDialog(null, "No details found for NodeID: " + nodeId);
	            }
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(null, "Error reading JSON file: " + ex.getMessage());
	        }
	    }

	    private void closeCurrentTab() {
	        int currentIndex = tabbedPane.getSelectedIndex();

	        if (currentIndex > 0) { // Không cho phép xóa tab chính (tab "Main")
	            // Xóa tab khỏi lịch sử nếu tồn tại
	            tabHistory.removeIf(index -> index == currentIndex);

	            // Xóa tab khỏi JTabbedPane
	            tabbedPane.remove(currentIndex);

	            // Điều hướng đến tab khác (nếu còn tab nào)
	            if (currentIndex - 1 >= 0) {
	                tabbedPane.setSelectedIndex(currentIndex - 1);
	            }
	        } else {
	            JOptionPane.showMessageDialog(null, "Cannot close the main tab.");
	        }
	    }

    private void filterTable(String query, DefaultTableModel tableModel, JTable table) {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);
        if (query.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void sortTable(Object columnName, JTable table, boolean ascending) {
        if (columnName == null) {
            JOptionPane.showMessageDialog(null, "Please select a column to sort.");
            return;
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        int columnIndex = table.getColumnModel().getColumnIndex(columnName.toString());
        
        // Check if the column is "NodeID" or "Rank", and handle accordingly
        if (columnName.toString().equals("NodeID") || columnName.toString().equals("Rank")) {
            sorter.setComparator(columnIndex, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    try {
                        // Try to parse as numbers
                        double val1 = Double.parseDouble(o1.toString());
                        double val2 = Double.parseDouble(o2.toString());
                        return Double.compare(val1, val2);
                    } catch (NumberFormatException e) {
                        // If not a number, compare as strings
                        return o1.toString().compareTo(o2.toString());
                    }
                }
            });
        } else {
            // Default comparator for other columns (compare as strings)
            sorter.setComparator(columnIndex, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        }

        // Set sorting order
        sorter.setSortKeys(java.util.Collections.singletonList(
                new RowSorter.SortKey(columnIndex, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING)
        ));
        sorter.sort();
    }
}

// Custom Renderers
class GreenBackgroundRenderer extends DefaultTableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getColumnName(column).equals("NodeID")) {
            cellComponent.setBackground(new Color(204, 255, 204)); // Màu xanh lá cây nhạt
        } else {
            cellComponent.setBackground(Color.WHITE); // Màu trắng mặc định
        }
        return cellComponent;
    }
}

class BlueBackgroundRenderer extends DefaultTableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getColumnName(column).equals("Rank")) {
            cellComponent.setBackground(new Color(173, 216, 230)); // Màu xanh dương nhạt
        } else {
            cellComponent.setBackground(Color.WHITE); // Màu trắng mặc định
        }
        return cellComponent;
    }
}

class RedBackgroundRenderer extends DefaultTableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getColumnName(column).equals("Score")) {
            cellComponent.setBackground(new Color(255, 182, 193)); // Màu đỏ nhạt
        } else {
            cellComponent.setBackground(Color.WHITE); // Màu trắng mặc định
        }
        return cellComponent;
    }
}
