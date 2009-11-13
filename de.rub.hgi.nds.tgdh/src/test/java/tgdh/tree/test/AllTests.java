package tgdh.tree.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * With command <code>java tgdh.test.AllTests</code> will all tests be called. 
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class AllTests extends TestSuite {
	
	public AllTests(String name) {
	   	super(name);
	}
	
	/** Usage: <code>java tgdh.test.AllTests</code>*/	
	public static void main(String[] args) {
	   	System.out.println(treeSuite().toString());
	    TestRunner.run(treeSuite());   	
	}	
	
	/**
	 * A test which contains all other tests in package 
	 * <code>tgdh.tree.test</code>.
	 * @return a test which contains all other tests in package 
	 *         <code>tgdh.tree.test</code>.
	 */
	public static Test treeSuite(){
	   	String myName = "NDS TGDH Tree Test";
	   	TestSuite mySuite = new TestSuite(myName);
	   	mySuite.addTest(new TestSuite(TestCreate		. class));
		mySuite.addTest(new TestSuite(TestJoin			. class));
		mySuite.addTest(new TestSuite(TestLeave			. class));
		mySuite.addTest(new TestSuite(TestMerge			. class));
		mySuite.addTest(new TestSuite(TestPartition		. class));
	   	return mySuite;
	}
}