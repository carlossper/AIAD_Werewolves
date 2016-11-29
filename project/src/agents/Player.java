package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.*;


/**
 * Created by ruben on 10/11/2016.
 */
public class Player extends Agent {
    protected ServiceDescription serviceDescription;
    protected State state;
    private AID moderatorName;
    private PlayerRole role;
    
    public Player() {
        this.state = State.CONNECTING;
        this.serviceDescription = new ServiceDescription();

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
                state = State.GAMEON;
                break;
            case GAMEON:
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

                            if(msg.getContent().equals("O jogo pode começar?"))
                            {
                                Utils.sendMessage("Sim, estou pronto.",moderatorName, ACLMessage.INFORM,myAgent);
                            }

                            if(msg.getContent().equals(PlayerRole.Werewolf.name()))
                            {
                                role = PlayerRole.Werewolf;
                                state = State.WAKE;
                            }
                            else if(msg.getContent().equals(PlayerRole.Villager.name()))
                            {
                                role = PlayerRole.Villager;
                                state = state.WAKE;
                            }





                            break;
                        case ACLMessage.PROPOSE:

                            if(msg.getContent().equals("Quer jogar Werewolf?"))
                            {
                               // System.out.println("Proposta para jogar reebida");
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
        Utils.sendMessage("Estabelecer Ligacao",moderatorName, ACLMessage.INFORM,this);
        state = State.CONNECTINGSENT;


    }

    private void acceptConnected()
    {
        //System.out.println("Enviou mensagem para aceitar jogar.");
        Utils.sendMessage("Aceito Ligacao",moderatorName,ACLMessage.ACCEPT_PROPOSAL,this);
        state = State.WAITING;
    }
}