import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.management.RuntimeErrorException;

/**
 * Reference code: https://www.geeksforgeeks.org/serialize-deserialize-binary-tree/
 * 
 * For code that handles cycles, refer the serializeWithCycleRemoval method
 * 
 * To support any data type, Node can store 'Object' instead of int
 * @author swara
 *
 */
public class Serializer implements TreeSerializer {

	Node root;
	static int t;
	static Set<Node> traversedNodes = new HashSet<>();
	
	//This is basically bfs
	@Override
	public String serialize(Node root) {
		// TODO Auto-generated method stub
		
		 if (root == null) {
	            return null;
	        }
        Stack<Node> s = new Stack<>();
        s.push(root);
        traversedNodes.add(root);
        
 
        List<String> l = new ArrayList<>();
        
        while (!s.isEmpty()) {
            Node t = s.pop();
           
 
            // If current node is NULL, store marker
            if (t == null) {
                l.add("#");
            }
            else {
 
                // Else, store current node
                // and recur for its children
                l.add("" + t.num);
                
                if (t.right != null && traversedNodes.contains(t.right)) {
                	
                	System.out.println(t.right.num);
                	throw new RuntimeErrorException(null, "Cycle encountered");
                }
                
                s.push(t.right);
                
                if (t.left != null && traversedNodes.contains(t.left)) {
                	System.out.println(t.left.num);
                	throw new RuntimeErrorException(null, "Cycle encountered");
                }
                
                s.push(t.left);
                
                if (t.left != null)
                	traversedNodes.add(t.left);
                
                if (t.right != null)
                	traversedNodes.add(t.right);
                
            }
        }
        return String.join(",", l);

	}
	
	//This is basically bfs
	public String serializeWithCycleRemoval(Node root) {
			// TODO Auto-generated method stub
			
			 if (root == null) {
		            return null;
		        }
	        Stack<Node> s = new Stack<>();
	        s.push(root);
	        traversedNodes.add(root);
	        
	 
	        List<String> l = new ArrayList<>();
	        
	        while (!s.isEmpty()) {
	            Node t = s.pop();
	           
	 
	            // If current node is NULL, store marker
	            if (t == null) {
	                l.add("#");
	            }
	            else {
	 
	                // Else, store current node
	                // and recur for its children
	                l.add("" + t.num);
	                
	                if (t.right != null && traversedNodes.contains(t.right)) {
	                	t.right = null;
//	                	System.out.println(t.right.num);
//	                	throw new RuntimeErrorException(null, "Cycle encountered");
	                }
	                
	                s.push(t.right);
	                
	                if (t.left != null && traversedNodes.contains(t.left)) {
	                	t.left = null;
//	                	System.out.println(t.left.num);
//	                	throw new RuntimeErrorException(null, "Cycle encountered");
	                }
	                
	                s.push(t.left);
	                
	                if (t.left != null)
	                	traversedNodes.add(t.left);
	                
	                if (t.right != null)
	                	traversedNodes.add(t.right);
	                
	            }
	        }
	        return String.join(",", l);

		}
		

	@Override
	public Node deserialize(String str) {
		// TODO Auto-generated method stub
		
		if (str == null)
            return null;
        t = 0;
        String[] arr = str.split(",");
        return generateTree(arr);
       
	}
	
	public static Node generateTree(String[] arr)
    {
        if (arr[t].equals("#"))
            return null;
 
        // Create node with this item
        // and recur for children
        Node root = new Node(Integer.parseInt(arr[t]));
        t++;
        root.left = generateTree(arr);
        t++;
        root.right = generateTree(arr);
        return root;
    }
	
	// A simple inorder traversal used
    // for testing the constructed tree
    static void inorder(Node root)
    {
        if (root != null) {
            inorder(root.left);
            System.out.print(root.num + " ");
            inorder(root.right);
        }
    }
    
    public static void main(String args[])
    {
        // Construct a tree shown in the above figure
        Serializer tree = new Serializer();
        tree.root = new Node(1);
        tree.root.left = new Node(2);
        
        Node n = new Node(1);
        tree.root.right = n;
        tree.root.left.left = new Node(7);
        tree.root.left.left.left = new Node(4);
        tree.root.left.right = new Node(5);
        
        //introduce cycle 
        tree.root.left.right.right = n;
        tree.root.right.right = new Node(28);
 
        String serialized = tree.serializeWithCycleRemoval(tree.root);
        System.out.println("Serialized view of the tree:");
        System.out.println(serialized);
        System.out.println();
 
        // Deserialize the stored tree into root1
        Node t = tree.deserialize(serialized);
 
        System.out.println(
            "Inorder Traversal of the tree constructed"
            + " from serialized String:");
        inorder(t);
    }
 

}
