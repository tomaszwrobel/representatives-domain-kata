package rdk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rdk.exception.UnauthorizedAccessException;
import rdk.exception.UnauthorizedDocumentCreationException;

public class Organisation {

    private String name;

    private User owner;
    
    private Set<User> members;

    private boolean active = false;

    private boolean activationAwaiting = false;

    private int numOfAcknowledgments = 3;

    private List<Document> documents;

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
    
    public Set<User> getMembers() {
        if (members == null) {
            members = new HashSet<User>();
        }
        return members;
    }

    public static class OrganisationBuilder {

        private User owner;

        private String name;

        private boolean isActive;

        private boolean activationAwaiting;
        
        private Set<User> members;

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
        
        public OrganisationBuilder inActive() {
            this.isActive = false;
            return this;
        }
        
        public OrganisationBuilder withMembers(User... users) {
            if (members == null) {
                members = new HashSet<User>();
            }
            members.addAll(Arrays.asList(users));
            return this;
        }

        public Organisation build() {
            Organisation newOrganisation = new Organisation();

            newOrganisation.name = this.name;
            newOrganisation.owner = this.owner;
            newOrganisation.activationAwaiting = this.activationAwaiting;
            newOrganisation.active = this.isActive;
            newOrganisation.members = this.members;

            return newOrganisation;
        }
    }

    public boolean isOwnedBy(User user) {
        return owner.getName() == user.getName() ? true : false;
    }

    public void addMember(User newMember) {
        if (!java.util.Objects.equals(newMember, this.owner)) {
            if (!isActive()) {   
                newMember.setRepresentativeRole();
                getMembers().add(newMember);
            } else {
                getMembers().add(newMember);
            }
        }
    }

    public boolean assertMemberCanBeAddedBy(User user) {
        return (user.getRole() == UserRole.OWNER) && (user.getName() == this.owner.getName()) ? true : false;
    }

    public void setNumOfRequiredAcknowledgments(int numOfRequiredAcknowledgments, User owner) throws UnauthorizedAccessException {
        if (assertIsAnOwner(owner)) {
            this.numOfAcknowledgments  = numOfRequiredAcknowledgments;
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rights to change number of acknowledgments");
        }
    }

    private boolean assertIsAnOwner(User user) {
        return java.util.Objects.equals(user, this.owner);
    }

    public int getNumOfAcknowledgments() {
        return numOfAcknowledgments;
    }

    public void activateBy(User admin) throws UnauthorizedAccessException {
        if (assertIsAdmin(admin)) {
            active = true;
        } else {
            throw new UnauthorizedAccessException("Only admin can activate organisation");
        }
    }

    private boolean assertIsAdmin(User admin) {
        return admin.getRole() == UserRole.ADMIN ? true : false;
    }

    public void promote(User member, User promotor) throws UnauthorizedAccessException {
        if (userBelongsToThisOrganisation(member, promotor)) {
            member.getPromoters().add(promotor);
        } else {
            throw new UnauthorizedAccessException("User can be promoted only by users in the same organisation");
        }
        if (member.getPromoters().size() == numOfAcknowledgments) {
            member.setRepresentativeRole();
        }
    }

    private boolean userBelongsToThisOrganisation(User... users) {
        return getMembers().containsAll(Arrays.asList(users));
    }

    public void cancelMembersRepresentative(User member, User owner) throws UnauthorizedAccessException {
        if (assertIsAnOwner(owner)) {
            member.cancelRepresentativeRole();
        } else {
            throw new UnauthorizedAccessException("Only owner can demote representative user");
        }
    }

    public void addDocument(Document createDocumentByUser) throws UnauthorizedDocumentCreationException {
        if (userBelongsToThisOrganisation(createDocumentByUser.getCreator())) {
            getDocuments().add(createDocumentByUser);
        } else {
            throw new UnauthorizedDocumentCreationException("Document can be created only by users from within this organisation");
        }
    }

    public List<Document> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<Document>();
        }
        return documents;
    }
}
