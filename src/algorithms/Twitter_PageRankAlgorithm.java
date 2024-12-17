package algorithms;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;

import graph.GraphManager;
import manager.DataManagerInterface;
import manager.TwitterDataManager;
public class Twitter_PageRankAlgorithm {
    private GraphManager graphManager;
    private double[] outEdgeWeights; // Mảng tĩnh lưu tổng trọng số outEdges cho các nodes
    private Graph graph ;
    private GraphModel graphModel ; // Lấy GraphModel
    private int numZerosCollumns;
    private boolean[] deadend;
    private DataManagerInterface manager;

    public Twitter_PageRankAlgorithm(GraphManager graphManager) {
        this.graphManager = graphManager;
        this.graph = graphManager.getGraph();
        this.graphModel = graphManager.getGraphModel();
        int nodeCount = graph.getNodeCount(); // Lấy số lượng node
        this.outEdgeWeights = new double[nodeCount]; // Khởi tạo mảng với kích thước phù hợp
        this.numZerosCollumns = 0;
        this.deadend = new boolean[nodeCount];
        this.manager = new TwitterDataManager("changed_database_11.json");
    }

    // Thêm trọng số lên cạnh
    
    public void assignIndexToNodes() {

        // Thêm cột 'weight' cho node nếu chưa tồn tại
        Table nodeTable = graphModel.getNodeTable();
        if (nodeTable.getColumn("index") == null) {
            nodeTable.addColumn("index", Integer.class); // Khai báo cột 'weight' là kiểu Double
        }
        
        int index = 0;

        // Duyệt qua tất cả các nodes và gán index
        for (Node node : graph.getNodes()) {
            // Sử dụng thuộc tính "index" để lưu
            node.setAttribute("index", index);
            index++;
        }
        // Khởi tạo mảng outEdgeWeights theo số lượng nodes
    }
    public void addWeightToAllEdges() {
        
        // Duyệt qua tất cả các cạnh trong đồ thị và thêm trọng số
        for (Edge edge : graph.getEdges()) {
        	String label = edge.getLabel();
        	if(label.equals("retweet")) {
        		edge.setWeight(1.0);
        	}
        	else if(label.equals("follow")) {
        		edge.setWeight(0.8);
        	}
        	else if(label.equals("comment")) {
        		edge.setWeight(0.6);
        	}
        	else if(label.equals("post")) {
        		edge.setWeight(0.4);
        	}
        }
    }
    public void computeOutEdgeWeights() {
        // Duyệt qua từng node để tính tổng trọng số outEdges
        for (Node node : graph.getNodes()) {
        	int index = (int) node.getAttribute("index");
        	System.out.println("Đang tính toán cho node " + index);
            String nodeLabel = node.getLabel();
            double totalWeight = 0;
            for (Edge edge : graph.getEdges(node)) {
            	Node source = edge.getSource();
            	String sourceLabel = source.getLabel();
            	if(sourceLabel.equals(nodeLabel) == true) {
            		totalWeight += edge.getWeight();
            	}
            }

            // Lưu tổng trọng số vào Map outEdgeWeights
            outEdgeWeights[index] = totalWeight;
            if(totalWeight == 0) {
            	this.numZerosCollumns++;
            	this.deadend[index] = true;
            }
            else {
            	this.deadend[index] = false;
            }
        }
    }
    
