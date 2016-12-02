package test;

import jade.core.Runtime;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class Boot {

	//static public AID modAgent;
	//static public ArrayList<AID> playerAgents = new ArrayList<AID>();
	
	public static void main(String[] args) {
		
		if(args.length<1) {
			System.out.println("Missing arg[0]=number of players");
			return;
		}
		
		int numPlayers = Integer.parseInt(args[0]);
		
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
			cc.createNewAgent("mod", "agents.Moderator", new Object[] {args[0]}).start();
			Thread.sleep(sleepTime);
			//buyers
			
			for(int i=0; i<numPlayers; i++) {
				cc.createNewAgent("play"+i, "agents.Player", null).start();
				Thread.sleep(sleepTime);
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
