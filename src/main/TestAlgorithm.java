package main;

import algorithms.Twitter_PageRankAlgorithm;
import config.ConfigInterface;
import config.TwitterConfig;
import graph.GraphBuilder;
import graph.GraphManager;

public class TestAlgorithm {
	public static void main(String[] args) {
		ConfigInterface config = new TwitterConfig();
		GraphManager graphManager = new GraphManager();
		GraphBuilder graphBuilder  = new GraphBuilder(graphManager, config);
		graphBuilder.buildGraph(config);
		graphManager.loadGraphFromFile(config.getGraphFilePath());
   	    Twitter_PageRankAlgorithm alg = new Twitter_PageRankAlgorithm(graphManager, config);
		alg.run(0.85, 200);
	}
	
}
