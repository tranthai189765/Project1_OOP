package algorithms;

import graph.GraphManager;
import config.ConfigInterface;
import config.TwitterConfig;
public class Test1 {
	public static void main(String[] args) {
    	GraphManager graphManager = new GraphManager();

    	// Tải đồ thị từ file
    	 ConfigInterface config = new TwitterConfig();
    	 graphManager.loadGraphFromFile("graph_changed_database_24.gexf");
    	 Twitter_PageRankAlgorithm alg = new Twitter_PageRankAlgorithm(graphManager, config);
 		 alg.run(0.85, 200);
	}
}
