package tgdh;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.Security;
import java.security.interfaces.DSAParams;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jgroups.Address;
import org.jgroups.stack.IpAddress;

import tgdh.GroupConf;
import tgdh.TgdhUtil;
import tgdh.Worker;
import tgdh.comm.Communicator;
import tgdh.tree.LeafNode;
import tgdh.tree.Tree;

/**
 * A swing-based user interface to simulate the tgdh-protocol.<br>
 * <IMG alt="TGDH Simulator" src="../images/tgdh-sim.jpg" width=401 height=116 border=0>
 * <br>1. <i>Button Merge</i>: merge this tree to another tree with the same group name.<br>
 * 2.1 <i>Button "add tmp to group name"</i>: change group name to "tmp" + group name, 
 * after this action the text will be changed to "remove tmp from group name". <br>
 * 2.2 <i>Button "remove tmp from group name"</i>: remove the leading "tmp" from the group name.
 * after this action the text will be changed to "add tmp to group name".<br>
 * 3. <i>Button at</i>: join into the group at the in the left textfield 
 * given position.<br>
 * 4. <i>Butoon join</i>: join into the group.<br>
 * 5. <i>Button leave</i>: leave the group.<br>
 * 6. <i>Button exit</i>: exit the programm.       
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class Demo extends JFrame 
	implements ActionListener, Runnable
{
	private String		group;
	private Worker 		worker;		 
	private JButton     renameGroupButton;      		
	private JLabel  	logoLabel;
	private JButton		mergeButton;
	private JLabel	    joinPosLabel;
	private JTextField 	joinPosField;
	private JButton    	joinAtButton;
	private JButton 	joinButton;
	private JButton 	leaveButton;
	private JButton    	exitButton;
	

	public Demo(
			String  	 groupname,
			DSAParams    params,		
			Communicator communicator) 
	{
		super("TGDH Simulator (unconnected)");
		this.group 		= groupname;
		
		// Construct GUI components (before session)
		
		renameGroupButton = new JButton("add tmp to group name");
		renameGroupButton .addActionListener(this);
		logoLabel = new JLabel("Lehrstuhl NDS");
		mergeButton   	= new JButton("Merge");
		mergeButton .addActionListener(this);
		JPanel nPanel 	= new JPanel();
		nPanel.setLayout(new BorderLayout());
		nPanel.add(mergeButton,     BorderLayout.WEST);
		nPanel.add(renameGroupButton, BorderLayout.CENTER);
		nPanel.add(logoLabel,       BorderLayout.EAST);
		
		joinPosLabel    = new JLabel(" Position ");
		joinPosField 	= new JTextField();
		joinAtButton  	= new JButton("Join at");
		joinAtButton.addActionListener(this);
		JPanel cPanel 	= new JPanel();
		cPanel.setLayout(new BorderLayout());
		cPanel.add(joinPosLabel,  	BorderLayout.WEST);
		cPanel.add(joinPosField,  	BorderLayout.CENTER);
		cPanel.add(joinAtButton,    BorderLayout.EAST);

		joinButton 	    = new JButton("Join");
		leaveButton     = new JButton("Leave");
		exitButton      = new JButton("Exit");		
		joinButton  .addActionListener(this);
		leaveButton .addActionListener(this);		
		exitButton  .addActionListener(this);
		JPanel sPanel 	= new JPanel();
		sPanel.setLayout(new BorderLayout());
		sPanel.add(joinButton,  	BorderLayout.WEST);
		sPanel.add(leaveButton,  	BorderLayout.CENTER);
		sPanel.add(exitButton,    	BorderLayout.EAST);		

		getContentPane().add(nPanel, 	BorderLayout.NORTH);
		getContentPane().add(cPanel,  	BorderLayout.CENTER);
		getContentPane().add(sPanel, 	BorderLayout.SOUTH);
	
		//------------------------------------------------------------          
		try{
			Address localaddress = communicator.getLocalAdresse();
			LeafNode owner = new LeafNode( TgdhUtil.getName((IpAddress)localaddress));
			Tree aTree = new Tree(owner);
			aTree.setKeyParams(params);
			aTree.genOwnerKeyPair();		    
			owner.setSignKey(owner.getPrivate());
			owner.setVerifyKey(owner.getPublic());
			
			worker = new Worker(group, aTree, communicator, 50);			
			setSize(400, 118);            
			addWindowListener(new WindowAdapter(){
				public void windowClosed(WindowEvent e){System.exit(0); } } );  
				          
			show();            
			setTitle(owner.getName() + "@" + group);		    
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
			
	}
	
	
	/** 
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e) {
		try{
			if(e.getSource().equals(renameGroupButton)){
				if("add tmp to group name".equals(renameGroupButton.getText())) {
					worker.setGroup("tmp" + worker.getGroup());
					renameGroupButton.setText("remove tmp from group name");
				}
				else {
					worker.setGroup(worker.getGroup().substring(3));
					renameGroupButton.setText("add tmp to group name");
				}				
				
			}
			else if(e.getSource().equals(mergeButton)){
				worker.merge();
			}
			else if(e.getSource().equals(joinAtButton)){
				String s = joinPosField.getText();				
				int position = Integer.parseInt(s);
				if(position > 1) {
					worker.joinGroup(position);
				}
				else{
					showErrMsg("Position must be positiv");
				}
				joinPosField.setText("");
			}			
			else if(e.getSource().equals(joinButton)){
				worker.joinGroup();
			}
			else if(e.getSource().equals(leaveButton)){
				worker.leaveGroup();				
			}
			else {
				//worker.getCommunicator().disconnect();
				//worker.getCommunicator().close();
				worker.interrupt();
				System.exit(0);				
			}
		} 
		catch(Throwable ex){
			showErrMsg(ex.getMessage());
		}			
	}
	
	private void showErrMsg(String message) {
		JOptionPane.showMessageDialog(
				this, 
				message, 
				"Error", 
				JOptionPane.ERROR_MESSAGE);		
	}

	/**
	 * Usage: java tgdh.demo.TGDHSimulator configFile
	 */
	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Usage: java tgdh.demo.TGDHSimulator configFile");
			System.exit(-2);
		}

		Security.addProvider(new BouncyCastleProvider());
        
		try {
			GroupConf aGroupConf = new GroupConf(args[0]);
			String group = aGroupConf.getGroupname();
			DSAParams params = aGroupConf.getParams();
			String mcast_address = aGroupConf.getMcast_address();
			int mcast_port = aGroupConf.getMcast_port();
			Communicator aCommunicator = new Communicator(mcast_address, mcast_port);	
			Demo simulator = new Demo(group, params, aCommunicator);
			simulator.run();			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	/**
	 * Starts the thread. It has a loop which will be always repeated every
	 * 2000 ms. Tt sets the title of the window if the group name was changed.
	 */   
	public void run() {
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			
			if(!group.equals(worker.getGroup())){
				group = worker.getGroup();
				setTitle(worker.getTree().getOwner().getName() + "@" + group);
			}
		}		
	}   
    
    /**
     * Returns the group name
     * @return the group name
     */
	public String getGroup() {
		return group;
	}

	/**
	 * Returns the worker
	 * @return the worker
	 */
	public Worker getWorker() {
		return worker;
	}
	
}
