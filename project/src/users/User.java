package users;

import jade.core.AID;
import utils.PlayerRole;

/**
 * Created by ei10117 on 28/11/2016.
 */
public class User {
    private AID name;
    private PlayerRole role;
    private Boolean alive = false;


    public User(AID name) {
        this.name = name;
    }

    public AID getName() {
        return name;
    }

    public void setName(AID name) {
        this.name = name;
    }

    public PlayerRole getRole() {
        return role;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }
}




