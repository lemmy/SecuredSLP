package tgdh;

import java.security.Security;
import java.security.interfaces.DSAParams;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jgroups.Address;
import org.jgroups.stack.IpAddress;

import tgdh.tree.LeafNode;
import tgdh.tree.Tree;


public class Markus extends Thread {
	Worker worker = null;
	
	public static void main(String[] args){
		Markus aMarkus = new Markus();
		aMarkus.start();
	}
	
	public void run() {
		Security.addProvider(new BouncyCastleProvider());
	    try {
	        GroupConf aGroupConf = 
	        	// new GroupConf("groupconf");
	            new GroupConf("228.222.11.8",3141,"this.subject",512);
	        String group = aGroupConf.getGroupname();
	        DSAParams params = aGroupConf.getParams();
	        String mcast_address = aGroupConf.getMcast_address();
	        int mcast_port = aGroupConf.getMcast_port();
	        tgdh.comm.Communicator aCommunicator = new tgdh.comm.Communicator(mcast_address, mcast_port);              Address localaddress = aCommunicator.getLocalAdresse();
	        LeafNode owner = new LeafNode( TgdhUtil.getName((IpAddress)localaddress));
	        Tree aTree = new Tree(owner);
	        aTree.setKeyParams(params);
	        aTree.genOwnerKeyPair();                      
	        owner.setSignKey(owner.getPrivate());
	        owner.setVerifyKey(owner.getPublic());
	        worker = new Worker(group, aTree, aCommunicator, 50);          
        } catch (Exception e) {
	        e.printStackTrace();
	        System.exit(-2);
	    }
	    //if (true!this.starter) {
	        try{
	            worker.joinGroup();
	        }
	        catch(Throwable ex){
	            System.out.println(ex.getMessage());
	        }
	    //}
	}
}
