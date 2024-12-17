package graph;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.gephi.graph.api.*;
import org.gephi.project.api.*;
import org.gephi.io.exporter.api.*;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.openide.util.Lookup;
import entities.User;
import entities.Tweet;

public class GraphManager {
	private GraphModel graphModel;
    private Graph graph;
    public GraphManager() {
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        graph = graphModel.getDirectedGraph();
    }
    public void addNode(entities.Node node) {
        addNode(node.getId());
    }

    public void addNode(String id) {
    	org.gephi.graph.api.Node graphNode = graph.getNode(id);
        if (graphNode != null) {
        	System.out.println("Node với id " + id + " đã tồn tại");
            return;  // Nếu node đã tồn tại, không thêm lại node này
        }

        // Tạo một node mới từ graphModel
        graphNode = graphModel.factory().newNode(id);
        graphNode.setLabel(id); // Gắn nhãn là ID của User

        // Kiểm tra nếu ID bắt đầu bằng "user_" thì set màu đỏ
        if (id.startsWith("user_")) {
            graphNode.setColor(Color.RED);
            graph.addNode(graphNode);
        }
        else if(id.startsWith("tweet_")) {
            graphNode.setColor(Color.BLUE);
            graph.addNode(graphNode);
        }
        else {
            return;
        }
    }
    public void addEdge(entities.Node source, entities.Node sink) {
        // Đảm bảo bạn đang giữ khóa ghi khi thêm node hoặc cạnh
        graph.writeLock(); // Lấy khóa ghi

        try {
            org.gephi.graph.api.Node sourceNode = graph.getNode(source.getId());
            org.gephi.graph.api.Node sinkNode = graph.getNode(sink.getId());

            // Nếu sourceNode chưa có trong đồ thị, thêm nó vào
            if (sourceNode == null) {
                sourceNode = graphModel.factory().newNode(source.getId());
                sourceNode.setLabel(source.getId());
                if (source.getId().startsWith("user_")) {
                    sourceNode.setColor(Color.RED);
                } else if (source.getId().startsWith("tweet_")) {
                    sourceNode.setColor(Color.BLUE);
                }
                graph.addNode(sourceNode);
            }

            // Nếu sinkNode chưa có trong đồ thị, thêm nó vào
            if (sinkNode == null) {
                sinkNode = graphModel.factory().newNode(sink.getId());
                sinkNode.setLabel(sink.getId());
                if (sink.getId().startsWith("user_")) {
                    sinkNode.setColor(Color.RED);
                } else if (sink.getId().startsWith("tweet_")) {
                    sinkNode.setColor(Color.BLUE);
                }
                graph.addNode(sinkNode);
            }

            // Thêm cạnh giữa các node
            Edge edge = graphModel.factory().newEdge(sourceNode, sinkNode, true);
            if (User.class.isInstance(source) && User.class.isInstance(sink)) {
                edge.setLabel("follow");
            } else if (User.class.isInstance(source) && Tweet.class.isInstance(sink)) {
                Tweet tweetSink = (Tweet) sink;
                User userSource = (User) source;
                if (tweetSink.getCommentedBy().contains(userSource.getId())) {
                	 edge.setLabel("comment");
                } else{
                	edge.setLabel("retweet");
                } 
            } else if (Tweet.class.isInstance(source) && User.class.isInstance(sink)) {
            	Tweet tweetSource = (Tweet) source;
                User userSink = (User) sink;
                if (tweetSource.getAuthor_id().equals(userSink.getId())) {
                    edge.setLabel("posted");
                }
            }

            // Kiểm tra nếu đã tồn tại cạnh cùng hướng và cùng label từ sourceNode đến sinkNode
            // Kiểm tra nếu đã tồn tại cạnh cùng hướng và cùng label từ sourceNode đến sinkNode
            List<Edge> existingEdgesSource = new ArrayList<>();
            for (Edge e : graph.getEdges(sourceNode)) {
                existingEdgesSource.add(e);
            }
            
            // Chuyển đổi list thành mảng
            Edge[] existingEdgeArraySource = existingEdgesSource.toArray(new Edge[0]);

            for (Edge existingEdge : existingEdgeArraySource) {
                if (existingEdge.getTarget().equals(sinkNode) && edge.getLabel().equals(existingEdge.getLabel())) {
                    System.out.println("Cạnh giữa " + source.getId() + " và " + sink.getId() + " với label '" + edge.getLabel() + "' đã tồn tại.");
                    return; // Không thêm cạnh mới nếu đã tồn tại cạnh trùng lặp
                }
            }

            // Thêm cạnh nếu không có cạnh trùng lặp
            graph.addEdge(edge);
            if(edge.getLabel().equals("retweet")) {
            	// System.out.println("Cạnh giữa " + source.getId() + " và " + sink.getId() + " với label '" + edge.getLabel() + "' đã được thêm.");
            }
           
        } finally {
            graph.writeUnlock(); // Đảm bảo giải phóng khóa ghi khi hoàn tất
        }
    }

    
    // Phương thức lấy danh sách node đầu vào của một node
 // Phương thức lấy danh sách node đầu vào của một node
 // Phương thức lấy danh sách node đầu vào của một node
    public List<org.gephi.graph.api.Node> getInNodes(String nodeId) {
        List<org.gephi.graph.api.Node> inNodes = new ArrayList<>();
        org.gephi.graph.api.Node targetNode = graph.getNode(nodeId);

        if (targetNode != null) {
            // Kiểm tra tất cả các cạnh trong đồ thị
            for (Edge edge : graph.getEdges()) {
                // Nếu cạnh có đích là node cần tìm, thêm node nguồn vào danh sách
                if (edge.getTarget().equals(targetNode)) {
                    inNodes.add(edge.getSource());
                }
            }
        } else {
            System.out.println("Node với ID " + nodeId + " không tồn tại.");
        }

        return inNodes;
    }

