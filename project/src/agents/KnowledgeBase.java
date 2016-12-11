package agents;

import users.Opponent;
import java.util.ArrayList;

/**
 * Created by ei10117 on 07/12/2016.
 */
public class KnowledgeBase {
    private ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    public ArrayList<Opponent> getOpponents()
    {
    	return opponents;
    }

    public void saveopponents(String[] opponentsNames, String localName)
    {

        for(int i=1; i<opponentsNames.length; i++ )
        {
            Opponent op = new Opponent();
            op.setName(opponentsNames[i]);
            opponents.add(op);
        }
    }

    public void deleteOpponent(String name)
    {

        java.util.Iterator<Opponent> it = opponents.iterator();

        while (it.hasNext()) {

            if (it.next().getName().equals(name)) {

                it.remove();
                break;
            }
        }

    }

}
