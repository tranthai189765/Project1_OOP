package graph;

import java.io.IOException;

import org.gephi.graph.api.Graph;

import manager.DataManagerInterface;
import utils.utils;
import manager.TwitterDataManager;
public class Test {
	public static void main(String[] args) {
        
		DataManagerInterface dataManager = new TwitterDataManager("changed_database_11.json");
		try {
			utils.extractUrlsToFile("changed_database_11.json", "debug.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GraphManager graphManager = new GraphManager();
		GraphBuilder graphBuilder  = new GraphBuilder(graphManager, dataManager);
		graphBuilder.buildGraph("graph_changed_database_24.gexf");
		if(dataManager.hasUser("user_maverick23NFT")) {
			System.out.println("Có thằng này r nhé");
		}
		Graph graph = graphManager.getGraph();
		System.out.println("Số lượng nodes : " + graph.getNodeCount());
    }

}
