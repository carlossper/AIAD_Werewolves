package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/** Comportamento OneShot para enviar uma ACLMessage.
 * Created by ruben on 09/11/2016.
 */
public class SendMessage extends jade.core.behaviours.OneShotBehaviour {
    private String message;
    private AID destination;
    private int type;
    private Agent agent;
    public SendMessage(String message, AID destination, int type, Agent agent) {
        this.message = message;
        this.destination = destination;
        this.agent = agent;



    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(type);
        msg.addReceiver(this.destination);
        msg.setContent(this.message);
        agent.send(msg);
    }
}
