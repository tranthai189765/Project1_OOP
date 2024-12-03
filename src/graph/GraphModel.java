package graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.nio.graphml.GraphMLImporter;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import entities.Node;
import entities.Tweet;
import entities.User;

public class GraphModel {

    private Graph<Node, DefaultEdge> graph;
    private String filePath;

    public GraphModel(String filePath) {
    	this.filePath = filePath;
    	this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        GraphMLImporter<Node, DefaultEdge> importer = new GraphMLImporter<>();
        
        importer.setVertexFactory(id -> {
            if (id.startsWith("user_")) {
                return new User(id,null);  // Tạo đối tượng User nếu ID bắt đầu với 'user_'
            } else if (id.startsWith("tweet_")) {
                return new Tweet(id,null); // Tạo đối tượng Tweet nếu ID bắt đầu với 'tweet_'
            } else {
                return new Node(id);  // Mặc định là Node
            }
        });

        try (FileReader reader = new FileReader(filePath)) {
            importer.importGraph(graph, reader);
            System.out.println("Đã tải đồ thị từ file: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể đọc file hoặc file không tồn tại. Tạo file mới.");
            createNewGraphFile();
        }
    }

    // Phương thức tạo file đồ thị mới
    private void createNewGraphFile() {
        try {
            File file = new File(this.filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // Tạo các thư mục cha nếu chưa tồn tại
            }
            if (file.createNewFile()) {
                System.out.println("File mới đã được tạo: " + this.filePath);
                saveGraphToFile(); // Lưu đồ thị trống vào file
            } else {
                System.out.println("Không thể tạo file mới: " + this.filePath);
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi tạo file: " + this.filePath);
            e.printStackTrace();
        }
    }
    // Phương thức thêm đỉnh mới vào đồ thị
    public boolean addVertex(Node vertex) {
        // Kiểm tra xem có đỉnh nào có ID trùng với ID của vertex không
        boolean idExists = graph.vertexSet().stream()
                .anyMatch(existingVertex -> existingVertex.getId().equals(vertex.getId()));

        if (!idExists) {
            graph.addVertex(vertex);
            System.out.println("Đã thêm đỉnh: " + vertex);
            return true;  // Thành công
        } else {
            System.out.println("Đỉnh với ID " + vertex.getId() + " đã tồn tại.");
            return false;  // Thất bại
        }
    }


    // Phương thức thêm cạnh mới vào đồ thị
    public boolean addEdge(Node source, Node target) {
        // Kiểm tra xem ID của các vertex đã tồn tại trong đồ thị chưa
        boolean sourceExists = graph.vertexSet().stream()
                .anyMatch(existingVertex -> existingVertex.getId().equals(source.getId()));
        boolean targetExists = graph.vertexSet().stream()
                .anyMatch(existingVertex -> existingVertex.getId().equals(target.getId()));

        // Nếu một trong các đỉnh chưa tồn tại, thêm vào đồ thị trước
        if (!sourceExists) {
            boolean sourceAdded = addVertex(source);  // Thêm đỉnh source vào đồ thị nếu chưa tồn tại
            if (!sourceAdded) return false;  // Nếu không thể thêm đỉnh source, trả về false
        }
        if (!targetExists) {
            boolean targetAdded = addVertex(target);  // Thêm đỉnh target vào đồ thị nếu chưa tồn tại
            if (!targetAdded) return false;  // Nếu không thể thêm đỉnh target, trả về false
        }

        // Kiểm tra lại sự tồn tại của các đỉnh sau khi thêm nếu cần
        if (graph.containsVertex(source) && graph.containsVertex(target)) {
            if (!graph.containsEdge(source, target)) {
                graph.addEdge(source, target);
                System.out.println("Đã thêm cạnh từ " + source + " đến " + target);
                return true;  // Thành công
            } else {
                System.out.println("Cạnh từ " + source + " đến " + target + " đã tồn tại.");
                return false;  // Cạnh đã tồn tại
            }
        } else {
            System.out.println("Không thể thêm cạnh. Kiểm tra xem các đỉnh với ID " + source.getId() + " và " + target.getId() + " đã tồn tại chưa.");
            return false;  // Nếu đỉnh không tồn tại
        }
    }

    // Phương thức lưu đồ thị vào file GraphML
    public void saveGraphToFile() {
        GraphMLExporter<Node, DefaultEdge> exporter = new GraphMLExporter<>();
        exporter.setVertexIdProvider(vertex -> vertex.getId()); // Sử dụng ID của Node làm ID của đỉnh

        try (FileWriter writer = new FileWriter(this.filePath)) {
            exporter.exportGraph(graph, writer);
            System.out.println("Đã lưu đồ thị vào file: " + this.filePath);
        } catch (IOException e) {
            System.out.println("Không thể lưu đồ thị vào file.");
            e.printStackTrace();
        }
    }

    public void display() {
        // Tạo mxGraph từ đồ thị JGraphT
        mxGraph mxGraph = new mxGraph();

        // Lấy đối tượng mặc định của mxGraph
        Object parent = mxGraph.getDefaultParent();

        // Bắt đầu cập nhật đồ thị
        mxGraph.getModel().beginUpdate();
        try {
            // Lưu vị trí các đỉnh
            java.util.Map<Node, Object> vertexMap = new java.util.HashMap<>();

            // Cấu hình vị trí cho các đỉnh
            int userCircleX = 600;  // Tâm x của hình tròn "User"
            int userCircleY = 500;  // Tâm y của hình tròn "User"
            int tweetCircleX = 600; // Tâm x của hình tròn "Tweet"
            int tweetCircleY = 500;  // Tâm y của hình tròn "Tweet"
            int userRadius = 400;   // Bán kính của hình tròn "User"
            int tweetRadius = 400;  // Bán kính của hình tròn "Tweet"

            // Đếm số đỉnh "User" và "Tweet"
            int userCount = (int) graph.vertexSet().stream().filter(node -> node instanceof User).count();
            int tweetCount = (int) graph.vertexSet().stream().filter(node -> node instanceof Tweet).count();

            int userIndex = 0;
            int tweetIndex = 0;

            // Sắp xếp đỉnh theo hình tròn
            for (Node node : graph.vertexSet()) {
                double angle = 0; // Góc trên hình tròn
                int x = 0, y = 0;
                int randomOffset = (int) (Math.random() * 70) - 70; // Tăng offset ngẫu nhiên

                if (node instanceof User) {
                    angle = 2 * Math.PI * userIndex / userCount; // Góc chia đều cho các đỉnh
                    x = userCircleX + (int) (userRadius * Math.cos(angle)) + randomOffset;
                    y = userCircleY + (int) (userRadius * Math.sin(angle)) + randomOffset;
                    userIndex++;
                } else if (node instanceof Tweet) {
                    angle = 2 * Math.PI * tweetIndex / tweetCount; // Góc chia đều cho các đỉnh
                    x = tweetCircleX + (int) (tweetRadius * Math.cos(angle)) + randomOffset;
                    y = tweetCircleY + (int) (tweetRadius * Math.sin(angle)) + randomOffset;
                    tweetIndex++;
                }

                // Thêm đỉnh vào đồ thị
                Object vertexCell = mxGraph.insertVertex(parent, null, node.getId(), x, y, 115, 20);
                vertexMap.put(node, vertexCell);
            }

            // Thêm các cạnh vào mxGraph
            for (DefaultEdge edge : graph.edgeSet()) {
                Node source = graph.getEdgeSource(edge);
                Node target = graph.getEdgeTarget(edge);

                Object sourceVertex = vertexMap.get(source);
                Object targetVertex = vertexMap.get(target);

                if (sourceVertex != null && targetVertex != null) {
                    mxGraph.insertEdge(parent, null, "", sourceVertex, targetVertex);
                }
            }

        } finally {
            mxGraph.getModel().endUpdate();  // Kết thúc cập nhật đồ thị
        }

        // Tạo cửa sổ đồ thị với khả năng cuộn dọc và ngang
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Tạo JPanel và thiết lập đồ thị vào nó
            JPanel panel = new JPanel(new BorderLayout());
            mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);

            // Thiết lập kích thước của mxGraphComponent
            graphComponent.setPreferredSize(new Dimension(3000, 3000));
            graphComponent.setAutoScroll(true);

            // Tạo JScrollPane để hỗ trợ cuộn dọc và ngang
            JScrollPane scrollPane = new JScrollPane(graphComponent);
            scrollPane.setPreferredSize(new Dimension(1000, 800)); // Kích thước hiển thị của JScrollPane
            panel.add(scrollPane, BorderLayout.CENTER);

            // Thêm panel vào cửa sổ
            frame.getContentPane().add(panel);

            // Thiết lập kích thước cửa sổ và hiển thị
            frame.setSize(1200, 900);
            frame.setVisible(true);

            // Đảm bảo giao diện được làm mới ngay lập tức
            panel.revalidate();
            panel.repaint();
        });
    }

    
 // Phương thức in ra tất cả các đỉnh trong đồ thị
    public void printVertices() {
        System.out.println("Danh sách các đỉnh trong đồ thị:");
        for (Node node : graph.vertexSet()) {
            System.out.println(node.getId()); // In ID của các đỉnh
        }
    }

    // Phương thức in ra tất cả các cạnh trong đồ thị
    public void printEdges() {
        System.out.println("Danh sách các cạnh trong đồ thị:");
        for (DefaultEdge edge : graph.edgeSet()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);
            System.out.println("Cạnh từ " + source.getId() + " đến " + target.getId());
        }
    }
    
    // Phương thức đếm số lượng User nodes
    public long userCount() {
        return graph.vertexSet().stream()
                .filter(vertex -> vertex instanceof User) // Lọc các đỉnh thuộc kiểu User
                .count();
    }

    // Phương thức đếm số lượng Tweet nodes
    public long tweetCount() {
        return graph.vertexSet().stream()
                .filter(vertex -> vertex instanceof Tweet) // Lọc các đỉnh thuộc kiểu Tweet
                .count();
    }
    
}
