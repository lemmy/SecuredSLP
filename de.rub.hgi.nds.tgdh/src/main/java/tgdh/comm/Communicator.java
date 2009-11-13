/*
 * Created on 2004-9-18
 *
 */
package tgdh.comm;

import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.SuspectEvent;
import org.jgroups.TimeoutException;
import org.jgroups.View;
import org.jgroups.stack.IpAddress;

import tgdh.TgdhException;

/**
 * This class implements a proxy who sends and receives the multicast message
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class Communicator extends Thread{
    private Channel   channel          = null;
	private boolean   printCommInfo    = false;
	
	/**
	 * Constructor, builds a multicast proxy who controls the multicast communication
	 * with given properties. Only the members who have the same multicast
	 * address and port can communicate.
	 * @param properties Properties required by JGroup
	 * @throws TgdhException
	 */
    public Communicator(String properties) 
    		throws TgdhException 
    {
        init(properties);
    }

	/**
	 * Constructor, builds a multicast proxy who controls the multicast communication
	 * with given multicast address and port. Only the members who have the same multicast
	 * address and port can communicate.
	 * @param mcastAddr  Multicast address
	 * @param mcastPort  Multicast port
	 * @throws TgdhException
	 */    
    public Communicator(String mcastAddr, int mcastPort) 
    	throws TgdhException 
    {
	  StringBuffer s = new StringBuffer();
      s.append("UDP(mcast_addr=");
		s.append(mcastAddr);
		s.append(";mcast_port=");
		s.append(mcastPort);
		s.append(";ip_ttl=32;");
		s.append("mcast_send_buf_size=65536;mcast_recv_buf_size=65536):");
	  //s.append("PIGGYBACK(max_wait_time=100;max_size=32000):");
	  s.append("PING(timeout=2000;num_initial_members=3):");
	  s.append("MERGE2(min_interval=5000;max_interval=10000):");
	  s.append("FD_SOCK:");
	  s.append("VERIFY_SUSPECT(timeout=1500):");
	  s.append("pbcast.NAKACK(max_xmit_size=8096;gc_lag=50;retransmit_timeout=600,1200,2400,4800):");
	  s.append("UNICAST(timeout=600,1200,2400,4800):");
	  s.append("pbcast.STABLE(desired_avg_gossip=20000):");
	  s.append("FRAG(frag_size=1024;down_thread=false;up_thread=false):");
	  //s.append("CAUSAL:");
	  s.append("pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;");
	  s.append("shun=false;print_local_addr=true)");
        
      String props = s.toString();
        
	  init(props);    	
    }
    
    /**
     * Initializes the Proxy, it will automatic reconnect and receive
     * only the message and event of other members in the same group
     * @param properties Properties required by JGroup
     */
    public void init(String properties)
    	throws TgdhException
    {
        try {
            channel = new JChannel(properties);
            channel.setOpt(Channel.AUTO_RECONNECT, 	Boolean.TRUE);
            channel.setOpt(Channel.LOCAL, 		Boolean.FALSE);
            channel.connect("TGDH.nds.ruhr-uni-bochum.de");
        } catch (ChannelException e) {
            throw new TgdhException(e);
        }
	}
    
    /**
     * Broadcasts the TgdhMessage <t>message</t>
     * @param message Message to be sent
     */
    public void send(TgdhMessage message)
    	throws 	TgdhException
    {
        try {
            send(null, null, message);
        } catch (ChannelException e) {
            throw new TgdhException(e);
        }
    }
    
    /**
     * Sends a message to destination address <t>dest</t> with source address <t>src</t>
     * @param dest   destination address of the message
     * @param src    source address of the message
     * @param message      message to be sent 
     */
    public void send(Address dest, Address src, TgdhMessage message)
    	throws ChannelException
    {
        Message msg = new Message (dest, src, message);

        if(printCommInfo && msg != null){
            System.out.println("Send:" + msg.toString());
        }

        channel.send(msg);
    }
    
    /**
     * returns TgdhMessage if it is an instance of TgdhMessage and the 
     * message is from others, else null, the default timeout is 2000 ms
     * @return Received message or event
     * @throws TgdhException
     */
    public Object receive() 
    	throws TgdhException 
    { 		
        return receive(2000); 
    }   	
    
    /**
     * Returns an Object if the received message is an instance of TgdhMessage or 
     * tgdhSuspendEvent, else null.
     * @param timeout Timeout
     * @return an Object if the received message is an instance of TgdhMessage or 
     * tgdhSuspendEvent, else null
     */
    public Object receive(long timeout) 
		throws TgdhException 
	{ 		
        try{
			Object obj = channel.receive(timeout);
	        if(printCommInfo && obj != null){
	            System.out.println("Receive:" + obj.toString());
	        }	        
	        
	        if(obj instanceof Message){
	        	Message msg = (Message) obj;
	        	if(msg.getObject() instanceof TgdhMessage) {
	        		return msg.getObject();	
	        	}	        		
	        }
	        
	        else if(obj instanceof SuspectEvent){
	        	obj = ((SuspectEvent) obj).getMember();
	        	if(obj instanceof IpAddress){
	        		IpAddress ip = (IpAddress) obj;
	        		return new TgdhSuspectEvent(
	        			ip.getIpAddress(), ip.getPort()); 
	        	}
	        }
	        
	        return null;
        } catch (ChannelException e) {
            throw new TgdhException(e);
        } catch (TimeoutException e) {
            throw new TgdhException(e);
        }
    }   	
    
    /**
     * Disconnects the channel from the current group (if connected), leaving 
     * the group. It is a null operation if not connected. It is a null 
     * operation if the channel is closed.
     */
    public void disconnect() {
        if(channel.isConnected()){
            channel.disconnect();
        }
    }
    
	/**
	 * Destroys the channel and its associated resources (e.g. the protocol 
	 * stack). After a channel has been closed, invoking methods on it throws 
	 * the ChannelClosed exception (or results in a null operation). It is a 
	 * null operation if the channel is already closed.
	 * 
	 * If the channel is connected to a group, disconnec() will be called 
	 * first.
	 */
    public void close() {
        if(channel.isOpen()){
            channel.close();
        }
    }
     
    /**
     * Returns the channel's own address. The result of calling this method 
     * on an unconnected channel is implementation defined (may return null). 
     * Calling this method on a closed channel returns null.
     * @return The channel's address. Generated by the underlying transport, 
     *         and opaque. Addresses can be used as destination in the Send 
     *         operation. 
     */  
    public Address getLocalAdresse(){
        return channel.getLocalAddress();
    }
 
}
