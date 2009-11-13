package tgdh.comm;

import tgdh.tree.LeafNode;

/**
 * 
 * This class implements the LeaveMessage in tgdh-protocol.
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class LeaveMessage extends TgdhMessage {
    private LeafNode leave;

	/**
	 * Constructs a new LeaveMessage with detail group and node who will 
	 * leave the group.
	 * @param group  the group name 
	 * @param leave  the node who will leave the group
	 */
    public LeaveMessage(
    		String group, 
    		LeafNode leave) 
    {
    	super(group, leave.getName(), leave.getSignKey());
        this.leave = leave;
    }
    
    /**
     * Returns the node who will the group
     * @return the node who will the group
     */
    public LeafNode getLeave() {
        return leave;
    }


	/**
	 * Returns a bytearray of the message, will be used for the signature. 
	 * All important data, here group name, signer name, node who will leave 
	 * the group, will be included in order to prevent the modification of this data.
	 * @return  a bytearray representation of the message, will used for the 
	 *          signature. 
	 */
    protected byte[] toByteArray() {
		byte[] a = super.toByteArray();
    	byte[] b = leave.toByteArray();
    	byte[] c = new byte[a.length + b.length];
    	System.arraycopy(a, 0, c, 0, a.length);
    	System.arraycopy(b, 0, c, a.length, b.length);    	
        return c;        
    }
    
    
    public String toString() {
        return "Node to leave: " + leave.getName();
    }
}
