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
 * This class tests the function {@link tgdh.tree.BasicTree#merge(BasicTree[])}</code>. 
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TestMerge extends TestCase {
	/**
	 * Tests the case that the two to merged trees are of the same height and the name 
	 * the leftmost member of the tree 1 if lexicographically before that of tree 2.<br />
	 * Part 1: View of the members in tree 1.
	 * <pre>
	 *         <0,0>                <0,0>                            <0,0>
	 *       /      \              /     \                  /                     \
	 *    <1,0>     <1,1>   +    <1,0>  <1,1>             <1,0>                  <1,1>  
	 *   /    \    /    \       /  \      M7  --\ 	   /      \               /      \
	 *<2,0> <2,1><2,2> <2,3> <2,0> <2,1>      --/     <2,0>     <2,1>       <2,2>    <2,3>
 	 * M1     M2   M3   M4 	M5   M6                 /    \    /   \        /    \     M7
	 *         (Left)              (Right)         <3,0> <3,1> <3,2> <3,3> <3,4> <3,5>
	 *                                              M1     M2   M3   M4     M5   M6
	 * 
	 * 	    tree 1   		tree 2	                    tree 1' <- tree 1 + tree 2
	 *                                                          Sponsors: M4
	 * </pre>
	 */ 	
	public static void test_2_trees_of_same_height_lr_view_1()
		throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");


		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M1, M2, new Node(), M3, M4};

		// Tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), new Node(), M5, M6, M7}; 
				
		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
			
		// build tree1
		BasicTree tree_1 = new BasicTree(tinfo);
			
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), " + 
			"<1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree before leave", expTree, tree_1.toString());
			
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M5");
			
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
			
		// expected tree before merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M5), <2,1>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
					
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2};
		LeafNode[] sponsors = tree_1.merge(trees);
			
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
			"<2,1>, <3,2>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>(M6), <2,3>(M7)";
		assertEquals("Tree after merge", expTree, tree_1.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}

	/**
	 * Tests the case that the two to merged trees are of the same height and the name 
	 * the leftmost member of the tree 1 if lexicographically before that of tree 2.<br />
	 * Part 1: View of the members in tree 2.
	 * <pre>
	 *         <0,0>                <0,0>                            <0,0>
	 *       /      \              /     \                  /                     \
	 *    <1,0>     <1,1>   +    <1,0>  <1,1>             <1,0>                  <1,1>  
	 *   /    \    /    \       /  \      M7  --\ 	   /      \               /      \
	 *<2,0> <2,1><2,2> <2,3> <2,0> <2,1>      --/     <2,0>     <2,1>       <2,2>    <2,3>
	 * M1     M2   M3   M4 	M5   M6                 /    \    /   \        /    \     M7
	 *         (Left)              (Right)         <3,0> <3,1> <3,2> <3,3> <3,4> <3,5>
	 *                                              M1     M2   M3   M4     M5   M6
	 * 
	 * 	    tree 1   		tree 2	                    tree 2' <- tree 1 + tree 2
	 *                                                           Sponsors: M4  
	 *  </pre>
	 */ 	
	public static void test_2_trees_of_same_height_lr_view_2()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");


		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M1, M2, new Node(), M3, M4};

		// Tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), new Node(), M5, M6, M7}; 
		
		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
			
		// build tree1
		BasicTree tree_1 = new BasicTree(tinfo);
			
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), " + 
			"<1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree before leave", expTree, tree_1.toString());
		
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M5");
			
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
			
		// expected tree before merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M5), <2,1>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
			
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1};
		LeafNode[] sponsors = tree_2.merge(trees);
			
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
			"<2,1>, <3,2>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>(M6), <2,3>(M7)";
		assertEquals("Tree after merge", expTree, tree_2.toString());
		
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}
	
	/**
	 * Tests the case that the two to merged trees are of the same height and the name 
	 * the leftmost member of the tree 1 if lexicographically after that of tree 2.<br />
	 * Part 1: View of the members in tree 1.
	 * <pre>
     * 
     *         <0,0>		        <0,0>                          <0,0>
     *       /      \		      /      \	                 /               \
     *    <1,0>     <1,1>   +    <1,0>   <1,1>            <1,0>              <1,1>  
     *   /    \    /    \        /   \     M7  --\       /      \          /        \
     *<2,0> <2,1> <2,2> <2,3> <2,0> <2,1>      --/     <2,0>   <2,1>    <2,2>      <2,3>
	 *M5     M2    M3   M4 	 M1   M6                 /    \    M7     /   \      /   \  
	 *   (Right)              (Left)                <3,0> <3,1>     <3,4> <3,5> <3,6> <3,7>
     *                                               M1     M6        M5    M2    M3   M4
     * 
	 * 	    tree 1   		tree 2	                  tree 1' <- tree 1 + tree 2
	 *                                                         Sponsors: M7
	 * </pre>
	 */ 	
	public static void test_2_trees_of_same_height_rl_view_1()		
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");


		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M5, M2, new Node(), M3, M4};

		// Tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), new Node(), M1, M6, M7}; 
		
		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M5");

		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M5), <2,1>(M2), " + 
			"<1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree before leave", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M1");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2};
		LeafNode[] sponsors = tree_1.merge(trees);
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M6), <2,1>(M7), " + 
				  "<1,1>, <2,2>, <3,4>(M5), <3,5>(M2), <2,3>, <3,6>(M3), <3,7>(M4)";
		assertEquals("Tree after merge", expTree, tree_1.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M7));
	}


	/**
	 * Tests the case that the two to merged trees are of the same height and the name 
	 * the leftmost member of the tree 1 if lexicographically after that of tree 2.<br />
	 * Part 1: View of the members in tree 2.
	 * <pre>
	 * 
	 *         <0,0>		        <0,0>                          <0,0>
	 *       /      \		      /      \	                 /               \
	 *    <1,0>     <1,1>   +    <1,0>   <1,1>            <1,0>              <1,1>  
	 *   /    \    /    \        /   \     M7  --\       /      \          /        \
	 *<2,0> <2,1> <2,2> <2,3> <2,0> <2,1>      --/     <2,0>   <2,1>    <2,2>      <2,3>
	 *M5     M2    M3   M4 	 M1   M6                 /    \    M7     /   \      /   \  
	 *   (Right)              (Left)                <3,0> <3,1>     <3,4> <3,5> <3,6> <3,7>
	 *                                               M1     M6        M5    M2    M3   M4
	 * 
	 * 	    tree 1   		tree 2	                  tree 2' <- tree 1 + tree 2
	 *                                                          Sponsors: M7
	 * </pre>
	 */ 	
	public static void test_2_trees_of_same_height_rl_view_2()		
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");


		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M5, M2, new Node(), M3, M4};

		// Tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), new Node(), M1, M6, M7}; 
		
		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M5");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M5), <2,1>(M2), " + 
			"<1,1>, <2,2>(M3), <2,3>(M4)";
		assertEquals("Tree before leave", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M1");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1};
		LeafNode[] sponsors = tree_2.merge(trees);
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M6), <2,1>(M7), " + 
				  "<1,1>, <2,2>, <3,4>(M5), <3,5>(M2), <2,3>, <3,6>(M3), <3,7>(M4)";
		assertEquals("Tree after merge", expTree, tree_2.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M7));
	}


	/**
	 * Tests the merge of more than two trees<br>
	 * Part 1: view of the members in tree 1.
	 * 
	 * <pre>
	 *
     *         <0,0>                <0,0>                         <0,0>
     *       /      \              /    \                   /               \
     *    <1,0>     <1,1>   +    <1,0>  <1,1>           <1,0>               <1,1>  
     *     M1      /    \         M5      M6    --\    /      \            /      \
     *           <2,2> <2,3>                    --/  <2,0>   <2,1>       <2,2>    <2,3>
     *          /   \   M4                             M1    /   \      /    \     M4
     *       <3,4> <3,5>                                   <3,2> <3,3> <3,4> <3,5>
     *         M2   M3                                      M5    M6    M2     M3    
	 * 
	 *          tree 1                                     temp <- tree 1 + tree 2
	 * 
	 *  
     *       <0,0>                                <0,0>
     *      /     \    --\               /                    \
     * + <1,0>  <1,1>  --/            <1,0>                  <1,1>
     *    M7     M8               /              \           /   \
     *      tree 3            <2,0>              <2,1>     <2,2> <2,3>
     *                        /   \             /     \     M7    M8
     *                     <3,0>  <3,1>       <3,2>   <3,3>
     *                       M1   /    \     /    \   M4
     *                         <4,2> <4,3> <4,4> <4,5>
     *                           M5    M6    M2   M3
	 * 
	 *       tree 3            tree 1' <- temp + tree 3
	 *                                  Sponsors: M1, M4
	 * </pre>
	 */  	
	public static void test_more_trees_view_1()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");
		LeafNode M8 = new LeafNode("M8");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), M1, new Node(), 
			new Node(), M2, M3, M4};
	
		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M5, M6};

		// tree 3
		// add nodes to the list
		Node[] nodes_3 = new Node[]{new Node(), M7, M8};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>(M1), <1,1>, <2,2>, " + 
				"<3,4>(M2), <3,5>(M3), <2,3>(M4)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
					
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M5");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M5), <1,1>(M6)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
	
		tinfo = new TreeInfo(
		        nodes_3, TreeInfo.PREORDER,
		        "M7");
		// build tree 3
		BasicTree tree_3 = new BasicTree(tinfo);
		// expected tree 3 before merge
		expTree = "Preorder: <0,0>, <1,0>(M7), <1,1>(M8)";
		assertEquals("Tree c before merge", expTree, tree_3.toString());
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2, tree_3};
		LeafNode[] sponsors = tree_1.merge(trees);
			
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>, <4,2>(M5), " + 
 				"<4,3>(M6), <2,1>, <3,2>, <4,4>(M2), <4,5>(M3), " + 
 				"<3,3>(M4), <1,1>, <2,2>(M7), <2,3>(M8)";
		assertEquals("Tree a after merge", expTree, tree_1.toString());
		
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M1));
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}

	

	/**
	 * Tests the merge of more than two trees<br>
	 * Part 2: view of the members in tree 2
	 * 
	 * <pre>
	 *
	 *         <0,0>                <0,0>                         <0,0>
	 *       /      \              /    \                   /               \
	 *    <1,0>     <1,1>   +    <1,0>  <1,1>           <1,0>               <1,1>  
	 *     M1      /    \         M5      M6    --\    /      \            /      \
	 *           <2,2> <2,3>                    --/  <2,0>   <2,1>       <2,2>    <2,3>
	 *          /   \   M4                             M1    /   \      /    \     M4
	 *       <3,4> <3,5>                                   <3,2> <3,3> <3,4> <3,5>
	 *         M2   M3                                      M5    M6    M2     M3    
	 * 
	 *          tree 1                                    temp  <- tree 1 + tree 2
	 * 
	 *  
	 *       <0,0>                                <0,0>
	 *      /     \    --\               /                    \
	 * + <1,0>  <1,1>  --/            <1,0>                  <1,1>
	 *    M7     M8               /              \           /   \
	 *      tree 3            <2,0>              <2,1>     <2,2> <2,3>
	 *                        /   \             /     \     M7    M8
	 *                     <3,0>  <3,1>       <3,2>   <3,3>
	 *                       M1   /    \     /    \   M4
	 *                         <4,2> <4,3> <4,4> <4,5>
	 *                           M5    M6    M2   M3
	 * 
	 *       tree 3            tree 2' <- temp + tree 3
	 *                            Sponsors: M1, M4
	 * </pre>
	 */  	
	public static void test_more_trees_view_2()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");
		LeafNode M8 = new LeafNode("M8");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), M1, new Node(), 
			new Node(), M2, M3, M4};
	
		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M5, M6};

		// tree 3
		// add nodes to the list
		Node[] nodes_3 = new Node[]{new Node(), M7, M8};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>(M1), <1,1>, <2,2>, " + 
				"<3,4>(M2), <3,5>(M3), <2,3>(M4)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M5");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M5), <1,1>(M6)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
	
		tinfo = new TreeInfo(
		        nodes_3, TreeInfo.PREORDER,
		        "M7");
		// build tree 3
		BasicTree tree_3 = new BasicTree(tinfo);
		// expected tree 3 before merge
		expTree = "Preorder: <0,0>, <1,0>(M7), <1,1>(M8)";
		assertEquals("Tree c before merge", expTree, tree_3.toString());

		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1, tree_3};
		LeafNode[] sponsors = tree_2.merge(trees);
		
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>, <4,2>(M5), " + 
				"<4,3>(M6), <2,1>, <3,2>, <4,4>(M2), <4,5>(M3), " + 
				"<3,3>(M4), <1,1>, <2,2>(M7), <2,3>(M8)";
		assertEquals("Tree a after merge", expTree, tree_2.toString());
		
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M1));
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}

	/**
	 * Tests the merge of more than two trees<br>
	 * Part 3: view of the members in tree 3.
	 * 
	 * <pre>
	 *
	 *         <0,0>                <0,0>                         <0,0>
	 *       /      \              /    \                   /               \
	 *    <1,0>     <1,1>   +    <1,0>  <1,1>           <1,0>               <1,1>  
	 *     M1      /    \         M5      M6    --\    /      \            /      \
	 *           <2,2> <2,3>                    --/  <2,0>   <2,1>       <2,2>    <2,3>
	 *          /   \   M4                             M1    /   \      /    \     M4
	 *       <3,4> <3,5>                                   <3,2> <3,3> <3,4> <3,5>
	 *         M2   M3                                      M5    M6    M2     M3    
	 * 
	 *          tree 1                                    temp <- tree 1 + tree 2
	 * 
	 *  
	 *       <0,0>                                <0,0>
	 *      /     \    --\               /                    \
	 * + <1,0>  <1,1>  --/            <1,0>                  <1,1>
	 *    M7     M8               /              \           /   \
	 *      tree 3            <2,0>              <2,1>     <2,2> <2,3>
	 *                        /   \             /     \     M7    M8
	 *                     <3,0>  <3,1>       <3,2>   <3,3>
	 *                       M1   /    \     /    \   M4
	 *                         <4,2> <4,3> <4,4> <4,5>
	 *                           M5    M6    M2   M3
	 * 
	 *       tree 3            tree 3' <- temp + tree 3
	 *                                 Sponsors: M1, M4
	 * </pre>
	 */  	
	public static void test_more_trees_view_3()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");
		LeafNode M8 = new LeafNode("M8");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), M1, new Node(), 
			new Node(), M2, M3, M4};
	
		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M5, M6};

		// tree 3
		// add nodes to the list
		Node[] nodes_3 = new Node[]{new Node(), M7, M8};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>(M1), <1,1>, <2,2>, " + 
				"<3,4>(M2), <3,5>(M3), <2,3>(M4)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M5");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M5), <1,1>(M6)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		tinfo = new TreeInfo(
		        nodes_3, TreeInfo.PREORDER,
		        "M7");

		// build tree 3
		BasicTree tree_3 = new BasicTree(tinfo);
		// expected tree 3 before merge
		expTree = "Preorder: <0,0>, <1,0>(M7), <1,1>(M8)";
		assertEquals("Tree c before merge", expTree, tree_3.toString());

		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1, tree_2};
		LeafNode[] sponsors = tree_3.merge(trees);
			
		// expected tree 3' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>, <4,2>(M5), " + 
				"<4,3>(M6), <2,1>, <3,2>, <4,4>(M2), <4,5>(M3), " + 
				"<3,3>(M4), <1,1>, <2,2>(M7), <2,3>(M8)";
		assertEquals("Tree a after merge", expTree, tree_3.toString());
			
		assertEquals("Sponsors number", 2, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M1));
		assertTrue(TgdhUtil.contains(sponsors, M4));
	}

		
	/**
	 * Makes sure that the case specified in figure 9 in
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * Part 1: View of the members in tree 1
 	 * <pre>
	 *        <0,0>                 <0,0>                         <0,0>
	 *      /        \             /     \                   /               \
	 *   <1,0>     <1,1>   +    <1,0>  <1,1>            <1,0>                 <1,1>  
	 *  /     \    /    \        M6      M7  --\      /        \            /      \
	 *<2,0> <2,1> <2,2> <2,3>                --/   <2,0>      <2,1>        <2,2>    <2,3>
	 * M1    M2  /   \    M5                       /    \     /   \      /    \    M5
	 *         <3,4> <3,5>                      <3,0> <3,1> <3,2> <3,3> <3,4> <3,5>
	 *          M3    M4                         M1    M2    M6    M7   M3    M4   
	 * 
	 *        tree 1               tree 2              tree 1' <- tree 1 + tree 2
	 *                                                          Sponsors: M2
	 * </pre>
	 */
	public static void test_Specification_view_1()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M1, M2,	new Node(), new Node(), M3, M4, M5};

		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M6, M7};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>, " + 
				"<3,4>(M3), <3,5>(M4), <2,3>(M5)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M6");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2};
		LeafNode[] sponsors = tree_1.merge(trees);
		
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), <2,1>, <3,2>(M6), " + 
					"<3,3>(M7), <1,1>, <2,2>, <3,4>(M3), <3,5>(M4), <2,3>(M5)"; 
		assertEquals("Tree a after merge", expTree, tree_1.toString());
		
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M2));
	}

	/**
	 * Makes sure that the case specified in figure 9 in
	 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 	 * Tree-based Group Key Agreement</a> can be correctly proceed.<br />
 	 * Part 2: View of the members in tree 2
	 * <pre>
	 *        <0,0>                 <0,0>                         <0,0>
	 *      /        \             /     \                   /               \
	 *   <1,0>     <1,1>   +    <1,0>  <1,1>            <1,0>                 <1,1>  
	 *  /     \    /    \        M6      M7  --\      /        \            /      \
	 *<2,0> <2,1> <2,2> <2,3>                --/   <2,0>      <2,1>        <2,2>    <2,3>
	 * M1    M2  /   \    M5                       /    \     /   \      /    \    M5
	 *         <3,4> <3,5>                      <3,0> <3,1> <3,2> <3,3> <3,4> <3,5>
	 *          M3    M4                         M1    M2    M6    M7   M3    M4   
	 * 
	 *        tree 1               tree 2              tree 1' <- tree 1 + tree 2
	 *                                                     Sponsors: M2
	 * </pre>
	 */
	public static void test_Specification_view_2()
	throws Exception
	{
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");
		LeafNode M6 = new LeafNode("M6");
		LeafNode M7 = new LeafNode("M7");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), 
			M1, M2,	new Node(), new Node(), M3, M4, M5};

		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M6, M7};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>, <2,2>, " + 
				"<3,4>(M3), <3,5>(M4), <2,3>(M5)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M6");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M6), <1,1>(M7)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1};
		LeafNode[] sponsors = tree_2.merge(trees);
			
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), <2,1>, <3,2>(M6), " + 
					"<3,3>(M7), <1,1>, <2,2>, <3,4>(M3), <3,5>(M4), <2,3>(M5)"; 
		assertEquals("Tree a after merge", expTree, tree_2.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M2));
	}


	/**
	 * Tests the case that the height difference of two trees is one, and the lower tree 
	 * must be joined to the root.
	 * Part 1: View of the members in tree 1.
	 * <pre>
     *                                               <0,0>
     *                                            /          \
     *      <0,0>          <0,0>                <1,0>       <1,1>
     *     /     \        /    \               /     \     /     \
     *   <1,0>  <1,1>   <1,0> <1,1>         <2,0>  <2,1> <2,2> <2,3>
     *  /    \   M3   +  M4     M5  --\    /     \   M3    M4    M5
     *<2,0> <2,1>                   --/ <3,0>  <3,1>	 
     *  M1    M2                          M1     M2
	 *
	 *    tree 1          tree 2            tree 1' <- tree 1 + tree 2
	 *                                               Sponsors: M3
	 *</pre>
	 */ 
	public static void test_join_to_root_hd_equal_1_view_1()
	throws Exception
	{
		// Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), M1, M2, M3};

		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M4, M5};
		
		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
		
		// Tree 2
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M4");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M4), <1,1>(M5)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2};
		LeafNode[] sponsors = tree_1.merge(trees);
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
				  "<2,1>(M3), <1,1>, <2,2>(M4), <2,3>(M5)";
		assertEquals("Tree after merge", expTree, tree_1.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M3));
	}
	
	/**
	 * Tests the case that the height difference of two trees is one, and the lower tree 
	 * must be joined to the root.
	 * Part 2: View of the members in tree 2.
	 * <pre>
	 *                                               <0,0>
	 *                                            /          \
	 *      <0,0>          <0,0>                <1,0>       <1,1>
	 *     /     \        /    \               /     \     /     \
	 *   <1,0>  <1,1>   <1,0> <1,1>         <2,0>  <2,1> <2,2> <2,3>
	 *  /    \   M3   +  M4     M5  --\    /     \   M3    M4    M5
	 *<2,0> <2,1>                   --/ <3,0>  <3,1>	 
	 *  M1    M2                          M1     M2
	 *
	 *    tree 1          tree 2            tree 2' <- tree 1 + tree 2
	 *                                          Sponsors: M3
	 *</pre>
	 */ 
	public static void test_join_to_root_hd_equal_1_view_2()
	throws Exception
	{
		// Create the tree before action
		LeafNode M1 = new LeafNode("M1");
		LeafNode M2 = new LeafNode("M2");
		LeafNode M3 = new LeafNode("M3");
		LeafNode M4 = new LeafNode("M4");
		LeafNode M5 = new LeafNode("M5");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), M1, M2, M3};
		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M4, M5};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>(M2), <1,1>(M3)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
		
		// Tree 2
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M4");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M4), <1,1>(M5)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1};
		LeafNode[] sponsors = tree_2.merge(trees);
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>, <3,0>(M1), <3,1>(M2), " + 
				  "<2,1>(M3), <1,1>, <2,2>(M4), <2,3>(M5)";
		assertEquals("Tree after merge", expTree, tree_2.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M3));
	}


	/**
	 * Tests the case that the higher tree's structure is symmetric. <br>
	 * Part 1: View of tree 1
	 * <pre>
 	 *              <0,0>              
 	 *        /               \          
	 *      <1,0>            <1,1>     
	 *    /     \           /     \   
	 * <2,0>  <2,1>      <2,2>   <2,3>               <0,0>
	 *  M1    /   \     /     \    M8     +         /     \
	 *     <3,2> <3,3> <3,4> <3,5>                <1,0>  <1,1>
	 *     /   \  M4   M5   /    \                 M9     M10        
	 *  <4,4> <4,5>      <4,10> <4,11>
	 *   M2    M3          M6     M7             
	 * 
	 *     tree 1                                    tree 2
	 *                          
 	 *                   <0,0>              
 	 *           /                 \          
	 *         <1,0>               <1,1>     
	 *       /     \            /         \   
	 *--\ <2,0>  <2,1>      <2,2>       <2,3>            
	 *--/  M1    /   \     /     \      /     \
	 *        <3,2> <3,3> <3,4> <3,5> <3,6>  <3,7>
	 *        /   \  M4   M5   /    \  M8    /    \        
	 *     <4,4> <4,5>      <4,10> <4,11> <4,14> <4,15>
	 *      M2    M3          M6     M7     M9     M10         
	 * 
	 *     tree 1' = tree 1 + tree 2
	 *        Sponsor: M8
	 * </pre>
	 */
	public static void test_symmetry_view_1()
	throws Exception
	{
		LeafNode M1  = new LeafNode("M1" );
		LeafNode M2  = new LeafNode("M2" );
		LeafNode M3  = new LeafNode("M3" );
		LeafNode M4  = new LeafNode("M4" );
		LeafNode M5  = new LeafNode("M5" );
		LeafNode M6  = new LeafNode("M6" );
		LeafNode M7  = new LeafNode("M7" );
		LeafNode M8  = new LeafNode("M8" );
		LeafNode M9  = new LeafNode("M9" );
		LeafNode M10 = new LeafNode("M10");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), M1,  
			new Node(), new Node(), M2, M3, M4, new Node(), 
			new Node(),	M5,	new Node(), M6, M7, M8};

		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M9, M10};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>, <3,2>, " +
			"<4,4>(M2), <4,5>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>, <4,10>(M6), <4,11>(M7), <2,3>(M8)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M9");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M9), <1,1>(M10)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_2};
		LeafNode[] sponsors = tree_1.merge(trees);
			
		// expected tree 1' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>, <3,2>, " +
			"<4,4>(M2), <4,5>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>, <4,10>(M6), <4,11>(M7), <2,3>, <3,6>(M8), <3,7>, <4,14>(M9), <4,15>(M10)";
		assertEquals("Tree a after merge", expTree, tree_1.toString());
		
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M8));
	}

	/**
	 * Tests the case that the higher tree's structure is symmetric. <br>
	 * Part 2: View of tree 2
	 * <pre>
	 *              <0,0>              
	 *        /               \          
	 *      <1,0>            <1,1>     
	 *    /     \           /     \   
	 * <2,0>  <2,1>      <2,2>   <2,3>               <0,0>
	 *  M1    /   \     /     \    M8     +         /     \
	 *     <3,2> <3,3> <3,4> <3,5>                <1,0>  <1,1>
	 *     /   \  M4   M5   /    \                 M9     M10        
	 *  <4,4> <4,5>      <4,10> <4,11>
	 *   M2    M3          M6     M7             
	 * 
	 *     tree 1                                    tree 2
	 *                          
	 *                   <0,0>              
	 *           /                 \          
	 *         <1,0>               <1,1>     
	 *       /     \            /         \   
	 *--\ <2,0>  <2,1>      <2,2>       <2,3>            
	 *--/  M1    /   \     /     \      /     \
	 *        <3,2> <3,3> <3,4> <3,5> <3,6>  <3,7>
	 *        /   \  M4   M5   /    \  M8    /    \        
	 *     <4,4> <4,5>      <4,10> <4,11> <4,14> <4,15>
	 *      M2    M3          M6     M7     M9     M10         
	 * 
	 *     tree 2' = tree 1 + tree 2
	 *        Sponsor: M8
	 * </pre>
	 */
	public static void test_symmetry_view_2()
	throws Exception
	{
		LeafNode M1  = new LeafNode("M1" );
		LeafNode M2  = new LeafNode("M2" );
		LeafNode M3  = new LeafNode("M3" );
		LeafNode M4  = new LeafNode("M4" );
		LeafNode M5  = new LeafNode("M5" );
		LeafNode M6  = new LeafNode("M6" );
		LeafNode M7  = new LeafNode("M7" );
		LeafNode M8  = new LeafNode("M8" );
		LeafNode M9  = new LeafNode("M9" );
		LeafNode M10 = new LeafNode("M10");

		// tree 1
		// add nodes to the list
		Node[] nodes_1 = new Node[]{new Node(), new Node(), M1,  
			new Node(), new Node(), M2, M3, M4, new Node(), 
			new Node(),	M5,	new Node(), M6, M7, M8};

		// tree 2
		// add nodes to the list
		Node[] nodes_2 = new Node[]{new Node(), M9, M10};

		TreeInfo tinfo = new TreeInfo(
		        nodes_1, TreeInfo.PREORDER,
		        "M1");
		// build tree 1
		BasicTree tree_1 = new BasicTree(tinfo);
		// expected tree 1 before merge
		String expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>, <3,2>, " +
			"<4,4>(M2), <4,5>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>, <4,10>(M6), <4,11>(M7), <2,3>(M8)";
		assertEquals("Tree a before merge", expTree, tree_1.toString());
						
		tinfo = new TreeInfo(
		        nodes_2, TreeInfo.PREORDER,
		        "M9");
		// build tree 2
		BasicTree tree_2 = new BasicTree(tinfo);
		// expected tree 2 before merge
		expTree = "Preorder: <0,0>, <1,0>(M9), <1,1>(M10)";
		assertEquals("Tree b before merge", expTree, tree_2.toString());
		
		// added the to merged trees
		BasicTree[] trees = new BasicTree[]{tree_1};
		LeafNode[] sponsors = tree_2.merge(trees);
			
		// expected tree 2' after merge
		expTree = "Preorder: <0,0>, <1,0>, <2,0>(M1), <2,1>, <3,2>, " +
			"<4,4>(M2), <4,5>(M3), <3,3>(M4), <1,1>, <2,2>, <3,4>(M5), " + 
			"<3,5>, <4,10>(M6), <4,11>(M7), <2,3>, <3,6>(M8), <3,7>, <4,14>(M9), <4,15>(M10)";
		assertEquals("Tree a after merge", expTree, tree_2.toString());
			
		assertEquals("Sponsors number", 1, sponsors.length);
		assertTrue(TgdhUtil.contains(sponsors, M8));
	}

	
	public static void main(String[] args) {
	    try{
	        TestRunner.run( TestMerge . class);
	        System.exit(0);
	    } catch(Exception e){
	        e.printStackTrace();
	        System.exit(-2);
	    }
	}
}
