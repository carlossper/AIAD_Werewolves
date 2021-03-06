package gui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import agents.*;
import utils.PlayerRole;

public class WerewolvesGUI {

	private JFrame frmWerewolves;
	private BufferedImage villagerImg = null;
	private BufferedImage werewolfImg = null;
	private BufferedImage villagerDeadImg = null;
	private BufferedImage werewolfDeadImg = null;
	private BufferedImage moonImg = null;
	private BufferedImage sunImg = null;
	private JPanel gridPanel = null;
	private JTextArea logTextArea = null;
	
	private boolean isDay=true;
	
	private static int numberPlayers;
	private static NumPlayersSelector numSelDialog;
	private Moderator modAgent;
	private ArrayList<Player> playerAgents = new ArrayList<Player>();
	private ArrayList<ArrayList<BackgroundPanel>> gridCells = new ArrayList<ArrayList<BackgroundPanel>>();
	private HashMap<String, BackgroundPanel> playersPanels = new HashMap<String, BackgroundPanel>();
	
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
					Thread.sleep(500);
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
		//frmWerewolves.setResizable(false);
		frmWerewolves.setBounds(100, 100, 800, 205);
		frmWerewolves.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frmWerewolves.getContentPane().setLayout(new GridLayout(1, 2, 1, 1));
		
		gridPanel = new JPanel();
		frmWerewolves.getContentPane().add(gridPanel);
		
		logTextArea = new JTextArea();
		logTextArea.setWrapStyleWord(true);
		logTextArea.setEditable(false);
		

		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		frmWerewolves.getContentPane().add(logScrollPane);
		
		try {
			villagerImg = ImageIO.read(new File("resources"+File.separator+"Villager.jpg"));
			werewolfImg = ImageIO.read(new File("resources"+File.separator+"Werewolf.jpg"));
			villagerDeadImg = ImageIO.read(new File("resources"+File.separator+"VillagerDEAD.jpg"));
			werewolfDeadImg = ImageIO.read(new File("resources"+File.separator+"WerewolfDEAD.jpg"));
			moonImg = ImageIO.read(new File("resources"+File.separator+"moon.jpg"));
			sunImg = ImageIO.read(new File("resources"+File.separator+"sun.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void initializeWithAgents() {
		gridPanel.setLayout(new GridLayout((numberPlayers/5)+1, 5, 5, 5));
		frmWerewolves.setBounds(100, 100, 800, 100+(numberPlayers*100/5)+((numberPlayers/5)+1)*5);
		for(int i=0; i<(numberPlayers/5)+1; i++) {
			ArrayList<BackgroundPanel> line = new ArrayList<BackgroundPanel>();
			for(int j=0; j <5; j++) {
				BackgroundPanel panel = new BackgroundPanel((Image)null);
				line.add(panel);
				gridPanel.add(panel);
			}
			gridCells.add(line);
		}

		gridCells.get(0).get(0).setImage(moonImg);
		gridCells.get(0).get(0).add(new JLabel("<html><font color='white' size='6'>Night</font></html>",SwingConstants.CENTER));
		
		//moderator
		gridCells.get(0).get(2).add(new JLabel("<html>"+modAgent.getAID().getLocalName()+"<br>"+modAgent.getModState()+"</html>"));
		modAgent.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(((State)e.getNewValue()).equals(State.DAY_VOTING) || ((State)e.getNewValue()).equals(State.WEREWOLVES_VOTING)) switchTurn();
				((JLabel)gridCells.get(0).get(2).getComponent(0)).setText(e.getNewValue().toString());
				((JLabel)gridCells.get(0).get(2).getComponent(0)).repaint();
			}
		});
		
		JButton continueModBtn = new JButton("Continue");
		continueModBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				continueMod();
			}
		});
		gridCells.get(0).get(3).add(continueModBtn);
		//player
		for(int i=0; i<(numberPlayers/5); i++) {
			for(int j=0; j<5; j++) {
				Player player = playerAgents.get(i*5+j);
				if(player.getPlayerRole().equals(PlayerRole.Villager) && villagerImg!=null) gridCells.get(1+i).get(j).setImage(villagerImg);
				else if(player.getPlayerRole().equals(PlayerRole.Werewolf) && werewolfImg!=null) gridCells.get(1+i).get(j).setImage(werewolfImg);
				gridCells.get(1+i).get(j).add(new JLabel("<html><font color='white' size='5'>"+player.getAID().getLocalName()+"<br>"+player.getPlayerRole()+"<br>"+player.getPlayerState()+"</font></html>"));
				playersPanels.put(player.getAID().getLocalName(), gridCells.get(1+i).get(j));
				player.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						BackgroundPanel playerPanel = playersPanels.get(e.getPropertyName());
						((JLabel)playerPanel.getComponent(0)).setText("<html><font color='white' size='5'>"+player.getAID().getLocalName()+"<br>"+player.getPlayerRole()+"<br>"+e.getNewValue().toString()+"</font></html>");
						if(((State)e.getNewValue()).equals(State.DEAD) && player.getPlayerRole().equals(PlayerRole.Villager)) playerPanel.setImage(villagerDeadImg);
						else if(((State)e.getNewValue()).equals(State.DEAD) && player.getPlayerRole().equals(PlayerRole.Werewolf)) playerPanel.setImage(werewolfDeadImg);
						playerPanel.repaint();
					}
				});
			}
		}
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
			modAgent.setGUI(this);
			cc.acceptNewAgent("mod", modAgent).start();
			
			//players
			while(modAgent.getModState()==State.REGISTER){
				System.out.print(""); //DO NOT DELETE THIS LINE!!!
			}
			for(int i=0; i<numberPlayers; i++) {
				Player playerAgent = new Player();
				playerAgent.setGUI(this);
				cc.acceptNewAgent("play"+i, playerAgent).start();
				playerAgents.add(playerAgent);
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void switchTurn() {
		isDay = !isDay;
		if(isDay){
			gridCells.get(0).get(0).setImage(sunImg);
			((JLabel)gridCells.get(0).get(0).getComponent(0)).setText("<html><font color='white' size='6'>Day</font></html>");
			
		}
		else{
			gridCells.get(0).get(0).setImage(moonImg);
			((JLabel)gridCells.get(0).get(0).getComponent(0)).setText("<html><font color='white' size='6'>Night</font></html>");
		}
		((JLabel)gridCells.get(0).get(0).getComponent(0)).repaint();
	}

	private void continueMod() {
		synchronized(modAgent) {
			modAgent.notify();
		}
	}
	
	public void log(String text) {
		logTextArea.append("\n"+text+"\n");
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}
	
}
