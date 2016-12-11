package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import users.Opponent;
import utils.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Random;

import gui.WerewolvesGUI;


/**
 * Created by ruben on 10/11/2016.
 */
public class Player extends Agent {
    protected ServiceDescription serviceDescription;
    protected State state = State.CONNECTING;
    private AID moderatorName;
    private PlayerRole role;
    private KnowledgeBase knowledgeBase;
    
    // Random generator
    private Random randomGenerator = new Random();
    
    //gui
    private WerewolvesGUI gui= null;
    //property change events
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Player() {
        this.serviceDescription = new ServiceDescription();
        this.knowledgeBase = new KnowledgeBase();
    }

    public void setGUI(WerewolvesGUI wwGUI) {
    	gui=wwGUI;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public State getPlayerState() {
    	return state;
    }
    private void setPlayerState(State newS) {
    	State oldS = state;
    	state = newS;
    	this.pcs.firePropertyChange(getAID().getLocalName(), oldS, state);
    }
    
    public PlayerRole getPlayerRole() {
    	return role;
    }
    
    protected void setup() {
        this.serviceConfig();
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                update();
            }
        });

        receiveMessage();

    }
    private void update() {

        switch (state) {
            case CONNECTING:
                connect();
                break;
            case CONNECTINGSENT: break;
            case WAITING: break;
            case WAKE:
                System.out.println(this.getLocalName() + " o meu role e " + role);
                this.gui.log(this.getLocalName() + " o meu role e " + role);
                setPlayerState(State.ALIVE);
                break;
        }

    }

    private void receiveMessage() {

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                
                if(msg != null)
                {
                    switch(msg.getPerformative())
                    {
                        case ACLMessage.INFORM:

                            if(msg.getContent().startsWith("Eliminacao"))
                            {
                                String[] mensagens = msg.getContent().split(" ");
                                knowledgeBase.deleteOpponent(mensagens[1]);
                                Utils.sendMessage("Eliminaçao confirmada", moderatorName, ACLMessage.INFORM, myAgent, null);
                            }

                            if(msg.getContent().equals("comecar jogo"))
                            {
                                Utils.sendMessage("Sim, estou pronto.",moderatorName, ACLMessage.INFORM,myAgent, null);
                            }                            
                            if(msg.getContent().startsWith(PlayerRole.Werewolf.name()))
                            {
                                role = PlayerRole.Werewolf;
                                String[] mensagems = msg.getContent().split(" ");
                                knowledgeBase.saveopponents(mensagems, myAgent.getLocalName());
                                setPlayerState(State.WAKE);
                                Utils.sendMessage("pronto",moderatorName,ACLMessage.INFORM, myAgent,null);
                                break;
                            }
                            else if(msg.getContent().startsWith(PlayerRole.Villager.name()))
                            {
                                role = PlayerRole.Villager;
                                String[] mensagems = msg.getContent().split(" ");
                                knowledgeBase.saveopponents(mensagems, myAgent.getLocalName());
                                setPlayerState(State.WAKE);
                                Utils.sendMessage("pronto",moderatorName,ACLMessage.INFORM, myAgent,null);
                                break;
                            }
                            if(msg.getContent().equals("Eliminado")) {
                            	((Player)myAgent).setPlayerState(State.DEAD);
                            	doDelete();
                            }
                            break;
                        case ACLMessage.REQUEST:
                            if(msg.getContent().equals("Votacao Werewolves"))
                            {
                            	// Voting
                            	//System.out.println("Propose vote for werewolves received!");
                            	
                            	ArrayList<Opponent> ops = knowledgeBase.getOpponents();
                            	
                            	int rand = randomGenerator.nextInt(ops.size());
                            	String vote = "Vote "+ops.get(rand).getName();
                            	
                            	System.out.println("Vote sent by "+ myAgent.getLocalName() +"! => "+vote);
                            	((Player)this.myAgent).gui.log("Vote sent by "+ myAgent.getLocalName() +"! => "+vote);
                            	
                            	Utils.sendMessage(vote, moderatorName, ACLMessage.INFORM, this.myAgent, null);
                            	break;
                            }
                            else if(msg.getContent().equals("Votacao Geral"));
                            {
                            	// Voting
                            	//System.out.println("Propose vote general received!");
                            	
                            	ArrayList<Opponent> ops = knowledgeBase.getOpponents();
                            	
                            	int rand = randomGenerator.nextInt(ops.size());
                            	String vote = "Vote "+ops.get(rand).getName();

                                System.out.println("Vote sent by "+ myAgent.getLocalName() +"! => "+vote);
                                ((Player)this.myAgent).gui.log("Vote sent by "+ myAgent.getLocalName() +"! => "+vote);
                            	Utils.sendMessage(vote, moderatorName, ACLMessage.INFORM, this.myAgent, null);
                            	
                            }
                        	break;
                        case ACLMessage.PROPOSE:
                            if(msg.getContent().equals("Quer jogar Werewolf?"))
                            {
                              //  System.out.println("Proposta para jogar recebida");
                                acceptConnected();
                            }
                            break;
                    }
                }
                else
                    block();
            }
        });
    }

    private void serviceConfig() {
        this.serviceDescription.setType("moderator");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(serviceDescription);
        try {
            DFAgentDescription[] resultado = DFService.search(this, dfd);
            this.moderatorName = resultado[0].getName();
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        //System.out.println("CONNECT: " + this.getLocalName());
        Utils.sendMessage("Estabelecer Ligacao",moderatorName, ACLMessage.INFORM,this, null);
        setPlayerState(State.CONNECTINGSENT);


    }

    private void acceptConnected()
    {
        //System.out.println("Enviou mensagem para aceitar jogar.");
        Utils.sendMessage("Aceito Ligacao",moderatorName,ACLMessage.ACCEPT_PROPOSAL,this, null);
        setPlayerState(State.WAITING);
    }

    @Override
    protected void takeDown() {
        System.out.println(this.getLocalName() +" foi terminado. Role: "+ role);
        this.gui.log(this.getLocalName() +" foi terminado. Role: "+ role);

    }
}