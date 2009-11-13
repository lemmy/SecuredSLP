package tgdh;

import java.io.NotSerializableException;
import java.util.LinkedList;
import java.util.List;

import tgdh.comm.*;
import tgdh.tree.*;

/**
 * This class implements a worker which deals with the messages, events of the 
 * tgdh-protocol.  
 *
 * <p>It manages three Queues:<br>;
 * 1. inQueue       contains all the messages which received from the other members, 
 *                  and also PartitionMessage which generated locally. &lt;br&gr;
 * 2. outQueue      contains all the messages which should be broadcasted. &lt;br&gr;
 * 3. partitionList contains the members which leave the group which detected with
 *                  the suspect event. It doesn't contain the members which leaves
 *                  the group with a LeaveMessage.<br>; </p>
 * <p>In order to decide, which message should be ignored, it manages a sequence number
 * of the key tree. After the action of JOIN, LEAVE, PARTITION, the sequence number
 * will be incremented. By merging of two trees, first the sequence number will be 
 * incremented, then the greater sequence number will be treated as the new sequence 
 * number the new tree.</p> 
 * <p>All update messages will be ignored, if the contained sequence number of the 
 * updated key tree is less than that the key tree, or equal but the key tree 
 * has already all bkeys. If greater, it will be added again to the inQueue so 
 * that it can be processed at next time. </p>
 * <p>After receiving a message who is instance of JoinMessage or MergeMessage, check
 * whether the partitionList is empty. If not, generates a new PartitionMessage 
 * and adds it to the inQueue. Now the received message can be added to the 
 * inQueue. After receiving a LeaveMessage, if the partitionList is empty, the
 * received message will be added to the inQueue, else verify the signature of 
 * this message and adds the member which want to leave the group to the partitionList, 
 * and generate a new PartitionMessage, then adds it to the inQueue.</p>    
 *   
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class Worker extends Thread {	
    private int treeSequence = 0;		
    private int expectedAction;
    private boolean isSponsor;
	
    private String 		group;
    private Communicator	communicator;
    private Tree       		tree;
    private int				tryTimes;
       
    private List            inQueue;
    private List            outQueue;
    private PartitionList   partitionList;
    private Tree            anotherTree;
    private int             anotherTreeSequence;
    
    /**
     * Constructs a new worker with detail group name, key tree and communicator
     * @param group         the group name
     * @param tree          the key tree
     * @param communicator  the communicator
     * @throws TgdhException
     */    
    public Worker(
    		String 		 group,
            Tree 		 tree, 
            Communicator communicator,
            int tryTimes)
    	throws TgdhException
    {
    	this.group		  = group;
        this.tree 		  = tree;        
        this.communicator = communicator;
        this.tryTimes     = tryTimes;
        
        inQueue  	  = new LinkedList();
        outQueue 	  = new LinkedList();
        partitionList = new PartitionList(); 
 
		expectedAction = TgdhMessage.NOSPECIAL;
		       
        start();
    }
    
    
    /**
     * Returns the key tree
     * @return the key tree.
     */
    public Tree getTree() {
        return tree;
    }
    
    /**
     * Returns the group name
     * @return the group name
     */
    public String getGroup() {
    	return group;
    }
    
    
    /**
     * Returns the communocator
     * @return the communicator.
     */
    public Communicator getCommunicator() {
        return communicator;
    }
    
    
    private void processMessage(MessageWithTry message) 
    	throws NotSerializableException, TgdhException
    {
        boolean successful = true;
        
        TgdhMessage msg = message.getMessage();
        if(msg instanceof UpdateMessage){
            successful = process((UpdateMessage) msg);
        }
        else if(msg instanceof JoinMessage){
            successful = process((JoinMessage) msg);
        }
        else if(msg instanceof LeaveMessage){
            successful = process((LeaveMessage) msg);
        }
        else if(msg instanceof PartitionMessage){
            successful = process((PartitionMessage) msg);
        }
        else if(msg instanceof MergeMessage){
            successful = process((MergeMessage) msg);
        }
        else if(msg instanceof ErrorMessage){
            process((ErrorMessage) msg);
        }
        else{
            throw new IllegalArgumentException("unknown message type");
        }
        
        if( // message.getTimes2try() > 0 && 
        	!successful){
            inQueue.add(message);
            // System.out.println("added message to the inQueue");
            Thread.yield();
        }
    }
    
    private boolean process(JoinMessage message)
    	throws TgdhException
    {
	    if(expectedAction != TgdhMessage.NOSPECIAL){
	        return false;
	    }
	    	    
        int pos = message.getPosition();
		LeafNode newMember = message.getNewNode();
		
		System.out.println(newMember.getName() + 
			" joins in the group at position " + pos);
				
		TreeInfo tinfo = tree.join(newMember, pos);
		treeSequence ++;
				
		if(tinfo != null){  // sponsor
		    LeafNode owner = tree.getOwner();
		    TgdhMessage tmsg = new UpdateMessage(
			    	group,
			        TgdhMessage.JOIN,
			        treeSequence,
			        tinfo,
			        owner.getName(),
			        owner.getSignKey());

		    tmsg.sign();
		    tmsg.init4verify(owner.getVerifyKey());		    
		    outQueue.add(tmsg);
		    
			printTree();
		}else{  // no sponsor
			expectedAction = TgdhMessage.UPDATE;
		}
			
		return true;
   }
    
    private boolean process(LeaveMessage message)
    	throws TgdhException
    {
	    if(expectedAction != TgdhMessage.NOSPECIAL){
	        return false;
	    }
	    
        // get the copy of the node to leave in the tree
        LeafNode toleave = tree.leafNode( message.getLeave().getName());

        if(toleave == null){
            System.out.println("the signer isn't groupmember.");
            return true;
        }
        
	    System.out.println(toleave.getName() + " leaves the group.");				
		TreeInfo tinfo = tree.leave(toleave);
		treeSequence ++;
		
		if(tinfo != null) { // sponsor
	        LeafNode owner = tree.getOwner();
		    TgdhMessage tmsg = new UpdateMessage(
		    		group,
			        TgdhMessage.LEAVE,
			        treeSequence,
			        tinfo,
			        owner.getName(),
			        owner.getSignKey());
			tmsg.sign();
			tmsg.init4verify(owner.getVerifyKey());
			outQueue.add(tmsg);			
			printTree();
		}
		else{
			expectedAction = TgdhMessage.UPDATE;
		}
			
		return true;
    }
    
    private boolean process(PartitionMessage message)
    	throws TgdhException
    {   
		LeafNode[] msg_nodes = message.getToLeaves();

    	StringBuffer s = new StringBuffer();
    	for(int i = 0; i < msg_nodes.length - 1; i++){
    		s.append(msg_nodes[i].getName());
    		s.append(", "); 
    	}
    	s.append(msg_nodes[msg_nodes.length - 1].getName());
    	s.append(" leave group");
    	System.out.println(s.toString());
    	
	    if(expectedAction != TgdhMessage.NOSPECIAL){
	        return false;
	    }
	    
		int number = 0;
		// get copies of the nodes to leave in the tree
		for (int i = 0; i < msg_nodes.length; i++) {
		    msg_nodes[i] = tree.leafNode(msg_nodes[i].getName());
		    if(msg_nodes[i] != null) number ++;
        }
        
		LeafNode[] tree_nodes = new LeafNode[number];
		number = 0;
		for (int i = 0; i < tree_nodes.length; i++) {
			if(msg_nodes[i] != null){
				tree_nodes[number++] = msg_nodes[i];
			}
		}		    				
		TreeInfo tinfo = tree.partition(tree_nodes);
		treeSequence++;
		
		if(tinfo == null){
			expectedAction = TgdhMessage.UPDATE;
			return true;			
		}
		
		isSponsor = true;			
		if(tree.getRoot().getPublic() == null) {
			expectedAction = TgdhMessage.UPDATE;
		}
		else{
			isSponsor = false;
			printTree();	
		}		
		LeafNode owner = tree.getOwner();
		TgdhMessage tmsg = new UpdateMessage(
			  group, TgdhMessage.PARTITION, treeSequence, 
			  tinfo, owner.getName(), owner.getSignKey() );
		tmsg.sign();
		tmsg.init4verify(owner.getVerifyKey());
		outQueue.add(tmsg);
		return true;
    }
    
    private boolean process(MergeMessage message) throws TgdhException
    {
	  if(expectedAction != TgdhMessage.NOSPECIAL && 
	    expectedAction != TgdhMessage.MERGE){	return false;}	    
    	
	  LeafNode signer = tree.leafNode(message.getSignerName());
	  if(signer == null)
       	System.out.println(message.getSignerName() + " merges its group to us");       
      else{
       	System.out.println(message.getSignerName() + " merges our group into another");
      }

	  LeafNode owner = tree.getOwner();	    
	  Tree mergedTree = new Tree(message.getToMerge());

	  if(expectedAction == TgdhMessage.NOSPECIAL)
	  {	
        if(signer == null){ // other tree
          if(owner == tree.rightMost()){ // rightmost
			tree.genOwnerKeyPair();
			tree.computeKeys(owner.getParent());
			treeSequence ++;
			TgdhMessage tmsg = new MergeMessage(
					group,	treeSequence,  tree.treeInfo(), 
					owner.getName(), owner.getSignKey());
			tmsg.sign();
			tmsg.init4verify(owner.getVerifyKey());
			communicator.send(tmsg);				
			
		
			// merge the tree
			TreeInfo updatedTinfo = tree.merge(mergedTree);
			treeSequence = Math.max(treeSequence, message.getTreeSequence());
			treeSequence ++;			
			if(updatedTinfo != null) {
				tmsg = new UpdateMessage(group,	TgdhMessage.MERGE, treeSequence,
				      updatedTinfo, owner.getName(), owner.getSignKey());
				tmsg.sign();
				tmsg.init4verify(owner.getVerifyKey());				
				outQueue.add(tmsg);				
				printTree();				 
			} else{
				expectedAction = TgdhMessage.UPDATE;
			}
          }
          else{ // not rightmost, store the tree
          	  anotherTree = mergedTree;
          	  anotherTreeSequence = message.getTreeSequence();
          	  expectedAction = TgdhMessage.MERGE;
          }
        }
        else{ // only need to update the tree 
			tree.removeKeys (signer);
			tree.updatePublics(mergedTree);
			tree.computeKeys(owner.getParent());
			expectedAction = TgdhMessage.MERGE;
			treeSequence ++;
        }        
      }
      
      else{ // expectedAction is TgdhMessage.MERGE
		if(signer == null){ // other tree
			anotherTree = mergedTree;
			anotherTreeSequence = message.getTreeSequence();
		}
		else{ // only need to update the tree 
			tree.removeKeys (signer);
			tree.updatePublics(mergedTree);
			tree.computeKeys(owner.getParent());
			expectedAction = TgdhMessage.MERGE;
			treeSequence ++;
		}
		
		TreeInfo updatedTinfo = tree.merge(anotherTree);
		treeSequence = Math.max(treeSequence, anotherTreeSequence);
		treeSequence ++;
		anotherTree = null;
		anotherTreeSequence = 0;			
		if(updatedTinfo != null) {
			TgdhMessage tmsg = new UpdateMessage(group,	TgdhMessage.MERGE, 
				treeSequence, updatedTinfo, owner.getName(), owner.getSignKey());
			tmsg.sign();
			tmsg.init4verify(owner.getVerifyKey());				
			outQueue.add(tmsg);				
			expectedAction = TgdhMessage.NOSPECIAL;
			printTree();				 
		} else{
			expectedAction = TgdhMessage.UPDATE;
		}      	
      }
      
	  return true;
    }
    private boolean process(UpdateMessage message) 
    		throws TgdhException, NotSerializableException
    {   
	TreeInfo treeinfo    = message.getUpdatedTree();
	if( message.getTreeSequence() < treeSequence || 
	   (message.getTreeSequence() == treeSequence && 
	   	tree.getRoot().getPublic() != null))
	{		
		return true;  // ignore redudant update message
	}
		
  	if(expectedAction != TgdhMessage.UPDATE) {
   		System.out.println("The expected action isn't update");
   		return false;
   	}    	
    	
   	LeafNode signer      = null;
    LeafNode owner	     = tree.getOwner();
	Tree	 newTree     = new Tree(treeinfo);		

/*
    if(message.getCause() == TgdhMessage.MERGE) {
      	signer = tree.leafNode(message.getSignerName());
       	if(signer == null) {        		
       		// not in the same group as sender of this message
       		// find my position in the new tree
       		LeafNode newI = newTree.leafNode(owner.getName());        		
			// update the public keys of the new nodes
			tree.setRoot(newTree.getRoot());					
			newI.setKeys(owner.getPrivate(), owner.getPublic());
			tree.setOwner(newI);								
			// use the other group name
			treeSequence = message.getTreeSequence();										
		}        	
       	else{// in the same group as sender of this message
       		tree.updatePublics(newTree);
       	}        	
       	tree.computeKeys(owner.getParent());
        	
       	expectedAction = TgdhMessage.NOSPECIAL;
       	printTree();
       	return true; 
    }            
*/        
    if( tree.count() == 1){// the new node who has joined at the group
       	if(message.getCause() != TgdhMessage.JOIN){          	
       		return true; }
          			        
        TreeInfo tinfo = new TreeInfo(treeinfo.getNodes(),
               	treeinfo.getOrder(), owner.getName(), tree.getKeyParams() );	            
        tree = new Tree(tinfo);
        LeafNode newOwner = tree.getOwner();
        newOwner.setKeys(owner.getPrivate(), owner.getPublic());
        newOwner.setSignKey(owner.getSignKey());
        newOwner.setVerifyKey(owner.getVerifyKey());
        tree.computeKeys(newOwner.getParent());
        
		expectedAction = TgdhMessage.NOSPECIAL;
		treeSequence   = message.getTreeSequence();
        printTree();
        return true;
   }            
        
   if(!treeinfo.toString().equals(tree.toString())) {
        System.err.println("WARN: the updated tree not the same as its own tree");
        return false;
   }

	tree.updatePublics(newTree);
	// if the updated tree doesn't have all publickeys, I am sponsor 
	// and I get new publickey updated.		
	if( isSponsor ){
		tree.computeKeys(tree.getOwner().getParent());
		if(newTree.getRoot().getPublic() == null) {
    		TgdhMessage tmsg = new UpdateMessage(
    				group,           message.getCause(),
    				treeSequence,    tree.treeInfo(), 
    				owner.getName(), owner.getSignKey());
	    	tmsg.sign();
	    	tmsg.init4verify(owner.getVerifyKey());
	    	outQueue.add(tmsg);
		}
	}
	
	Node root = tree.getRoot();
	Node left = root.getLeft();
	Node right = root.getRight();
	if(root.getPublic() != null || 
	  (left != null && left.getPublic() != null && right.getPublic() != null)) {
		isSponsor = false;
		if(tree.getRoot().getPrivate() == null)
			tree.computeKeys(tree.getOwner().getParent());
		expectedAction = TgdhMessage.NOSPECIAL;
		/* print tree and groupkey */
		printTree();
	}
	return true;		
   }
    
    private void process(ErrorMessage message)
    	throws TgdhException
    {
	    System.out.println(message.toString());
    }    
    
    
    //----------------------------------------------------------//
    
