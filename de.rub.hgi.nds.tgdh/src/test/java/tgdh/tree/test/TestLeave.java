/*
 * Created on 2004-6-4
 *
 */
package tgdh.tree.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import tgdh.tree.*;
/**
 * This classes tests the function {@link tgdh.tree.BasicTree#leave(Node)}.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TestLeave extends TestCase {
	/**
	 * Makes sure that the case specified in figure 5 in
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * 
 	 * <pre>
	 *             <0,0>                       <0,0>
	 *           /       \                   /       \
	 *        <1,0>     <1,1>     --\    <1,0>       <1,1>  
	 *       /   \      /   \     --/    /    \      /    \
	 *    <2,0> <2,1><2,2> <2,3>       <2,0> <2,1> <2,2> <2,3> 
	 *     M1    M2   M3  /    \         M1    M2    M4    M5
	 *                  <3,6> <3,7>           Sponsor: M5
	 *                    M4    M5
	 *                           -M3
	 * </pre>
	 */ 
	
	public static void test_Specification()
	throws Exception
	{
		// Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		
		// add nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, 
				new Node(), M3, new Node(), M4, M5};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree before leave
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), " + 
					"<1,1>, <2,2>(M3), <2,3>, <3,6>(M4), <3,7>(M5)";
		assertEquals("Tree before leave", expTree, atree.toString());
	
		// M3 leaves the tree
		LeafNode sponsor = (LeafNode) atree.leave(M3);
			
		// expected tree after leave
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M4), <2,3>(M5)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		LeafNode expSponsor = M5;
		assertEquals("Sponsor", expSponsor, sponsor); 
	}

	/**
 	 * Tests the case that the sibling of the node to leave is not a leafnode. 
	 * 
	 * <pre>
	 *             <0,0>                       <0,0>
	 *           /       \                   /       \
	 *        <1,0>     <1,1>     --\    <1,0>       <1,1>  
	 *       /   \      /   \     --/    /    \      /    \
	 *    <2,0> <2,1><2,2> <2,3>       <2,0> <2,1> <2,2> <2,3> 
	 *     M1    M2   M3  /    \         M1    M2    M3    M4
	 *                  <3,6> <3,7>           Sponsor: M4
	 *                    M4    M5
	 *                           -M5
	 * </pre>
	 */ 	
	public static void test_2()
	throws Exception
	{
	    // Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		
		// add nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, 
				new Node(), M3, new Node(), M4, M5};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// built the tree
		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree before leave
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), " + 
					"<1,1>, <2,2>(M3), <2,3>, <3,6>(M4), <3,7>(M5)";
		assertEquals("Tree before leave", expTree, atree.toString());
	
		// M3 leaves the tree
		LeafNode sponsor = (LeafNode) atree.leave(M5);
			
		// expected tree after leave
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		LeafNode expSponsor = M4;
		assertEquals("Sponsor", expSponsor, sponsor); 
	}
	
	
	public static void main(String[] args) {
	    try{
	        TestRunner.run( TestLeave . class);
	        System.exit(0);
	    } catch(Exception e){
	        e.printStackTrace();
	        System.exit(-2);
	    }
	}

}
