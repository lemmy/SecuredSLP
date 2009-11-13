/*
 * Created on 2004-6-4
 *
 */
package tgdh.tree.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import tgdh.tree.*;
/**
 * This classes tests the functions {@link tgdh.tree.BasicTree#join(Node)} and
 * {@link tgdh.tree.BasicTree#join(Node, int)}.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TestJoin extends TestCase {
	/**
	 * Makes sure that the case specified in  figure 3 in  
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * The function {@link tgdh.tree.BasicTree#join(Node)} will be here tested.
 	 * <pre>
	 *            <0,0>                    <0,0>
	 *           /    \                 /        \
	 *        <1,0>  <1,1>  --\       <1,0>     <1,1>  
	 *        /    \  M3    --/      /    \     /   \
	 *     <2,0> <2,1>            <2,0> <2,1> <2,2> <2,3>
	 *      M1     M2                M1    M2   M3    M4 (new node)
	 *                     join(M4)    Sponsor: M3
	 * </pre> 
	 */ 	
	public static void test_join_1_param_specification()
	throws Exception
	{
		// Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		
		// add nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, M3};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");

		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree before join
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree before join", expTree, atree.toString());

		// M4 joins the tree
		LeafNode sponsor = (LeafNode) atree.join(new LeafNode("M4"));

		// expected tree after join
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree after join", expTree, atree.toString());
			
		LeafNode expSponsor = M3;
		assertEquals("Sponsor", expSponsor, sponsor); 
	}

	/**
	 * Makes sure that the function {@link tgdh.tree.BasicTree#join(Node, int)} can 
	 * be proceed correctly. Here is the position available.
	 * <pre>
	 *            <0,0>                    <0,0>
	 *           /    \                 /        \
	 *        <1,0>  <1,1>  --\       <1,0>     <1,1>  
	 *        /    \  M3    --/      /     \      M3
	 *     <2,0> <2,1> *            <2,0>  <2,1>* 
	 *      M1     M2                M1   /    \  
	 *                                  <3,2> <3,3>    
	 *                                   M2    M4
	 *    *Position 5
	 *                      join(M4, 5)  Sponsor: M2 
	 * </pre>                
	 */ 	
	public static void test_join_2_params_LeafNode()
	throws Exception
	{
	    // Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		
		// add nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, M3};
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree before join
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree before join", expTree, atree.toString());

		// M4 joins in the tree at position 5 
		LeafNode sponsor = (LeafNode) atree.join(new LeafNode("M4"), 5);
		
		// expected tree after join
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>, <3,2>(M2), <3,3>(M4), <1,1>(M3)";
		assertEquals("Tree after join", expTree, atree.toString());
			
		LeafNode expSponsor = M2;
		assertEquals("Sponsor", expSponsor, sponsor); 
}

	/**
	 * Makes sure that the function {@link tgdh.tree.BasicTree#join(Node, int)} can 
	 * be proceed correctly. Here is the position not available. 
	 * <pre>
	 *            <0,0>                    <0,0>
	 *           /    \                 /        \
	 *        <1,0>  <1,1>  --\       <1,0>     <1,1>  
	 *        /    \  M3    --/      /    \     /   \
	 *     <2,0> <2,1>            <2,0> <2,1> <2,2> <2,3>
	 *      M1     M2                M1    M2   M3    M4 (new node)
	 *                     join(M4, 6)   Sponosr: M3
	 * </pre> 
	 */ 	
	public static void test_join_2_params_absent()
	throws Exception
	{
	    // Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		
		// add nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, M3};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		BasicTree atree = new BasicTree(tinfo);
			
		// expected tree before join
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree before join", expTree, atree.toString());

		// M4 joins the tree
		LeafNode sponsor = (LeafNode) atree.join(new LeafNode("M4"), 6);

		// expected tree after join
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree after join", expTree, atree.toString());
			
		LeafNode expSponsor = M3;
		assertEquals("Sponsor", expSponsor, sponsor); 
	}

	
	public static void main(String[] args) {
	    try{
	        TestRunner.run( TestJoin . class);
	        System.exit(0);
	    } catch(Exception e){
	        e.printStackTrace();
	        System.exit(-2);
	    }
	}

}
