package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 *  Usado pelo moderador para esperar as conexões dos jogadores.
 * Created by ruben on 09/11/2016.
 */
public class WaitingConection extends jade.core.behaviours.CyclicBehaviour{
    private Agent agent;

    public WaitingConection(Agent a) {
        super(a);
        this.agent = a;

    }

    public void action()
    {
        ACLMessage msg = this.agent.receive();
        System.out.println("waiting for players!");
        if(msg != null){
            try {
                if(msg.getContent().equalsIgnoreCase("ligacao"))
                {
                    ACLMessage reply = msg.createReply();
                    reply.setContent("conectado");
                    myAgent.send(reply);
                    agent.addBehaviour(new RegisterPlayer());
                }
            }
            catch (Exception e) {
                }
            }else
            block();

    }
}
