package test;

import jade.core.Runtime;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class Boot {

	//static public AID modAgent;
	//static public ArrayList<AID> playerAgents = new ArrayList<AID>();
	
	public static void main(String[] args) {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile p = new ProfileImpl();
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createMainContainer(p);
		// Create a new agent, a DummyAgent
		// and pass it a reference to an Object

		try {
			//gui
			cc.createNewAgent("gui_watch", "jade.tools.rma.rma", null).start();
			//sellers
			long sleepTime = 500;
			Thread.sleep(sleepTime);
			cc.createNewAgent("mod", "agents.Moderator", new Object[] {"10"}).start();
			Thread.sleep(sleepTime);
			//buyers
			cc.createNewAgent("play1", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play2", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play3", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play4", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play5", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play6", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play7", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play8", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play9", "agents.Player", null).start();
			Thread.sleep(sleepTime);
			cc.createNewAgent("play10", "agents.Player", null).start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
