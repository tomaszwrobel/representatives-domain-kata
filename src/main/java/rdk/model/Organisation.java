package rdk.model;

import java.util.ArrayList;
import java.util.List;

public class Organisation {

    private String name;

    private User owner;
    
    private List<User> members;

    private boolean active = false;

    private boolean activationAwaiting = false;

    public boolean isActive() {
        return active;
    }

    public boolean isActivationAwaiting() {
        return activationAwaiting;
    }

    public void awaitForActivation() {
        this.activationAwaiting = true;
    }

    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
        this.owner.setOwnerRole();
    }
    
    public String getName() {
        return name;
    }
    
    public List<User> getMembers() {
        if (members == null) {
            members = new ArrayList<User>();
        }
        return members;
    }

    public static class OrganisationBuilder {

        private User owner;

        private String name;

        private boolean isActive;

        private boolean activationAwaiting;

        public OrganisationBuilder() {

        }

        public OrganisationBuilder(String name) {
            this.name = name;
        }

        public static OrganisationBuilder organisation(String name) {
            return new OrganisationBuilder(name);
        }

        public static OrganisationBuilder organisation() {
            return new OrganisationBuilder();
        }

        public OrganisationBuilder ownedBy(User owner) {
            this.owner = owner;
            return this;
        }

        public OrganisationBuilder awaitsForActivation() {
            this.activationAwaiting = true;
            return this;
        }

        public OrganisationBuilder active() {
            this.isActive = true;
            return this;
        }

        public Organisation build() {
            Organisation newOrganisation = new Organisation();

            newOrganisation.name = this.name;
            newOrganisation.owner = this.owner;
            newOrganisation.activationAwaiting = this.activationAwaiting;
            newOrganisation.active = this.isActive;

            return newOrganisation;
        }
    }

    public boolean isOwnedBy(User user) {
        return owner.getName() == user.getName() ? true : false;
    }

    public void addMember(User newMember) {
        if (!isActive()) {
            newMember.setRepresentativeRole();
        }
        getMembers().add(newMember);
    }

    public boolean assertMemberCanBeAddedBy(User user) {
        return (user.getRole() == UserRole.OWNER) && (user.getName() == this.owner.getName()) ? true : false;
    }
}
