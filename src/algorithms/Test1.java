package algorithms;

import graph.GraphManager;

public class Test1 {
	public static void main(String[] args) {
    	GraphManager graphManager = new GraphManager();

    	// Tải đồ thị từ file
    	 graphManager.loadGraphFromFile("graph_changed_database_24.gexf");
    	 Twitter_PageRankAlgorithm alg = new Twitter_PageRankAlgorithm(graphManager);
 		 alg.run(0.85, 200);
	}
}
