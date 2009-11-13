package tgdh.comm;

import java.net.InetAddress;
import java.util.Date;

/**
 * This class implements the suspect event in tgd-protocol. When a network fault 
 * happens, the program exits, etc., the remaining members receive a suspect event. 
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhSuspectEvent {
	private InetAddress ipAddress;
	private int port;
	private Date detectTime;
	
	/**
	 * Constructs a new TgdhSuspectEvent with detail IP address, port, and 
	 * the detection time.
	 * @param ipAddress  the IP address of the member who leaves the group
	 * @param port       the port of the member who leaves the group
	 * @param detectTime the detection time
	 */
	public TgdhSuspectEvent(InetAddress ipAddress, 
		int port, Date detectTime) 
	{
		this.ipAddress = ipAddress;
		this.port = port;
		
		if(detectTime == null){
			this.detectTime = new Date();
		}
		else{
			this.detectTime = detectTime;	
		}
	}
	

	/**
	 * Constructs a new TgdhSuspectEvent with detail IP address and port. 
	 * @param ipAddress  the IP address of the member who leaves the group
	 * @param port       the port of the member who leaves the group
	 */
	public TgdhSuspectEvent(InetAddress ipAddress, int port) 
	{
		this(ipAddress, port, new Date());
	}
	
	/**
	 * Returns the detection time.
	 * @return the detection time.
	 */
	public Date getDetectTime() {
		return detectTime;
	}

	/**
	 * Returns the the IP address of the member who leaves the group.
	 * @return the the IP address of the member who leaves the group.
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/**
	 * Returns the the port of the member who leaves the group.
	 * @return the the port of the member who leaves the group.
	 */
	public int getPort() {
		return port;
	}

}
