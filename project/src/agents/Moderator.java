package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import utils.Utils;

/**
 * Created by ruben on 10/11/2016.
 */
public class Moderator extends Agent{
    private int numbersPlayers;
    private ServiceDescription serviceDescription;
    private State state;

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


}
