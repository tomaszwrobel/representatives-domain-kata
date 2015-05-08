package rdk.model;

public class Organisation {

    private String name;

    private User owner;

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
}
