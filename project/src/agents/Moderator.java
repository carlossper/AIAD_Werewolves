package agents;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import gui.WerewolvesGUI;
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
    private int messagesReceived;
    private int currentWerewolves;
    private Boolean voteEnded;
    private State state = State.REGISTER;
    private ConcurrentHashMap<String,User> users;
    private Random randomGenerator = new Random();
    
    //gui
    private WerewolvesGUI gui= null;
    //property change events
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public Moderator() {
		users = new ConcurrentHashMap<String,User>();
    }
    
    public Moderator(int numPlayers) {
    	numberPlayers=numPlayers;
    	voteEnded = true;
    	messagesReceived = 0;
    	currentWerewolves = (numberPlayers*3)/10;
		users = new ConcurrentHashMap<String,User>();
    }

    public void setGUI(WerewolvesGUI wwGUI) {
    	gui=wwGUI;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public State getModState() {
    	return state;
    }
    private void setModState(State newS) {
    	if(newS==State.WEREWOLVES_VOTING || newS==State.WAITING_VOTES_WEREWOLVES ||
    			newS==State.WAITING_VOTES || newS==State.DAY_VOTING) {
    		synchronized(this) {
    			try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    		
    	State oldS=state;
    	state = newS;
    	this.pcs.firePropertyChange("", oldS, state);
    }
    
    public User getUser(AID key) {
    	return users.get(key);
    }
    public ConcurrentHashMap<String,User> getUsers() {
    	return users;
    }
    
    @Override
    protected void setup() {

        Object[] args = getArguments();
        if(args!=null) this.numberPlayers = Integer.parseInt((String)args[0]);
        System.out.println("Venham jogar Werewolves of Miller's Hollow!!!! Sao precisos " + this.numberPlayers + " jogadores!" );
        if(gui!=null)gui.log("Venham jogar Werewolves of Miller's Hollow!!!! Sao precisos " + this.numberPlayers + " jogadores!" );
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


                            if(msg.getContent().equals("Eliminaçao confirmada"))
                            {

                                messagesReceived++;

                                if(messagesReceived == users.size())
                                    voteEnded = true;
                            }

                            if(msg.getContent().equals("Estabelecer Ligacao"))
                            {
                                //System.out.println("Pedido de ligacao recebido.");
                                Utils.sendMessage("Quer jogar Werewolf?",msg.getSender(),ACLMessage.PROPOSE,myAgent, null);
                            }

                            if(msg.getContent().equals("Sim, estou pronto."))
                            {
                               users.get(msg.getSender().getLocalName()).setAlive(true);
                               numberPlayers++;
                            }

                            if(!voteEnded) {
                                if (msg.getContent().startsWith("Vote")) {
                                    String[] mensagems = msg.getContent().split(" ");
                                    messagesReceived++;

                                    users.get(mensagems[1]).addVote();
                                    if (messagesReceived == users.size()) {
                                        mostVoted();
                                    } else if (messagesReceived == currentWerewolves && state == State.WAITING_VOTES_WEREWOLVES) {

                                        mostVoted();
                                    }

                                }
                            }

                            if(msg.getContent().equals("pronto"))
                            {
                                messagesReceived++;
                                System.out.println(messagesReceived);
                                if(gui!=null)gui.log(""+messagesReceived);
                                if(messagesReceived == users.size())
                                    setModState(State.WEREWOLVES_VOTING);
                            }

                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:

                            if(state == State.STARTING) {
                                if (msg.getContent().equals("Aceito Ligacao")) ;
                                {
                                	//System.out.println("resposta de aceitação recebido");
                                    users.put(msg.getSender().getLocalName(), new User(msg.getSender()));
                                    System.out.println("Jogador " + msg.getSender().getLocalName() + " conectado.");
                                    if(gui!=null)gui.log("Jogador " + msg.getSender().getLocalName() + " conectado.");
                                    
                                    if (users.size() == numberPlayers)
                                    {
                                        System.out.println("Todos os jogadores estao conectados, o jogo vai comecar.");
                                        if(gui!=null)gui.log("Todos os jogadores estao conectados, o jogo vai comecar.");
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
                    setModState(State.GAMESTARTING);

                }
                break;
            case WEREWOLVES_VOTING:
                messagesReceived = 0;
                if(voteEnded) {
                    System.out.println("\nWerewolves voting\n");
                    if(gui!=null)gui.log("\nWerewolves voting\n");

                    for (ConcurrentHashMap.Entry<String, User> entry : users.entrySet()) {
                        if (entry.getValue().getRole().equals(PlayerRole.Werewolf)) {
                            AID dst = entry.getValue().getName();
                            Utils.sendMessage("Votacao Werewolves", dst, ACLMessage.REQUEST, this, null);
                            setModState(State.WAITING_VOTES_WEREWOLVES);
                        }
                    }
                }
                voteEnded = false;

            	break;

            case DAY_VOTING:
                messagesReceived = 0;
                if(voteEnded)
                {
                    System.out.println("\nCommunity voting\n");
                    if(gui!=null)gui.log("\nCommunity voting\n");
                    this.sendMessageToAllPlayers("Votacao Geral", null, ACLMessage.REQUEST);
                    setModState(State.WAITING_VOTES);
                }
                voteEnded = false;

            	break;

            case SLEEP:
                break;

            case WAITING_VOTES_WEREWOLVES:
                if(voteEnded) {
                    System.out.println("\nDAY\nVillagers: " + (users.size() - currentWerewolves)+"\nWerewolfs: " + currentWerewolves);
                    if(gui!=null)gui.log("\nDAY\nVillagers: " + (users.size() - currentWerewolves)+"\nWerewolfs: " + currentWerewolves);
                    if(currentWerewolves == 0 || users.size() - currentWerewolves == 0)
                        setModState(State.GAMEDONE);
                    else
                    setModState(State.DAY_VOTING);

                    messagesReceived = 0;
                }

                break;

            case WAITING_VOTES:

                if(voteEnded) {
                    System.out.println("\nNIGHT\nVillagers: " + (users.size() - currentWerewolves)+"\nWerewolfs: " + currentWerewolves);
                    if(gui!=null)gui.log("\nNIGHT\nVillagers: " + (users.size() - currentWerewolves)+"\nWerewolfs: " + currentWerewolves);
                    if(currentWerewolves == 0 || users.size() - currentWerewolves == 0)
                        setModState(State.GAMEDONE);
                    else
                        setModState(State.WEREWOLVES_VOTING);

                    messagesReceived = 0;
                }

                break;
            case GAMESTARTING:
            	break;

            case GAMEDONE:
                if(currentWerewolves == 0){
                    System.out.println("\nGanharam os Villagers");
                    if(gui!=null)gui.log("\nGanharam os Villagers");
                }
                else {
                	System.out.println("\nGanharam os Werewolfs");
                	if(gui!=null)gui.log("\nGanharam os Werewolfs");
                }
                setModState(State.GAMESTARTING);
                break;

            	}



    }

    public void serviceConfig() {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("moderator");
        serviceDescription.setName(this.getLocalName());
        Utils.registerService(serviceDescription,this);
        setModState(State.STARTING);
    }

    private void generatePlayerRoles() {

       // System.out.println("GeneratePlayerRoles().");
        //generate villagers
        for (ConcurrentHashMap.Entry<String,User> entry : users.entrySet()) {

            entry.getValue().setRole(PlayerRole.Villager);
        }
        
    	//generate wolves - 30% of players
    	for(int i=0; i < Math.floor(this.numberPlayers * 0.3) ; i++) {
    		int rand = randomGenerator.nextInt(numberPlayers);
    		Iterator<Entry<String,User>> it = users.entrySet().iterator();
    		Entry<String,User> entry = it.next();
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
        for (ConcurrentHashMap.Entry<String,User> entry : users.entrySet()) {

            User user = entry.getValue();

            //send names
            String names = new String();
            for (ConcurrentHashMap.Entry<String,User> entry2 : users.entrySet()) {
               if(!entry.equals(entry2) && !(entry.getValue().getRole().equals(PlayerRole.Werewolf) && entry2.getValue().getRole().equals(PlayerRole.Werewolf)))
            	   names += entry2.getKey() + " ";
            }
            
            Utils.sendMessage(user.getRole().name() + " " + names,user.getName(),ACLMessage.INFORM,this, null);
        }


        messagesReceived = 0;
    }

    private void sendMessageToAllPlayers(String message, Object contentObject, int type) {
        for (ConcurrentHashMap.Entry<String,User> entry : users.entrySet()) {
            AID name = entry.getValue().getName();
            Utils.sendMessage(message,name, type,this, contentObject);
        }
    }

    public void mostVoted()
    {
        AID mostVoted = null;
        int max = 0;
        for (ConcurrentHashMap.Entry<String,User> entry : users.entrySet()) {
            System.out.println(entry.getKey() + ": "+ entry.getValue().getVotes() + ", " + entry.getValue().getRole());
            if(gui!=null)gui.log(entry.getKey() + ": "+ entry.getValue().getVotes() + ", " + entry.getValue().getRole());
            if(entry.getValue().getVotes() > max) {
                mostVoted = entry.getValue().getName();
                max = entry.getValue().getVotes();
            }
        }

        messagesReceived = 0;

        for (ConcurrentHashMap.Entry<String,User> entry : users.entrySet()) {
            entry.getValue().resetVotes();
            if(entry.getValue().getName() == mostVoted) {
                Utils.sendMessage("Eliminado", mostVoted, ACLMessage.INFORM, this, null);
                if(entry.getValue().getRole() == PlayerRole.FortuneTeller.Werewolf)
                    currentWerewolves--;

            }
            else
                Utils.sendMessage("Eliminacao "+ mostVoted.getLocalName(),entry.getValue().getName(), ACLMessage.INFORM,this,null);
        }

        users.remove(mostVoted.getLocalName());
    }
}
