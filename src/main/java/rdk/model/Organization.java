package rdk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rdk.exception.UnauthorizedAccessException;
import rdk.exception.UnauthorizedDocumentCreationException;


public class Organization {

    private String name;

    private User owner;

    private Set<User> members;

    private boolean active = false;

    private boolean activationAwaiting = false;

    private int numOfAcknowledgments = 3;

    private int numOfDocumentConfirmations = 3;

    private List<Document> documents;

    public Organization(String name, User owner, boolean active, boolean activationAwaiting, Set<User> members) {
        owner.setOwnerRole();

        this.name = name;
        this.owner = owner;
        this.active = active;
        this.activationAwaiting = activationAwaiting;
        this.members = members;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isActivationAwaiting() {
        return activationAwaiting;
    }

    public void awaitForActivation(User user) throws UnauthorizedAccessException {
        if (isOwnedBy(user)) {
            this.activationAwaiting = true;
        } else {
            throw new UnauthorizedAccessException("This can be done only by owner");
        }
    }

    public User getOwner() {
        return owner;
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

    public boolean isOwnedBy(User user) {
        return java.util.Objects.equals(user, owner);
    }

    public void addMemberBy(User newMember, User owner) throws UnauthorizedAccessException {
        if (!isOwnedBy(newMember)) {
            addNewMember(newMember, owner);
        } else {
            throw new UnauthorizedAccessException("Owner cannot become representative user");
        }
    }

    private void addNewMember(User newMember, User owner) throws UnauthorizedAccessException {
        if (isOwnedBy(owner)) {
            getMembers().add(newMember);
        } else {
            throw new UnauthorizedAccessException("Only organization owner can add new members");
        }

    }

    public boolean assertMemberCanBeAddedBy(User user) {
        return (user.getRole() == UserRole.OWNER) && (isOwnedBy(user)) ? true : false;
    }

    public void setNumOfRequiredAcknowledgments(int numOfRequiredAcknowledgments, User owner) throws UnauthorizedAccessException {
        if (isOwnedBy(owner)) {
            this.numOfAcknowledgments = numOfRequiredAcknowledgments;
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rights to change number of acknowledgments");
        }
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
        if (isActive()) {
            promoteWhenOrganisationIsActive(member, promotor);
        } else {
            promoteWhenOrganisationIsInactive(member, promotor);
        }
    }
    
    private void promoteWhenOrganisationIsInactive(User newMember, User promotor) throws UnauthorizedAccessException {
        if (isOwnedBy(promotor)) {
            if (userBelongsToThisOrganisation(newMember)) {
                newMember.setRepresentativeRole();
            }
        } else {
            throw new UnauthorizedAccessException("User can be promoted only by owners when organisationis not active");
        }
    }
    
    private void promoteWhenOrganisationIsActive(User newMember, User promotor) throws UnauthorizedAccessException {
        if (userBelongsToThisOrganisation(newMember, promotor)) {
            newMember.promoteBy(promotor);
        } else {
            throw new UnauthorizedAccessException("User can be promoted only by users in the same organisation");
        }
        if (hasEnoughAcknowledgements(newMember)) {
            newMember.setRepresentativeRole();
        }
    }
    
    private boolean hasEnoughAcknowledgements(User member) {
        return member.getPromoters().size() >= numOfAcknowledgments ? true : false;
    }

    private boolean userBelongsToThisOrganisation(User... users) {
        return getMembers().containsAll(Arrays.asList(users));
    }

    public void cancelMembersRepresentative(User member, User owner) throws UnauthorizedAccessException {
        if (isOwnedBy(owner)) {
            member.cancelRepresentativeRole();
        } else {
            throw new UnauthorizedAccessException("Only owner can demote representative user");
        }
    }

    public void addDocumentByUser(Document document, User user) throws UnauthorizedDocumentCreationException {
        if (userBelongsToThisOrganisation(user)) {
            addDocumentTo(document);
        } else {
            throw new UnauthorizedDocumentCreationException("Document can be created only by users from within this organisation");
        }
    }

    private void addDocumentTo(Document document) throws UnauthorizedDocumentCreationException {
        if (active) {
            getDocuments().add(document);
        } else {
            throw new UnauthorizedDocumentCreationException("Documents cannot be made when organization is inactive");
        }
    }

    public List<Document> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<Document>();
        }
        return documents;
    }

    public int getNumOfRequiredDocumentConfirmations() {
        return numOfDocumentConfirmations;
    }

    public void setNumOfRequiredDocumentConfirmation(int numOfRequiredDocumentConfirmation, User owner) {
        this.numOfDocumentConfirmations = numOfRequiredDocumentConfirmation;
    }
}
