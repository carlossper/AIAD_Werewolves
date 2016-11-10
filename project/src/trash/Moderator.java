package trash;

import jade.core.Agent;
import behaviours.*;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import utils.*;

import javax.swing.*;

/**
 * Created by ruben on 09/11/2016.
 */
public class Moderator extends Agent {
    private ServiceDescription serviceDescription;

    @Override
    protected void setup() {
        this.serviceDescription = new ServiceDescription();
        this.serviceConfig();
        addBehaviour(new WaitingConection(this));
    }


    public void takeDown() {
        System.out.println("GoodBye World!");
    }

    public void serviceConfig()
    {
        this.serviceDescription.setType("moderator");
        this.serviceDescription.setName(this.getLocalName());
        Utils.registerService(this.serviceDescription,this);
    }





}
