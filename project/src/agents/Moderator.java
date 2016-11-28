package agents;

import java.util.ArrayList;
import java.util.Random;
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
    private int numberPlayers;
    private ServiceDescription serviceDescription;
    private State state;
	//private ArrayList<User> users;
    private ConcurrentHashMap<AID,User> users;
    private Random randomGenerator = new Random();
    
    public Moderator() {
		users = new ConcurrentHashMap<AID,User>();
        this.state = State.REGISTER;
        this.serviceDescription = new ServiceDescription();
    }

    @Override
    protected void setup() {
        Object[] args = getArguments();
        this.numberPlayers = Integer.parseInt((String)args[0]);
        System.out.println(numberPlayers);
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
                                System.out.println("Pedido de ligacao recebido.");
                                Utils.sendMessage("Quer jogar Werewolf?",msg.getSender(),ACLMessage.PROPOSE,myAgent);
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
                                    System.out.println("resposta de aceitação recebido");
                                    users.put(msg.getSender(), new User(msg.getSender()));
                                    System.out.println(users.size());


                                    if (users.size() == numberPlayers)
                                    {
                                        state = State.ALLCONNECTED;
                                        sendStartGameMessage();
                                        numberPlayers = 0;
                                    }
                                }
                            }
                            break;

                    }
                }
                else
                    block();
                System.out.println("numPlayers: " + numberPlayers);
            }
        });
    }



    private void update() {
        switch (state)
        {
            case REGISTER: this.serviceConfig();
                break;
            case STARTING: break;
            case ALLCONNECTED:
                if(numberPlayers == users.size())
                generatePlayerTypes();
        }

    }

    public void serviceConfig()
    {
        this.serviceDescription.setType("moderator");
        this.serviceDescription.setName(this.getLocalName());
        Utils.registerService(this.serviceDescription,this);
        state = State.STARTING;
    }

    private void generatePlayerTypes() {

        System.out.println("chegou");
        //generate villagers
        //generate villagers
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {

            entry.getValue().setRole(PlayerRole.Villager);
        }
    	//generate wolves - 30% of players
    	/*int rand=0;
    	for(int i=0; i<0.3*numbersPlayers; i++) {
    		rand = randomGenerator.nextInt(numbersPlayers);
    		playerRoles[rand]=PlayerRole.Werewolf;
    	}*/
    	
    	/*//generate fortune_teller - 1
    	do {
    		rand = randomGenerator.nextInt(numbersPlayers);
    		if(playerRoles[rand]!=PlayerRole.Werewolf) playerRoles[rand]=PlayerRole.FortuneTeller;
    	}while(playerRoles[rand]!=PlayerRole.FortuneTeller);
    	*/
    	for(int i=0;i<users.size(); i++) {
    		System.out.println("Type[" + i + "]:" + users.get(i).getRole());
    	}
    	state  = State.GAMEON;
    }

    private void sendStartGameMessage() {
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            AID name = entry.getKey();
            Utils.sendMessage("O jogo pode começar?",name,ACLMessage.INFORM,this);
        }
    }
}
