package rdk.model;


public class Organisation {

    private boolean active = false;
    
    private boolean activationAwaiting;

    public boolean isActive() {
        return active;
    }

    public boolean isActivationAwaiting() {
        return activationAwaiting;
    }
}
