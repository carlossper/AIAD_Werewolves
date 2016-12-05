package behaviours;

/** Comportamento oneShot que vai comunicar com todos os jogadores
 *  para os meter na lista de espera para começar o jogo.
 * Created by ruben on 09/11/2016.
 */
public class RegisterPlayer extends jade.core.behaviours.OneShotBehaviour {

    @Override
    public void action() {
        //está verde ainda, era só para ver a ligação
        //o objectivo é mandar a ACLMessage tipo PROPOSE, a convidar para se inscrever no jogo
        //e o jogador responde com tipo ACCEPT_PROPOSAL

        System.out.println("cabbumm");
    }
}
