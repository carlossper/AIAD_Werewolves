package agents;

import java.util.Arrays;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import utils.*;

/**
 * Created by ruben on 10/11/2016.
 */
public class Moderator extends Agent{
    private int numbersPlayers;
    private ServiceDescription serviceDescription;
    private State state;

    private Random randomGenerator = new Random();
    
    public Moderator() {
        this.numbersPlayers = 0;
        this.state = State.REGISTER;
        this.serviceDescription = new ServiceDescription();
    }

    @Override
    protected void setup() {
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
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:

                            if(msg.getContent().equals("Aceito Ligacao"));
                            {
                                System.out.println("resposta de aceitação recebido");
                                numbersPlayers++;
                                
                                if(numbersPlayers==5) generatePlayerTypes();
                            }
                            break;

                    }
                }
                else
                    block();
                System.out.println("numPlayers: " + numbersPlayers);
            }
        });
    }

    private void update() {
        switch (state)
        {
            case REGISTER: this.serviceConfig();
                break;
            case STARTING: break;
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
    	PlayerType[] playerTypes = new PlayerType[numbersPlayers];
    	
    	//generate villagers
    	Arrays.fill(playerTypes, PlayerType.Villager);
    	
    	//generate wolves - 40% of players
    	int rand=0;
    	for(int i=0; i<0.4*numbersPlayers; i++) {
    		rand = randomGenerator.nextInt(numbersPlayers);
    		playerTypes[rand]=PlayerType.Werewolf;
    	}
    	
    	//generate fortune_teller - 1
    	do {
    		rand = randomGenerator.nextInt(numbersPlayers);
    		if(playerTypes[rand]!=PlayerType.Werewolf) playerTypes[rand]=PlayerType.FortuneTeller;
    	}while(playerTypes[rand]!=PlayerType.FortuneTeller);
    	
    	for(int i=0;i<numbersPlayers; i++) {
    		System.out.println("Type[" + i + "]:" + playerTypes[i]);
    	}
    }

}
