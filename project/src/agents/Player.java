package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;


/**
 * Created by ruben on 10/11/2016.
 */
public class Player extends Agent {
    protected ServiceDescription serviceDescription;
    protected State state = State.CONNECTING;
    private AID moderatorName;
    private PlayerRole role;
    private KnowledgeBase knowledgeBase;

    //property change events
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Player() {
        this.serviceDescription = new ServiceDescription();
        this.knowledgeBase = new KnowledgeBase();
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
    	this.pcs.firePropertyChange("playerState@"+getAID().getLocalName(), oldS, state);
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
                System.out.println(this.getLocalName() + " o meu role é " + role);
                setPlayerState(State.GAMEON);
                break;
            case GAMEON:
            	//switch role...
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
                            if(msg.getContent().startsWith("comecar jogo"))
                            {
                                String[] mensagems = msg.getContent().split(" ");
                                knowledgeBase.saveopponents(mensagems, myAgent.getLocalName());

                                Utils.sendMessage("Sim, estou pronto.",moderatorName, ACLMessage.INFORM,myAgent, null);

                            }                            
                            if(msg.getContent().equals(PlayerRole.Werewolf.name()))
                            {
                                role = PlayerRole.Werewolf;
                                setPlayerState(State.WAKE);
                                break;
                            }
                            else if(msg.getContent().equals(PlayerRole.Villager.name()))
                            {
                                role = PlayerRole.Villager;
                                setPlayerState(State.WAKE);
                                break;
                            }
                            if(msg.getContent().equals("Eliminado"))
                                doDelete();
                            break;
                        case ACLMessage.REQUEST:
                        	
                            if(msg.getContent().equals("Votacao Werewolves"))
                            {
                            	System.out.println("Propose vote for werewolves received!");
                            	// Handling function for werewolves voting
                            	break;
                            }
                            else if(msg.getContent().equals("Votacao Geral"));
                            {
                            	System.out.println("Propose vote general received!");
                            	// Handling function for general voting 
                            	
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
        Utils.sendMessage("Aceito Ligacao",moderatorName,ACLMessage.ACCEPT_PROPOSAL,this, null
        );
        setPlayerState(State.WAITING);
    }

    @Override
    protected void takeDown() {
        System.out.println(this.getLocalName() +" foi terminado.");

    }
}