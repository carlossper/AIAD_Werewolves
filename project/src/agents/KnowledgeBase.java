package agents;

import jade.core.AID;
import jade.util.leap.Iterator;
import users.Opponent;
import users.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ei10117 on 07/12/2016.
 */
public class KnowledgeBase {
    private String time;
    private int turn;
    private ArrayList<Opponent> opponents = new ArrayList<Opponent>();
    
    public KnowledgeBase() {
        time = "day";
        turn = 0;
    }

    public void  addTurn() {
        turn++;
    }

    public void setDay()
    {
        time = "day";
    }

    public void setNight(){
        time = "night";
    }

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
        String mensagem = "";
      /*  for (int i=0; i<opponents.size(); i++) {
                mensagem += opponents.get(i).getName()+ ", ";
        }
        System.out.println(localName + ": Os meus adversarios sao : " + mensagem);*/
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
