import java.util.ArrayList;
import java.util.List;

public class BPlusTree {
    private Node root;
    private final int maxDegree;
    private final int minKeys;

    // отслеживание пути при операциях
    private List<String> executionPath;

    public BPlusTree(int maxDegree) {
        this.maxDegree = maxDegree;
        this.minKeys = (int) Math.ceil(maxDegree / 2.0) - 1;
        this.root = new Node(true);
        this.executionPath = new ArrayList<>();
    }

    public void insert(int key) {
        executionPath.clear();
        executionPath.add("START_INSERT");

        Node root = this.root;
        if (root.keys.size() == maxDegree - 1) {
            executionPath.add("ROOT_SPLIT_NEEDED");
            Node newRoot = new Node(false);
            newRoot.children.add(root);
            this.root = newRoot;
            splitChild(newRoot, 0);
            insertNonFull(newRoot, key);
        } else {
            executionPath.add("ROOT_NOT_FULL");
            insertNonFull(root, key);
        }

        executionPath.add("INSERT_COMPLETE");
    }

    private void insertNonFull(Node node, int key) {
        executionPath.add("INSERT_NON_FULL");
        int i = node.keys.size() - 1;

        if (node.isLeaf) {
            executionPath.add("LEAF_INSERT");
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            node.keys.add(i + 1, key);
        } else {
            executionPath.add("INTERNAL_NODE_TRAVERSE");
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
            }
            i++;

            Node child = node.children.get(i);
            if (child.keys.size() == maxDegree - 1) {
                executionPath.add("CHILD_SPLIT");
                splitChild(node, i);
                if (key > node.keys.get(i)) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    private void splitChild(Node parent, int childIndex) {
        executionPath.add("SPLIT_CHILD");
        Node child = parent.children.get(childIndex);
        Node newChild = new Node(child.isLeaf);

        int midIndex = (maxDegree - 1) / 2;
        int midKey = child.keys.get(midIndex);

        // перемещение ключей в новый узел
        for (int i = midIndex + 1; i < child.keys.size(); i++) {
            newChild.keys.add(child.keys.get(i));
        }

        // удаление перемещенных ключей
        child.keys.subList(midIndex, child.keys.size()).clear();

        if (!child.isLeaf) {
            for (int i = midIndex + 1; i < child.children.size(); i++) {
                newChild.children.add(child.children.get(i));
            }
            child.children.subList(midIndex + 1, child.children.size()).clear();
        } else {
            // связывание листьев
            newChild.next = child.next;
            child.next = newChild;
        }

        parent.keys.add(childIndex, midKey);
        parent.children.add(childIndex + 1, newChild);
    }

    public boolean search(int key) {
        executionPath.clear();
        executionPath.add("START_SEARCH");
        return search(root, key);
    }

    private boolean search(Node node, int key) {
        executionPath.add("SEARCH_NODE");
        int i = 0;
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }

        if (i < node.keys.size() && key == node.keys.get(i)) {
            executionPath.add("KEY_FOUND");
            return true;
        }

        if (node.isLeaf) {
            executionPath.add("LEAF_REACHED_KEY_NOT_FOUND");
            return false;
        }

        executionPath.add("GO_TO_CHILD");
        return search(node.children.get(i), key);
    }

    public void delete(int key) {
        executionPath.clear();
        executionPath.add("START_DELETE");
        delete(root, key);
        executionPath.add("DELETE_COMPLETE");
    }

    private void delete(Node node, int key) {
        // упрощенная реализация удаления (без перебалансировки)
        executionPath.add("DELETE_NODE");
        if (node.isLeaf) {
            executionPath.add("DELETE_FROM_LEAF");
            node.keys.remove(Integer.valueOf(key));
        } else {
            executionPath.add("DELETE_FROM_INTERNAL");
        }
    }

    public List<String> getExecutionPath() {
        return new ArrayList<>(executionPath);
    }

    public void print() {
        printTree(root, 0);
    }

    private void printTree(Node node, int level) {
        System.out.println("Level " + level + ": " + node.keys);
        if (!node.isLeaf) {
            for (Node child : node.children) {
                printTree(child, level + 1);
            }
        }
    }

    public void clear() {
        root = new Node(true);
        executionPath.clear();
    }
}