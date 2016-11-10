package trash;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import behaviours.*;
/**
 * Created by ruben on 09/11/2016.
 */
public class Jogador extends Agent {
    private ServiceDescription serviceDescription;
    private AID moderatorName;
    private Boolean alive=true;

    @Override
    protected void setup() {
        this.serviceDescription = new ServiceDescription();
        this.serviceConfig();
        addBehaviour(new SendMessage("ligacao",moderatorName,ACLMessage.INFORM,this));

        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                System.out.println("out");
                ACLMessage msg = receive();
                if(msg != null){
                    System.out.println(msg.getSender() + " : " + msg.getContent());
                }else
                    block();


            }
        });

    }


    public void takeDown() {
        System.out.println("GoodBye World!");
    }


    private void serviceConfig() {
        this.serviceDescription.setType("moderator");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(serviceDescription);
        try{
            DFAgentDescription[] resultado = DFService.search(this, dfd);
            this.moderatorName = resultado[0].getName();
        }catch(FIPAException e){
            e.printStackTrace();
        }
    }

    public ServiceDescription getServiceDescription() {
        return serviceDescription;
    }

    public AID getModeratorName() {
        return moderatorName;
    }
}
