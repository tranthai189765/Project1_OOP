package graph;

import data.Tweet;
import data.User;

public class Test {
	public static void main(String[] args) {
        // Khởi tạo GraphModel với file đồ thị GraphML
        GraphModel graph = new GraphModel("TwitterGraph.graphml");
        graph.display();
        
    }

}