    // Phương thức lấy danh sách node đầu ra của một node
    public List<org.gephi.graph.api.Node> getOutNodes(String nodeId) {
        List<org.gephi.graph.api.Node> outNodes = new ArrayList<>();
        org.gephi.graph.api.Node sourceNode = graph.getNode(nodeId);

        if (sourceNode != null) {
            // Kiểm tra tất cả các cạnh trong đồ thị
            for (Edge edge : graph.getEdges()) {
                // Nếu cạnh có nguồn là node cần tìm, thêm node đích vào danh sách
                if (edge.getSource().equals(sourceNode)) {
                    outNodes.add(edge.getTarget());
                }
            }
        } else {
            System.out.println("Node với ID " + nodeId + " không tồn tại.");
        }

        return outNodes;
    }
    
 // Phương thức lấy danh sách các đỉnh trong đồ thị
    public List<org.gephi.graph.api.Node> getNodes() {
        List<org.gephi.graph.api.Node> nodes = new ArrayList<>();
        for (org.gephi.graph.api.Node node : graph.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }

    // Phương thức lấy danh sách các cạnh trong đồ thị
    public List<org.gephi.graph.api.Edge> getEdges() {
        List<org.gephi.graph.api.Edge> edges = new ArrayList<>();
        for (org.gephi.graph.api.Edge edge : graph.getEdges()) {
            edges.add(edge);
        }
        return edges;
    }

    public List<org.gephi.graph.api.Edge> getInEdges(String nodeId) {
        List<org.gephi.graph.api.Edge> inEdges = new ArrayList<>();
        org.gephi.graph.api.Node targetNode = graph.getNode(nodeId);

        if (targetNode != null) {
            // Duyệt qua tất cả các cạnh của node và kiểm tra nếu cạnh có đích là node hiện tại
            for (Edge edge : graph.getEdges()) {
                if (edge.getTarget().equals(targetNode)) {
                    inEdges.add(edge);
                }
            }
        } else {
            System.out.println("Node với ID " + nodeId + " không tồn tại.");
        }

        return inEdges;
    }

    // Phương thức lấy danh sách các cạnh đầu ra (out-edges) của một node
    public List<org.gephi.graph.api.Edge> getOutEdges(String nodeId) {
        List<org.gephi.graph.api.Edge> outEdges = new ArrayList<>();
        org.gephi.graph.api.Node sourceNode = graph.getNode(nodeId);

        if (sourceNode != null) {
            // Duyệt qua tất cả các cạnh của node và kiểm tra nếu cạnh có nguồn là node hiện tại
            for (Edge edge : graph.getEdges()) {
                if (edge.getSource().equals(sourceNode)) {
                    outEdges.add(edge);
                }
            }
        } else {
            System.out.println("Node với ID " + nodeId + " không tồn tại.");
        }

        return outEdges;
    }
    public void loadGraphFromFile(String filepath) {
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        try {
            // Import file GEXF và lấy ContainerLoader
        	org.gephi.io.importer.api.Container containerLoader = importController.importFile(new java.io.File(filepath));

            // Lấy Workspace hiện tại
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            Workspace workspace = pc.getCurrentWorkspace();

            // Sử dụng ContainerLoader để thêm dữ liệu vào Workspace
            importController.process(containerLoader, new DefaultProcessor(), workspace);

            // Cập nhật GraphModel và Graph từ Workspace hiện tại
            graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
            graph = graphModel.getDirectedGraph();

            System.out.println("Đã tải đồ thị từ file " + filepath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải đồ thị từ file: " + filepath);
        }
    }
    
    public void saveGraphtoFile(String filepath) {
    	ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            Exporter exporter = ec.getExporter("gexf");
            ec.exportFile(new java.io.File(filepath), exporter);
            System.out.println("Đã xuất đồ thị ra file "+ filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }      
    }
    public Graph getGraph() {
        if (graph == null) {
            System.out.println("Lỗi: Đồ thị không được khởi tạo.");
        }
        return graph;
    }
    
    public static void main(String[] args) {
    	GraphManager graphManager = new GraphManager();

    	// Tải đồ thị từ file
    	 graphManager.loadGraphFromFile("graph_changed_database_15.gexf");

    	    if (graphManager.getGraph() == null) {
    	        System.out.println("Lỗi: Đồ thị chưa được tải thành công.");
    	        return;
    	    }
    	    System.out.println("Danh sách các node trong đồ thị:");
    	    Graph graph = graphManager.getGraph();
    	    for (org.gephi.graph.api.Node node : graph.getNodes()) {
    	        System.out.println(node.getId());
    	    }

    	// Lấy danh sách các node đầu vào/ra của node cụ thể
    	List<org.gephi.graph.api.Node> inNodes = graphManager.getOutNodes("user_Web300fa");
    	List<org.gephi.graph.api.Node> outNodes = graphManager.getInNodes("tweet_ChainLinkGod_1864346017451126942");

    	// In kết quả
    	System.out.println("Danh sách node đầu vào:");
    	for (org.gephi.graph.api.Node node : inNodes) {
    	    System.out.println(node.getId());
    	}

    	System.out.println("Danh sách node đầu ra:");
    	for (org.gephi.graph.api.Node node : outNodes) {
    	    System.out.println(node.getId());
    	}
    	
    	List<org.gephi.graph.api.Edge> inEdges = graphManager.getOutEdges("user_OnChainPK");
    	List<org.gephi.graph.api.Edge> outEdges = graphManager.getInEdges("tweet_ChainLinkGod_1864346017451126942");

    	System.out.println("Danh sách các cạnh đầu vào:");
    	for (org.gephi.graph.api.Edge edge : inEdges) {
    	    System.out.println(edge.getId() + edge.getLabel());
    	}

    	System.out.println("Danh sách các cạnh đầu ra:");
    	for (org.gephi.graph.api.Edge edge : outEdges) {
    	    System.out.println(edge.getId() + edge.getLabel());
    	}
    }
	public GraphModel getGraphModel() {
		// TODO Auto-generated method stub
		return graphModel;
	}
}
