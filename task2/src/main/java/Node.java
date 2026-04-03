import java.util.ArrayList;
import java.util.List;

public class Node {
    public boolean isLeaf;
    public List<Integer> keys;
    public List<Node> children;
    public Node next; // для связки листьев

    public Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }
}