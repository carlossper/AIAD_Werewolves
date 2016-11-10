package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 * Created by ruben on 09/11/2016.
 */
public class Utils {
    static public void registerService(ServiceDescription service, Agent agent) {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(service);
        try {
            DFService.register(agent, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    static public void sendMessage(String message, AID destination, int type, Agent agent) {
        ACLMessage msg = new ACLMessage(type);
        msg.addReceiver(destination);
        msg.setContent(message);
        agent.send(msg);
    }
}