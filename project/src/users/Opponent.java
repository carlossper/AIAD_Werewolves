package users;

import jade.core.AID;

/**
 * Created by ei10117 on 07/12/2016.
 */
public class Opponent {

    private double trust;
    private String name;

    public Opponent() {
        this.setTrust(0.5);
        this.setName(name);
    }

	public double getTrust() {
		return trust;
	}

	public void setTrust(double trust) {
		this.trust = trust;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
