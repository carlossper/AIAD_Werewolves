package gui;

import java.awt.EventQueue;
import java.awt.Label;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import users.User;
import agents.*;
import java.awt.BorderLayout;

public class WerewolvesGUI {

	private JFrame frame;
	private static int numberPlayers;
	private static NumPlayersSelector numSelDialog;
	private Agent modAgent;
	//private ArrayList<AID> playerAgents = new ArrayList<AID>();
	
	JLabel label;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WerewolvesGUI window = new WerewolvesGUI();
					//window.frame.setEnabled(false);
					window.frame.setVisible(true);
					numSelDialog.setVisible(true);
					numberPlayers=numSelDialog.getValue();
					window.createAgents();
					
					ConcurrentHashMap<AID,User> users = ((Moderator)window.modAgent).getUsers();
					
					String text="";
					for(User user : users.values()) {
						text+=user.getName().getLocalName()+": "+user.getRole().name()+"\n";
					}
					window.label.setText(text);
					//window.frame.setEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WerewolvesGUI() {
		initialize();

		numSelDialog = new NumPlayersSelector();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		label = new JLabel("lol");
		frame.getContentPane().add(label, BorderLayout.CENTER);
	}

	private void createAgents() {
		
		modAgent = new agents.Moderator(numberPlayers);
		
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile p = new ProfileImpl();
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createMainContainer(p);
		
		try {
			//gui
			cc.createNewAgent("gui_watch", "jade.tools.rma.rma", null).start();
			
			//mod
			cc.acceptNewAgent("mod", modAgent).start();
			//cc.createNewAgent("mod", "agents.Moderator", new Object[] {""+numberPlayers}).start();

			//players
			Thread.sleep(500);
			for(int i=0; i<numberPlayers; i++) {
				cc.createNewAgent("play"+i, "agents.Player", null).start();
			}
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
