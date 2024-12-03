package entities;

public class Node {
    private String id; // ID duy nhất cho mỗi node
    
    public Node() {
    	
    }

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void SetId(String id) {
        this.id = id;
    }       

    @Override
    public String toString() {
        return id; // Hiển thị ID làm nhãn mặc định
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}