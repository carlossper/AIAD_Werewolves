package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

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

    static public void sendMessage(String message, AID destination, int type, Agent agent, Object contentObject) {
        agent.addBehaviour(new OneShotBehaviour() {
                               @Override
                               public void action() {
                                   ACLMessage msg = new ACLMessage(type);
                                   msg.addReceiver(destination);
                                   if(message != null)
                                   {
                                       msg.setContent(message);
                                   }

                                   if(contentObject != null) {
                                       try {
                                           msg.setContentObject((Serializable) contentObject);
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }

                                   }

                                   agent.send(msg);
                               }
                           });

    }
}