    public void addWeighttoAllNodes() {
    	Graph graph = graphManager.getGraph();
        // Thêm cột 'weight' cho node nếu chưa tồn tại
    	GraphModel graphModel = graphManager.getGraphModel(); // Lấy GraphModel

        // Thêm cột 'weight' cho node nếu chưa tồn tại
        Table nodeTable = graphModel.getNodeTable();
        if (nodeTable.getColumn("weight") == null) {
            nodeTable.addColumn("weight", Double.class); // Khai báo cột 'weight' là kiểu Double
        }
        
    	int nodeCount = graph.getNodeCount();
    	double weight = 1.0 / nodeCount;
    	for (Node node : graph.getNodes()) {
    		node.setAttribute("weight", weight);
        }
    }
    public void run(double dampingFactor, int maxIterations) {
        // Bước khởi tạo
    	System.out.println("Đang assign");
        this.assignIndexToNodes();
        System.out.println("Đang add Weight lên cạnh");
        this.addWeightToAllEdges();
        System.out.println("Đang add weight lên đỉnh");
        this.addWeighttoAllNodes();
        //this.updateEdgeWeights();
        System.out.println("Đang tính toán out edges");
        this.computeOutEdgeWeights();

        System.out.println("number of dead ends = " + this.numZerosCollumns);
        Graph graph = graphManager.getGraph();
        GraphModel graphModel = graphManager.getGraphModel(); // Lấy GraphModel

        // Khởi tạo PageRank
        double currentLoss = 0;
        double previousLoss = 0;
        double[] pageRank = new double[graph.getNodeCount()];
        for (Node node : graph.getNodes()) {
            double weight = (double) node.getAttribute("weight");
            int index = (int) node.getAttribute("index");
            pageRank[index] = weight;
            if(this.deadend[index]==true) {
            	//System.out.println("Node là dead end : " + node.getId());
            	currentLoss += weight;
            }
        }

        // Debug: In nội dung ban đầu của pageRank
        System.out.println("Initial PageRank:");
        for (Node node : graph.getNodes()) {
            int index = (int) node.getAttribute("index");
            System.out.println("Node ID: " + node.getId() + ", Score: " + pageRank[index]);
        }

        // Thực hiện các vòng lặp PageRank
        for (int i = 0; i < maxIterations; i++) {
            System.out.println("Iteration " + (i + 1) + "-----------------");
            double[] newPageRank = new double[graph.getNodeCount()];
            previousLoss = currentLoss;
        	//System.out.println("previousLoss = "+ previousLoss);
        	currentLoss = 0;

            // Tính toán PageRank mới cho mỗi node
            for (Node node1 : graph.getNodes()) {
                int index1 = (int) node1.getAttribute("index");
                String node1Label = node1.getLabel();
                System.out.println("Đang xử lý node "+ index1);
                double newValue = 0.0;
                for (Edge edge : graph.getEdges(node1)) {
                     // Chỉ duyệt qua các cạnh kết nối
                    Node source = edge.getSource();
                    String sourceLabel = source.getLabel();
                    if(sourceLabel.equals(node1Label) == false) {
                    	//System.out.println("Đang xử lý neighbor " + source.getId());
                        int sourceIndex = (int) source.getAttribute("index");
                        double currentRank = pageRank[sourceIndex];
                        double matrixValue = edge.getWeight() / outEdgeWeights[sourceIndex]; // Tính trực tiếp
                        newValue += currentRank * matrixValue;
                    }
                    
                }
                newPageRank[index1] = newValue + 1.0/graph.getNodeCount() * previousLoss;
                if(this.deadend[index1] == true) {
                	//System.out.println("Cộng loss vào cho node " + node1.getId());
                	currentLoss += newPageRank[index1];
                }
            }

            // Gán newPageRank thành pageRank
            pageRank = newPageRank;

            // Debug: In nội dung pageRank sau mỗi vòng lặp
            System.out.println("PageRank after iteration " + (i + 1) + ":");
            for (Node node : graph.getNodes()) {
                int index = (int) node.getAttribute("index");
                System.out.println("Node ID: " + node.getId() + ", Score: " + pageRank[index]);
            }
        }

        // Gán điểm PageRank vào node và tính rank
        Table nodeTable = graphModel.getNodeTable();
        if (nodeTable.getColumn("score") == null) {
            nodeTable.addColumn("score", Double.class); // Khai báo cột 'score' là kiểu Double
        }
        if (nodeTable.getColumn("rank") == null) {
            nodeTable.addColumn("rank", Integer.class); // Khai báo cột 'rank' là kiểu Integer
        }

        // Lấy các điểm số (scores) từ các node
        manager.loadFromDatabase();
        List<NodeScore> nodeScores = new ArrayList<>();
        for (Node node : graph.getNodes()) {
            String nodeId = (String) node.getId();
            if (manager.hasUser(nodeId)) {  // Kiểm tra nếu node tồn tại trong database
                double score = pageRank[(int) node.getAttribute("index")]; // Lấy score từ mảng pageRank
                node.setAttribute("score", score); // Gán score vào node
                nodeScores.add(new NodeScore(node, score));
            }
        }

        // Sắp xếp các node theo điểm số giảm dần
        nodeScores.sort((n1, n2) -> Double.compare(n2.score, n1.score));

        // Gán rank cho mỗi node
        for (int rank = 0; rank < nodeScores.size(); rank++) {
            Node node = nodeScores.get(rank).node;
            node.setAttribute("rank", rank + 1); // Rank bắt đầu từ 1
        }

        // In ra rank và score của các node
        for (Node node : graph.getNodes()) {
            if (manager.hasUser(node.getId().toString())) {  // Kiểm tra nếu node có trong database
                System.out.println("Node ID: " + node.getId() + ", Rank: " + node.getAttribute("rank") +
                        ", Score: " + node.getAttribute("score"));
            }
        }

        // Ghi kết quả ra file
        writeResultsToFile(nodeScores, "PageRankResults.txt");
    }
    
    private void writeResultsToFile(List<NodeScore> nodeScores, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Ghi tiêu đề với định dạng cột
            String header = String.format("%-70s %-10s %-20s%n", "NodeID", "Rank", "Score");
            writer.write(header);

            // Ghi dữ liệu từng dòng
            for (NodeScore nodeScore : nodeScores) {
                Node node = nodeScore.node;
                int rank = (int) node.getAttribute("rank");
                double score = (double) node.getAttribute("score");
                
                // Định dạng mỗi dòng với độ rộng cố định
                String row = String.format("%-70s %-10d %-20.12f%n", node.getId(), rank, score);
                writer.write(row);
            }

            System.out.println("Kết quả đã được ghi vào file: " + fileName);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }


    // Lớp phụ trợ để lưu trữ node và điểm số của nó
    private static class NodeScore {
        Node node;
        double score;

        NodeScore(Node node, double score) {
            this.node = node;
            this.score = score;
        }
    }

}
