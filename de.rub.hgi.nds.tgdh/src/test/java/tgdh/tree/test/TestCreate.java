/*
 * Created on 2004-6-4
 *
 */
package tgdh.tree.test;

import tgdh.tree.*;
import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * This class tests the constructors of {@link tgdh.tree.Tree}.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TestCreate extends TestCase {
	/**
	 * Makes sure that a new tree with only one node can be constructed. The order of 
	 * the nodes in the {@link tgdh.tree.TreeInfo} is PREORDER <br />
     * <pre>
	 * 	  The tree to be tested is:
	 *             <0,0>
	 *              M1
	 * 
	 * PreOrder:  {<0,0>M1}
	 * </pre>
	 */	
	public static void testPreOrder_1_Node(){
		// add nodes to the list
		LeafNode M1 = new LeafNode("M1");
		Node[] nodes = new Node[]{M1};
		
		try {
			TreeInfo tinfo = new TreeInfo(
			        nodes, TreeInfo.PREORDER,
			        "M1");
			
			// build the tree
			Tree atree = new Tree(tinfo);
			
			//expected tree
			String expectedTree = "Preorder: <0,0>(M1)";
			assertEquals("Tree information", expectedTree, atree.toString()); 
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * Makes sure that a new tree with more nodes can be constructed. The order of 
	 * the nodes in the {@link tgdh.tree.TreeInfo} is PREORDER <br />
	 * The tree to be tested is:
	 * <pre>
	 *                                     <0,0>
	 *                                   /        \
	 *                                <1,0>      <1,1>
	 *                               /     \    /      \
	 *                             <2,0> <2,1> <2,2>  <2,3>
	 *                             M1      M2   M3     M4
	 * PreOrder:  {<0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)}
	 * </pre>
	 */	
	public static void testPreorder()
	throws Exception
	{	
		// add nodes to the list
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");

		Node[] nodes = new Node[]{
			new Node(), new Node(), M1, M2, new Node(), M3, M4};
			
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		//expected tree
		String expectedTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree information", expectedTree, atree.toString()); 
	}
	

	/**
	 * Makes sure that  an IllegalArgumentException will be thrown, when the order of the nodes
	 * in a {@link tgdh.tree.TreeInfo} is INORDER. Since 
	 */
	public static void testInorder()
	throws Exception
	{
		Node[] nodes = new Node[]{new Node()};
		
		try {
			TreeInfo tinfo = new TreeInfo(
			        nodes, TreeInfo.INORDER,
			        "M1");
			// build the tree
			BasicTree atree = new BasicTree(tinfo);
			
			// expected tree
			fail("Expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String expException = "Inorder is not supported!";
			assertEquals("Tree information", expException, e.getMessage());
		}
	}

	/**
	 * Makes sure that a new tree with only one node can be constructed. The order of 
	 * the nodes in the {@link tgdh.tree.TreeInfo} is POSTORDER <br />
	 * The tree to be tested is:
	 * <pre>
	 * Tree          <0,0>
	 *               (M1)
	 * PostOrder: {<0,0>(M1)}
	 * </pre>
	 */
	public static void testPostOrder_1_Node()
	throws Exception
	{
		// add nodes to the list
		Node[] nodes = new Node[]{new LeafNode("M1")};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.POSTORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree
		String expectedTree = "Preorder: <0,0>(M1)";
		assertEquals("Tree information", expectedTree, atree.toString()); 
	}
	
	
	/**
	 * Makes sure that a new tree with more nodes can be constructed. The order of 
	 * the nodes in the {@link tgdh.tree.TreeInfo} is POSTORDER <br />
	 * The tree to be tested is:<pre>
	 *                                     <0,0>
	 *                                   /        \
	 *                                <1,0>      <1,1>
	 *                               /     \    /      \
	 *                             <2,0> <2,1> <2,2>  <2,3>
	 *                             M1      M2   M3     M4
	 * PostOrder: {<2,0>M1, <2,1>M2, <1,0>, <2,2>M3, <2,3>M4, <1,1>, <0,0>}
	 * </pre>
	 */
	public static void testPostorder()
	throws Exception
	{
		// add nodes to the list
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");

		Node[] nodes = new Node[]{M1, M2, new Node(), M3, M4, new Node(), new Node()};

		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.POSTORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		// exptected tree
		String expectedTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree information", expectedTree, atree.toString()); 
	}

	
	public static void main(String[] args) {
	    try{
	        TestRunner.run( TestCreate . class);
	        System.exit(0);
	    } catch(Exception e){
	        e.printStackTrace();
	        System.exit(-2);
	    }
	}
}
