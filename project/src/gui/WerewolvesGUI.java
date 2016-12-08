package gui;

import java.awt.EventQueue;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import agents.*;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WerewolvesGUI {

	private JFrame frmWerewolves;
	private static int numberPlayers;
	private static NumPlayersSelector numSelDialog;
	private Moderator modAgent;
	private ArrayList<Player> playerAgents = new ArrayList<Player>();
	private ArrayList<ArrayList<JPanel>> gridCells = new ArrayList<ArrayList<JPanel>>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WerewolvesGUI window = new WerewolvesGUI();
					window.frmWerewolves.setEnabled(false);
					
					window.frmWerewolves.setVisible(true);
					numSelDialog.setVisible(true);
					numberPlayers=numSelDialog.getValue();
					
					window.createAgents();
					Thread.sleep(250);
					window.initializeWithAgents();
					
					window.frmWerewolves.setEnabled(true);
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
		frmWerewolves = new JFrame();
		frmWerewolves.setTitle("Werewolves of Miller's Hollow");
		frmWerewolves.setResizable(false);
		frmWerewolves.setBounds(100, 100, 520, 205);
		frmWerewolves.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initializeWithAgents() {
		frmWerewolves.setBounds(100, 100, 520, 100+(numberPlayers*100/5)+((numberPlayers/5)+1)*5);
		frmWerewolves.getContentPane().setLayout(new GridLayout((numberPlayers/5)+1, 5, 5, 5));
		for(int i=0; i<(numberPlayers/5)+1; i++) {
			ArrayList<JPanel> line = new ArrayList<JPanel>();
			for(int j=0; j <5; j++) {
				JPanel panel = new JPanel();
				line.add(panel);
				frmWerewolves.getContentPane().add(panel);
			}
			gridCells.add(line);
		}

		//turn (day/night)
		gridCells.get(0).get(1).add(new JLabel("Night"));
		
		//moderator
		gridCells.get(0).get(2).add(new JLabel("<html>"+modAgent.getAID().getLocalName()+"<br>"+modAgent.getModState()+"</html>"));
		frmWerewolves.addPropertyChangeListener("state", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				((JLabel)gridCells.get(0).get(2).getComponent(0)).setText(e.getNewValue().toString());
				((JLabel)gridCells.get(0).get(2).getComponent(0)).repaint();
			}
		});
		for(int i=0; i<(numberPlayers/5); i++) {
			for(int j=0; j<5; j++) {
				gridCells.get(1+i).get(j).add(new JLabel("<html>"+playerAgents.get(i*5+j).getAID().getLocalName()+"-"+playerAgents.get(i*5+j).getPlayerRole()+"<br>"+playerAgents.get(i*5+j).getPlayerState()+"</html>"));
			}
		}
		//player1 state
		//player2 state
		//playerN state
	}

	private void createAgents() {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile p = new ProfileImpl();
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createMainContainer(p);
		
		try {			
			//mod
			modAgent = new agents.Moderator(numberPlayers);
			cc.acceptNewAgent("mod", modAgent).start();
			
			//players
			while(modAgent.getModState()==State.REGISTER){
				System.out.print(""); //DO NOT DELETE THIS LINE!!!
			}
			for(int i=0; i<numberPlayers; i++) {
				Player playerAgent = new Player();
				cc.acceptNewAgent("play"+i, playerAgent).start();
				playerAgents.add(playerAgent);
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
