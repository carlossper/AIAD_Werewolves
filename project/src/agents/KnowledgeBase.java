package agents;

import jade.core.AID;
import users.Opponent;
import users.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ei10117 on 07/12/2016.
 */
public class KnowledgeBase {
    private String time;
    private int turn;
    private ConcurrentHashMap<String, Opponent> opponents;

    public KnowledgeBase() {
        time = "day";
        turn = 0;
        opponents = new ConcurrentHashMap<String,Opponent>();
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


    public void saveopponents(String[] opponentsNames, String localName)
    {

        for(int i=2; i<opponentsNames.length; i++ )
        {
            if(!localName.equals(opponentsNames[i]))
            opponents.put(opponentsNames[i], new Opponent());
        }
       String mensagem = "";
        for (ConcurrentHashMap.Entry<String,Opponent> entry : opponents.entrySet() ) {
                mensagem += entry.getKey()+ " ,";
        }
        System.out.println(localName + "Os meus adversarios sao : " + mensagem);
    }

}