/*    private void log(String event) throws TgdhException{
        StringBuffer buffer = new StringBuffer();
        Date now = new Date();
        buffer.append(now.toGMTString());
        buffer.append(": ");
        buffer.append(event);
        buffer.append("\n");

        try {
            logger.write(buffer.toString().getBytes());
        } catch (IOException e) {
            throw new TgdhException(e.getCause());
        }
    }
*/
	/**
	 * Sends the request to join in the group
	 * @throws TgdhException
	 */
    public void joinGroup() throws TgdhException {
        joinGroup(1);
    }
    
    /**
     * Sends the request to join in the group at detail position
     * @param position the insert position
     * @throws TgdhException
     */
    public void joinGroup(int position) throws TgdhException {
    	if(expectedAction != TgdhMessage.NOSPECIAL) {
    		throw new TgdhException("expected an other action");
    	}
        if(tree.count() != 1){
            throw new TgdhException("can't join a group with more members");
        }
        TgdhMessage msg = new JoinMessage(
        		group,        	
        		tree.getOwner(), position);
        msg.sign();
        msg.init4verify(tree.getOwner().getVerifyKey());
        outQueue.add(msg);

		expectedAction = TgdhMessage.UPDATE;

        System.out.println("I join in the group at position " + position);
    }
    
	/**
	 * Sends the request to leave the group
	 * @throws TgdhException
	 */    
    public void leaveGroup() throws TgdhException {
        TgdhMessage msg = new LeaveMessage(group, tree.getOwner());
        msg.sign();
        msg.init4verify(tree.getOwner().getVerifyKey());
        outQueue.add(msg);
     }

	/**
	 * Sends the request to merge my key tree to another key tree with the same 
	 * group name
	 * @throws TgdhException
	 */    
    public void merge() throws TgdhException{
		if(expectedAction != TgdhMessage.NOSPECIAL) {
			throw new TgdhException("expected an other action");
		}

    	if(tree.getOwner() != tree.rightMost()){
    		System.out.println("only the rightmost member can merge a tree");
    		return;	
    	}    	
    	
    	System.out.println("I merge our group");
    	LeafNode owner = tree.getOwner();
    	tree.genOwnerKeyPair();
    	tree.computeKeys(owner.getParent());    	
    	treeSequence ++;
    	
        TgdhMessage msg = new MergeMessage(
        			group,           treeSequence,    tree.treeInfo(), 
        			owner.getName(), owner.getSignKey());
        			
        msg.sign();
        msg.init4verify(owner.getVerifyKey());        
        outQueue.add(msg);
        
        expectedAction = TgdhMessage.MERGE;
        printTree();
    }
    
    /**
     * Starts the thread. It has a loop which will be repeated if this thread
     * is not interrupted. It first deal with the messages in the inQueue, then
     * the messages in the outQueue, then receive message in the group from other
     * members.    
     */
    public void run() {
      TgdhMessage 	 	tmsg 		= null;
      MessageWithTry 	msgWT		= null;
      TreeInfo 		 	tinfo 		= null;
      Object 		 	obj			= null;
      TgdhSuspectEvent  sEvent		= null;
      LeafNode			signer      = null;
      boolean 			clearPartitionList = false;
      boolean			acceptMessage	   = false;
      
      while(!this.isInterrupted()){
          /* work the message in inQueue */
       	if(!inQueue.isEmpty()){
            msgWT  = (MessageWithTry) inQueue.remove(0);
			try {
				processMessage(msgWT);
			} catch (NotSerializableException e1) {
				e1.printStackTrace();
				this.interrupt();
			} catch (TgdhException e1) {
				e1.printStackTrace();
				this.interrupt();
			}
       	}
		try{
         	/* send the message */
        	if(!outQueue.isEmpty()){
             	tmsg = (TgdhMessage) outQueue.remove(0);
           	 	communicator.send(tmsg);				   
             	if(tmsg instanceof LeaveMessage) {
			   		communicator.close();
               		// Thread.yield();
               		this.interrupt();
			   		System.out.println("I leave the group");
			   		continue;
             	}
          	}
           	/* receive the message */
          	obj = communicator.receive();	           
          	if(obj == null){ 	continue;  }	 		 
		  	if(obj instanceof TgdhSuspectEvent){          	  
		    	sEvent = (TgdhSuspectEvent) obj;
		    	LeafNode exitNode = new LeafNode( TgdhUtil.getName(
			   			sEvent.getIpAddress(), sEvent.getPort()));
		    	if(tree.leafNode(exitNode.getName()) != null){   
		    		partitionList.add(exitNode);    
		    	}
		    	continue;
	     	}	
		  	// now the message is instance of TgdhMessage
		 	if(! verifyMsg((TgdhMessage)obj)){
		 		 continue; 
		 	} 
		   
         	if(obj instanceof MergeMessage || obj instanceof JoinMessage) {
			 	if(!partitionList.isEmpty()){
					msgWT = new MessageWithTry(new PartitionMessage(partitionList.removeNodes()), tryTimes);          	  	  			
					inQueue.add(msgWT);						
			 	}
          	}          	
          	else if(obj instanceof LeaveMessage && !partitionList.isEmpty()) {
          	  	LeaveMessage lmsg = (LeaveMessage) obj;
          	  	// add the member to the partition list
          	  	partitionList.add(tree.leafNode(lmsg.getLeave().getName()));
			  	msgWT = new MessageWithTry(
					new PartitionMessage(partitionList.removeNodes()), tryTimes);          	  	  			
			  	inQueue.add(msgWT);
			  	continue;   	            	  
          	}          
			 
		 	inQueue.add(new MessageWithTry((TgdhMessage) obj, tryTimes));
    	 } catch(TgdhException e){}
      } // mainloop
      
      System.out.println("exit main loop");
    }
    
    
 	
    private void printTree(){
        System.out.println(tree.toString());
        System.out.println("Sym. GroupKey (160 Bit):");
        System.out.println(TgdhUtil.toHex(tree.getSymmetricKey(160)) + "\n");
    }
    
    
	private boolean verifyMsg(TgdhMessage message) 
		throws TgdhException
	{	
		if(!message.getGroup().equals(group)) return false;
		
		LeafNode signer = null;
		
		if(message instanceof JoinMessage) {
			return message.verify();	
		}
		else if(message instanceof MergeMessage) {
			signer = tree.leafNode(message.getSignerName());
			if(signer != null){
				message.init4verify(signer.getVerifyKey());	
			}
			return message.verify();
		}
		
		else if(message instanceof UpdateMessage){
			UpdateMessage umsg = (UpdateMessage) message;
			if(umsg.getCause() == TgdhMessage.JOIN && tree.count() == 1)
			    //The update message for my join	
				return message.verify();
			else if(umsg.getCause() == TgdhMessage.MERGE){
				signer = tree.leafNode(message.getSignerName());
				if(signer != null){ // the signer is in my tree
					message.init4verify(signer.getVerifyKey()); 
				}
				return message.verify();
			}
		}
		
		signer = tree.leafNode(message.getSignerName());
		if(signer == null || signer.getVerifyKey() == null) 
			return false;
		message.init4verify(signer.getVerifyKey());
		return message.verify();		
	}
	
	/**
	 * Sets the group name
	 * @param groupname the group name
	 */
	public void setGroup(String groupname) {
		this.group = groupname;
	}

