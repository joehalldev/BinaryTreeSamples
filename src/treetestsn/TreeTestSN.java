package treetestsn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * Binary Tree Samples
 * @author Joe Hall
 */
public class TreeTestSN {

	public static void main(String[] args) throws IOException {
		long t1 = System.currentTimeMillis();
		BTreeNode tree = null;
		int recordCount = 0;
		try(FileReader fr = new FileReader("main.txt");
			BufferedReader br = new BufferedReader(fr);) {
			String line = br.readLine();
			while(line != null) {
				recordCount++;
				if(recordCount % 500_000 == 0)
					System.out.println("Read records: " + recordCount + ", " + (System.currentTimeMillis() - t1) + "ms");
				if(recordCount % 2000000 == 0)
					break;
				SNRec nodeData = new SNRec(line);
				if(tree == null)	
					tree = new BTreeNode(nodeData);
				else 
					tree.addNode(nodeData);
				line = br.readLine();
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Finished Loading Tree in (" + (t2 - t1) + ")ms from " + recordCount + " records.");
		long t3 = System.currentTimeMillis();
		SNRec rec = tree.findSNData("ef7cb77d-4407-46e6-a023-3b79382fafd2"); // search serial
		long t4 = System.currentTimeMillis();
		System.out.println("SNRec: " + rec.sn + ", Lookup: " + (t4 - t3) + "ms");
		//traverseTree(tree, 1);
		//travTreeNoRecur(tree, new PrintVisitor());
		// Count items in tree
		CountVisitor visitor = new CountVisitor();
		travTreeNoRecur(tree, visitor);
		System.out.println("Total nodes: " + visitor.counter);
		visitor = new CountVisitor();
		travTreeBFirst(tree, visitor);
		visitor = new CountVisitor();
		travTreeDFirst(tree, visitor);
		System.out.println("Total nodes: " + visitor.counter);
	}
	
	static void travTreeBFirst(BTreeNode node, TreeVisitor visitor) {
		Queue<BTreeNode> q = new ArrayDeque<>();
		q.add(node);
		while(!q.isEmpty()) {
			node = q.poll();
			if(node.left != null)
				q.add(node.left);
			if(node.right != null)
				q.add(node.right);
			visitor.visit(node);
		}
	}
	
	static void travTreeDFirst(BTreeNode node, TreeVisitor visitor) {
		Stack<BTreeNode> q = new Stack<>();
		q.push(node);
		while(!q.isEmpty()) {
			node = q.pop();
			if(node.left != null)
				q.push(node.left);
			if(node.right != null)
				q.push(node.right);
			visitor.visit(node);
		}
	}
	
	static void travTreeNoRecur(BTreeNode root, TreeVisitor visitor) {
		BTreeNode current;
		Stack<BTreeNode> stack = new Stack<>();
		current = root;
		while(current != null) {
			stack.push(current);
			current = current.left;
		}
		while(!stack.isEmpty()) {
			current = stack.pop();
			visitor.visit(current);
			if(current.right != null) {
				current = current.right;
				while(current != null) {
					stack.push(current);
					current = current.left;
				}
			}
		}
	}
	
	private static interface TreeVisitor {
		public void visit(BTreeNode node);
	}
	
	private static class PrintVisitor implements TreeVisitor {
		public void visit(BTreeNode node) {
			System.out.println(node.data.sn);
		}
	}
	private static class CountVisitor implements TreeVisitor {
		int counter;
		public void visit(BTreeNode node) {
			this.counter++;
		}
	}
	
	// Depth First
	static void traverseTreeRecursion(BTreeNode node, int level) {
		System.out.println(level + ") " + node.data.sn);
		level++;
		if(node.left != null)
			traverseTreeRecursion(node.left, level);
		if(node.right != null)
			traverseTreeRecursion(node.right, level);
	}
	
	private static class BTreeNode {
		private final SNRec data;
		private BTreeNode left;
		private BTreeNode right;
		
		public BTreeNode(SNRec data) {
			this.data = data;
		}
		
		public void addNode(SNRec rec) {
			final int diff = rec.sn.compareTo(data.sn);
			if(diff < 0) {
				if(this.left != null)
					this.left.addNode(rec);
				else
					this.left = new BTreeNode(rec);
			} else if (diff > 0) {
				if(this.right != null)
					this.right.addNode(rec);
				else
					this.right = new BTreeNode(rec);
			}
			// else already in list ignore
		}
		
		public SNRec findSNData(String sn) {
			SNRec retVal = null;
			final int diff = sn.compareTo(data.sn);
			if(diff == 0) {
				retVal = this.data;
			} else if(diff < 0) {
				if(this.left != null)
					return this.left.findSNData(sn);
			} else {
				if(this.right != null)
					return this.right.findSNData(sn);
			}
			return retVal; // returns null if not found
		}
		
	}
	
	private static class SNRec {
		final String sn; // Primary Key
		final String altId;
		
		public SNRec(String line) {
			String vals[] = line.split("\\*");
			this.sn = vals[0];
			this.altId = vals[1];
		}
	}
	
}
