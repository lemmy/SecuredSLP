/*
 * Created on 2004-6-4
 *
 */
package tgdh.tree.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import tgdh.TgdhUtil;
import tgdh.tree.*;
/**
 * This class test the function {@link tgdh.tree.BasicTree#basicPartition(Node[])}</code>. 
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TestPartition extends TestCase {

	/**
	 * Makes sure that the case specified in  figure 7 in  
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * Part 1: View of M2, M3, M5, M6
 	 * 
 	 * <pre>
 	 *             <0,0>                         <0,0>
	 *           /       \                    /       \
	 *        <1,0>     <1,1>    --\       <1,0>       <1,1>  
	 *       /   \      /    \   --/       /   \      /    \
	 *    <2,0> <2,1> <2,2> <2,3>         <2,0> <2,1> <2,2> <2,3> 
	 *    /   \ M3	  M4  /    \          M2    M3    M5    M6
	 * <3,0> <3,1>	    <3,6> <3,7>
	 *  M1    M2	     M5    M6           Sponsors: M2, M6
	 *  
	 *                             -(M1, M4)
	 * </pre>  
	 */
	
	public static void test_specification_view_1()
		throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");

		// add nodes to list
		Node[] nodes = new Node[]{new Node(), new Node(), new Node(),
			M1, M2, M3, new Node(), M4, new Node(),  M5, M6};
			
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M2");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		//expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
				"<2,1>(M3), <1,1>, <2,2>(M4), <2,3>, <3,6>(M5), <3,7>(M6)";
		assertEquals("Tree before leave", expTree, atree.toString());
		
		// add the leafnodes who leaves the tree
		Node[] nodestoleave = new Node[]{M1, M4};
			
		// partition operation
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
			
		// expected tree after partition
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M2), <2,1>(M3), <1,1>, <2,2>(M5), <2,3>(M6)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M2));
		assertTrue(TgdhUtil.contains(sponsors, M6));
	}

	/**
	 * Makes sure that the case specified in  figure 7 in  
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * Part 2: View of M1, M4
	 * 
 	 * <pre>
 	 *             <0,0>                         <0,0>
	 *           /       \                    /       \
	 *        <1,0>     <1,1>    --\       <1,0>       <1,1>  
	 *       /   \      /    \   --/         M1         M4 
	 *    <2,0> <2,1> <2,2> <2,3>          
	 *    /   \ M3	  M4  /    \          
	 * <3,0> <3,1>	    <3,6> <3,7>
	 *  M1    M2	     M5    M6              Sponsors: M1, M4
	 *  
	 *                             -(M2, M3, M5, M6)
	 * </pre>  
	 */
	
	public static void test_specification_view_2()
		throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
				
		// add nodes to list
		Node[] nodes = new Node[]{new Node(), new Node(), new Node(),
			M1, M2, M3, new Node(), M4, new Node(),  M5, M6};

		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
			
		//expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
				"<2,1>(M3), <1,1>, <2,2>(M4), <2,3>, <3,6>(M5), <3,7>(M6)";
		assertEquals("Tree before leave", expTree, atree.toString());
		
		// add the leafnodes who leaves the tree
		Node[] nodestoleave = new Node[]{M2, M3, M5, M6};

		// partition operation
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
			
		// expected tree after partition
		expTree = "Preorder: <0,0>, <1,0>(M1), <1,1>(M4)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M1));
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}
		
	
	/**
	 * Tests the case that the nodes to leave contain brother nodes.
	 * Part 1: View of M3, M5, M6
	 * 
	 * <pre>
	 *             <0,0>                       <0,0>
	 *           /       \                   /       \
	 *        <1,0>      <1,1>      --\    <1,0>    <1,1>  
	 *        /   \	  /    \      --/     M3     /    \
	 *     <2,0> <2,1> <2,2> <2,3>               <2,2> <2,3> 
	 *    /   \  M3	  M4  /    \	            M5    M6
	 * <3,0> <3,1>	    <3,6> <3,7>
	 *  M1   M2            M5    M6
 	 *
 	 *                          -(M1, M2, M4)    Sponsor: (M3, M6)
	 * </pre> 
	 */
	public static void test_pairs_of_sibling_view_1()
		throws Exception
	{
		// add nodes to the list
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");

		Node[] nodes = new Node[]{new Node(), new Node(), new Node(),
			M1, M2, M3, new Node(), M4, new Node(), M5, M6};

		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M3");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
		//expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
			"<2,1>(M3), <1,1>, <2,2>(M4), <2,3>, <3,6>(M5), <3,7>(M6)";
		assertEquals("Tree before leave", expTree, atree.toString());
	
		Node[] nodestoleave = new Node[]{M1, M2, M4};

		// add the nodes who leave the tree to the list
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
		// expected tree after partition
		expTree = "Preorder: <0,0>, <1,0>(M3), <1,1>, <2,2>(M5), <2,3>(M6)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M3));
		assertTrue(TgdhUtil.contains(sponsors, M6));
	}	
	
	/**
	 * Tests the case that the nodes to leave contain brother nodes.
	 * Part 1: View of M1, M2, M4
	 * 
	 * <pre>
	 *             <0,0>                       <0,0>
	 *           /       \                   /       \
	 *        <1,0>      <1,1>      --\    <1,0>    <1,1>  
	 *        /   \	  /    \      --/     /    \     M4
	 *     <2,0> <2,1> <2,2> <2,3>      <2,0> <2,2> 
	 *    /   \  M3	  M4  /    \	     M1    M2
	 * <3,0> <3,1>	    <3,6> <3,7>
	 *  M1   M2            M5    M6       Sponsors: M2, M4
	 *
	 *                          -(M3, M5, M6)
	 * </pre> 
	 */
	public static void test_pairs_of_sibling_view_2()
		throws Exception
	{
		// add nodes to the list
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");

		Node[] nodes = new Node[]{new Node(), new Node(), new Node(),
			M1, M2, M3, new Node(), M4, new Node(), M5, M6};

		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
		//expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
			"<2,1>(M3), <1,1>, <2,2>(M4), <2,3>, <3,6>(M5), <3,7>(M6)";
		assertEquals("Tree before leave", expTree, atree.toString());
	
		// add the nodes who leave the tree to the list
		Node[] nodestoleave = new Node[]{M3, M5, M6};
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
		// expected tree after partition
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M4)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M2));
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}	


	/**
	 * Tests the case that the nodes to leave contains brother nodes, and after this 
	 * partition action the old root will be deleted. 
	 * Part 1: View of M3
	 * <pre>
	 *         <0,0>                    <0,0>
	 *        /     \                     M3
	 *     <1,0>    <1,1>   -\
	 *     /    \     M3    -/
	 *  <2,0>  <2,1>                   Sponsors: M3
	 *   M1      M2
	 *                     -(M1, M2)
	 * </pre>  
	 */		
	public static void test_delete_root_view_1()
		throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");

		// add the nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, M3};		

		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M3");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
		// expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree after leave", expTree, atree.toString());
			
		// add the leafnodes who leave the tree
		Node[] nodestoleave = new Node[]{M1, M2};
			
			
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
		
		// expected tree after partition
		expTree = "Preorder: <0,0>(M3)";			
		assertEquals("Tree after leave", expTree, atree.toString());
		
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M3));
	}
	
	/**
	 * Tests the case that after the  partition action the old root will be deleted. 
	 * Part 1: View of M1, M2
	 * <pre>
	 *         <0,0>                    <0,0>
	 *        /     \                 /       \    
	 *     <1,0>    <1,1>   -\     <1,0>     <1,1>
	 *     /    \     M3    -/      M1        M2
	 *  <2,0>  <2,1>                  sponsors: M2
	 *   M1      M2
	 *                     -(M3)
	 * </pre>  
	 */

		
	public static void test_delete_root_view_2()
		throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		// add the nodes to the list
		Node[] nodes = new Node[]{new Node(), new Node(), M1, M2, M3};		
		
		TreeInfo tinfo = new TreeInfo(
		        nodes, TreeInfo.PREORDER,
		        "M1");
		// build the tree
		BasicTree atree = new BasicTree(tinfo);
		// expected tree before partition
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree after leave", expTree, atree.toString());
		
		// add the leafnodes who leave the tree
		Node[] nodestoleave = new Node[]{M3};
						
		LeafNode[] sponsors = atree.basicPartition(nodestoleave);
			
		// expected tree after partition
		expTree = "Preorder: <0,0>, <1,0>(M1), <1,1>(M2)";			
		assertEquals("Tree after leave", expTree, atree.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M2));
	}	
	
	public static void main(String[] args) {
	    try{
	        TestRunner.run( TestPartition . class);
	        System.exit(0);
	    } catch(Exception e){
	        e.printStackTrace();
	        System.exit(-2);
	    }
	}

}
