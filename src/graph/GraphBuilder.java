package graph;

import manager.DataManagerInterface;
import java.util.List;
import java.util.Map;

import entities.Tweet;
import entities.User;
public class GraphBuilder {
	private DataManagerInterface dataManager;
	private GraphManager graphManager;
	public GraphBuilder(GraphManager graphManager, DataManagerInterface dataManager) {
		this.dataManager = dataManager;
		this.graphManager = graphManager;
	}
	public void buildGraph(String filepath) {
        // Tải dữ liệu từ database
        dataManager.loadFromDatabase();

        // Duyệt qua từng mục dữ liệu
        for (Map.Entry<String, List<User>> entry : dataManager.getData().entrySet()) {
            for (User user : entry.getValue()) {
            	if(!user.getTweets().isEmpty()) {
                // Thêm User vào đồ thị
                graphManager.addNode(user);
              

                 // Thêm các Tweet của User vào đồ thị
                  for (Tweet tweet : user.getTweets()) {
                    graphManager.addNode(tweet);
                    User author = new User();
                    author.SetId(tweet.getAuthor_id());
                    graphManager.addEdge(tweet, author);
                    if(user.getId().equals(author.getId()) == false) {
                    	graphManager.addEdge(user, tweet);
                    }
                    for (String commenter_id : tweet.getCommentedBy()) {
                    	User commenter = new User();
                    	commenter.SetId(commenter_id);
                    	graphManager.addEdge(commenter, tweet);
                    }
                }
            }
            }
        }
        graphManager.saveGraphtoFile(filepath);
        System.out.println("Đồ thị đã được xây dựng từ dữ liệu.");
    }
}
