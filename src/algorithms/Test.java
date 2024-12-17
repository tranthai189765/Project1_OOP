package algorithms;

import graph.GraphManager;
import entities.User;

public class Test {
    public static void main(String[] args) {
        // Khởi tạo GraphManager
        GraphManager graphManager = new GraphManager();
        
        // Khởi tạo các User
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        User user5 = new User();
        
        // Gán ID cho các user
        user1.SetId("user_1");
        user2.SetId("user_2");
        user3.SetId("user_3");
        user4.SetId("user_4");
        user5.SetId("user_5");

        // Thêm các user vào graphManager
        graphManager.addNode(user1);
        graphManager.addNode(user2);
        graphManager.addNode(user3);
        graphManager.addNode(user4);

        // Thêm các cạnh giữa các user (kiểm tra tránh thêm trùng lặp)
        graphManager.addEdge(user2, user1);
        graphManager.addEdge(user3, user1);
        graphManager.addEdge(user3, user4);
        graphManager.addNode(user1);
        graphManager.addNode(user5);
        graphManager.addEdge(user5, user1);
        

        // Kiểm tra xem graphManager có chứa graph không trước khi chạy PageRank
        if (graphManager.getGraph() == null) {
            System.out.println("Graph is not initialized properly.");
            return; // Nếu chưa khởi tạo graph, dừng lại
        }

        // Khởi tạo và chạy thuật toán PageRank
        Twitter_PageRankAlgorithm alg = new Twitter_PageRankAlgorithm(graphManager);
        alg.run(0.85, 10);
    }
}
