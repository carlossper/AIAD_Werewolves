package agents;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import users.User;
import utils.*;

/**
 * Created by ruben on 10/11/2016.
 */


public class Moderator extends Agent{
    private int numberPlayers = -1;

    private State state = State.REGISTER;
    private ConcurrentHashMap<AID,User> users;
    private Random randomGenerator = new Random();
    
    //property change events
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public Moderator() {
		users = new ConcurrentHashMap<AID,User>();
    }
    
    public Moderator(int numPlayers) {
    	numberPlayers=numPlayers;
		users = new ConcurrentHashMap<AID,User>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public State getModState() {
    	return state;
    }
    private void setModState(State newS) {
    	State oldS=state;
    	state = newS;
    	this.pcs.firePropertyChange("modState", oldS, state);
    }
    
    public User getUser(AID key) {
    	return users.get(key);
    }
    
    public ConcurrentHashMap<AID,User> getUsers() {
    	return users;
    }
    
    @Override
    protected void setup() {

        Object[] args = getArguments();
        if(args!=null) this.numberPlayers = Integer.parseInt((String)args[0]);
        System.out.println("Venham jogar Werewolves of Miller's Hollow!!!! São precisos " + this.numberPlayers + " jogadores!" );
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                update();
            }
        });
        receiveMessages();
    }

    private void receiveMessages() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if(msg != null) {
                    switch (msg.getPerformative())
                    {
                        case ACLMessage.INFORM:
                            if(msg.getContent().equals("Estabelecer Ligacao"))
                            {
                                //System.out.println("Pedido de ligacao recebido.");
                                Utils.sendMessage("Quer jogar Werewolf?",msg.getSender(),ACLMessage.PROPOSE,myAgent, null);
                            }

                            if(msg.getContent().equals("Sim, estou pronto."))
                            {
                               users.get(msg.getSender()).setAlive(true);
                               numberPlayers++;
                            }
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            if(state == State.STARTING) {
                                if (msg.getContent().equals("Aceito Ligacao")) ;
                                {
                                	//System.out.println("resposta de aceitação recebido");
                                    users.put(msg.getSender(), new User(msg.getSender()));
                                    System.out.println("Jogador " + msg.getSender().getLocalName() + " conectado.");


                                    if (users.size() == numberPlayers)
                                    {
                                        System.out.println("ALLCONNECTED");
                                        setModState(State.ALLCONNECTED);
                                        
                                        sendMessageToAllPlayers("comecar jogo", null, ACLMessage.INFORM);
                                        numberPlayers = 0;
                                    }
                                }
                            }
                            break;
                    }
                }
                else block();
            }
        });
    }

    private void update() {
        switch (state)
        {
            case REGISTER: 
            	this.serviceConfig();
                break;
            case STARTING:
            	break;
            case ALLCONNECTED:
                if(numberPlayers == users.size())
                {
                	generatePlayerRoles();
                	// Check if this is right
                	setModState(State.WEREWOLVES_VOTING);
                    
                }
                break;
            case WEREWOLVES_VOTING:
            	for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet())
            	{	
            		if(entry.getValue().getRole().equals(PlayerRole.Werewolf))
            		{
            			// Sends message to werewolves
            			AID dst = entry.getValue().getName();
            			Utils.sendMessage("Votacao Werewolves", dst, ACLMessage.REQUEST, this, null);
            		}
                }
            	// Change to proper state
            	setModState(State.DAY_VOTING);
            	break;
            case DAY_VOTING:
            	System.out.println();
            	this.sendMessageToAllPlayers("Votacao Geral", null, ACLMessage.REQUEST);

            	// Change to proper state
            	setModState(State.SLEEP);            	
            	break;
            case GAMESTARTING:
            	break;
            	}

    }

    public void serviceConfig()
    {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("moderator");
        serviceDescription.setName(this.getLocalName());
        Utils.registerService(serviceDescription,this);
        setModState(State.STARTING);
    }

    private void generatePlayerRoles() {

        System.out.println("GeneratePlayerRoles().");
        //generate villagers
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {

            entry.getValue().setRole(PlayerRole.Villager);
        }
        
    	//generate wolves - 30% of players
    	for(int i=0; i < Math.floor(this.numberPlayers * 0.3) ; i++) {
    		int rand = randomGenerator.nextInt(numberPlayers);
    		Iterator<Entry<AID,User>> it = users.entrySet().iterator();
    		Entry<AID,User> entry = it.next();
    		for(int j=1; j < rand; j++) {
    			entry = it.next();
    		}
    		if(entry.getValue().getRole().equals(PlayerRole.Werewolf)) {
    			i--;
    			continue;
    		}
    		else entry.getValue().setRole(PlayerRole.Werewolf);
    	}    	

    	//send roles along with opponents
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {

            User user = entry.getValue();

            //send names
            String names = new String();
            for (ConcurrentHashMap.Entry<AID,User> entry2 : users.entrySet()) {
               if(!entry.equals(entry2) && !(entry.getValue().getRole().equals(PlayerRole.Werewolf) && entry2.getValue().getRole().equals(PlayerRole.Werewolf)))
            	   names += entry2.getKey().getLocalName()+ " ";
            }
            
            Utils.sendMessage(user.getRole().name() + " " + names,user.getName(),ACLMessage.INFORM,this, null);
        }


        setModState(State.GAMESTARTING);
    }

    private void sendMessageToAllPlayers(String message, Object contentObject, int type) {
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            AID name = entry.getKey();
            Utils.sendMessage(message,name, type,this, contentObject);
        }
    }
    
    private void sendMessageToOfSameRole(PlayerRole role, String message, Object contentObject, int type) {
    	for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            if(entry.getValue().getRole().equals(role))
            	Utils.sendMessage(message, entry.getKey(), type,this, contentObject);
        }
    }



    private void informElimination(AID name)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("Eliminado");
        msg.setSender(name);
        this.send(msg);
    }
}
