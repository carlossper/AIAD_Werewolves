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

    private State state;
    private ConcurrentHashMap<AID,User> users;
    private Random randomGenerator = new Random();
    
    public Moderator() {
		users = new ConcurrentHashMap<AID,User>();
        this.state = State.REGISTER;

    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        this.numberPlayers = Integer.parseInt((String)args[0]);
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
                               // System.out.println("Pedido de ligacao recebido.");
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
                                  //  System.out.println("resposta de aceitação recebido");
                                    users.put(msg.getSender(), new User(msg.getSender()));
                                    System.out.println("Jogador " + msg.getSender().getLocalName() + " conectado.");


                                    if (users.size() == numberPlayers)
                                    {
                                        state = State.ALLCONNECTED;
                                        sendMessageToAllPlayers("O jogo pode começar?");
                                        numberPlayers = 0;
                                    }
                                }
                            }
                            break;

                    }
                }
                else
                    block();

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
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("moderator");
        serviceDescription.setName(this.getLocalName());
        Utils.registerService(serviceDescription,this);
        state = State.STARTING;
    }

    private void generatePlayerTypes() {

        System.out.println("GeneratePlayerTypes().");
        //generate villagers
        //generate villagers
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {

            entry.getValue().setRole(PlayerRole.Villager);
        }
    	//generate wolves - 30% of players
    	int contador=0;
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            if(randomGenerator.nextInt(10 ) < 3 && contador < this.numberPlayers * 0.3)
            {
                entry.getValue().setRole(PlayerRole.Werewolf);
                contador++;
            }
        }
    	



    	/*for(ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
    		System.out.println("O jogador" + entry.getValue().getName().getLocalName() + " tem o papel de " + entry.getValue().getRole() + ".");
    	}*/
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            User user = entry.getValue();

            Utils.sendMessage(user.getRole().name(),user.getName(),ACLMessage.INFORM,this);
        }

    	state  = State.GAMEON;
    }

    private void sendMessageToAllPlayers(String message) {
        for (ConcurrentHashMap.Entry<AID,User> entry : users.entrySet()) {
            AID name = entry.getKey();
            Utils.sendMessage(message,name,ACLMessage.INFORM,this);
        }
    }
}
