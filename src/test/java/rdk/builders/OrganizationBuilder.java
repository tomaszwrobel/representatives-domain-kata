package rdk.builders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import rdk.model.Organization;
import rdk.model.User;


public class OrganizationBuilder {

    private User owner;

    private String name;

    private boolean isActive;

    private boolean activationAwaiting;

    private Set<User> members;

    public OrganizationBuilder() {

    }

    public OrganizationBuilder(String name) {
        this.name = name;
    }

    public static OrganizationBuilder organization(String name) {
        return new OrganizationBuilder(name);
    }

    public static OrganizationBuilder organization() {
        return new OrganizationBuilder();
    }

    public OrganizationBuilder ownedBy(User owner) {
        this.owner = owner;
        return this;
    }

    public OrganizationBuilder awaitsForActivation() {
        this.activationAwaiting = true;
        return this;
    }

    public OrganizationBuilder active() {
        this.isActive = true;
        return this;
    }

    public OrganizationBuilder inActive() {
        this.isActive = false;
        return this;
    }

    public OrganizationBuilder withMembers(User... users) {
        if (members == null) {
            members = new HashSet<User>();
        }
        members.addAll(Arrays.asList(users));
        return this;
    }

    public Organization build() {
        return new Organization(this.name, this.owner,this.isActive, this.activationAwaiting, this.members);
    }
}