/**
 * This class implements a list to manages the members who leave the group 
 * caused by abnormally reason, e.g. network fault, terminate of the programm.
 */
static class PartitionList {
	private List members;
    	
	/**
	 * Constructs a new PartitionList
	 */    	
	public PartitionList(){
		members = new LinkedList();
	}    	
	/**
	 * Adds a member to the list
	 */
	public void add(LeafNode member){
		members.add(member);
	}    	
	/**
	 * Returns the members in the list.
	 * @return the members in the list.
	 */
	public LeafNode[] getMembers() {
		if(members.size() == 0){
			return null;
		}    		
		LeafNode[] tmp = new LeafNode[members.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = (LeafNode) members.get(i);
		}
		return tmp;    		    	
	} 
	/**
	 * Returns the members in the list and then clear the list.
	 * @return the members in the list.
	 */    	
	public LeafNode[] removeNodes() {
		if(members.size() == 0){
			return null;
		}    		
		LeafNode[] tmp = new LeafNode[members.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = (LeafNode) members.remove(0);
		}
		return tmp;    		    	
	}
	/**
	 * Indicates if the list is empty
	 * @return true is the list is empty, false if not.
	 */    	
	public boolean isEmpty(){
		return members.isEmpty();
	}   	
	/**
	 * Clears the list.
	 */
	public void clear() {
		members.clear();
	}  	
}
    
}
