package graph;

import entities.Tweet;
import entities.User;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo GraphModel với file đồ thị GraphML
        GraphModel graph = new GraphModel("TwitterGraph.graphml");

        // Tạo các đối tượng User và Tweet
        User user1 = new User("UserName1", "user1@example.com");
        Tweet tweet1 = new Tweet("Tweet0007", user1);
        System.out.println(user1.getId() + "   "+ tweet1.getId() );

        // Thêm các đối tượng User và Tweet vào đồ thị
        graph.addVertex(user1);
        graph.addVertex(tweet1);

        // Thêm cạnh giữa User và Tweet (đây là đối tượng Node, không phải ID)
        graph.addEdge(user1, tweet1); // Truyền các đối tượng User và Tweet

        // Lưu đồ thị vào file GraphML
        graph.saveGraphToFile();

        // Hiển thị đồ thị
        graph.display();
        
        System.out.println(graph.userCount() + "   "+ graph.tweetCount() );
    }
}
