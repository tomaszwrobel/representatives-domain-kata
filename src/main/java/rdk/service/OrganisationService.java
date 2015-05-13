package rdk.service;

import static rdk.model.Organisation.OrganisationBuilder.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rdk.exception.UnauthorizedAccessException;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;


@Service
public class OrganisationService {

    @Autowired
    private DocumentService documentService;

    public Organisation createNewOrganisation(String name, User user) {
        user.setOwnerRole();
        return organisation(name).ownedBy(user).build();
    }

    public void requestForActivation(Organisation newOrganisation, User owner) throws UnauthorizedAccessException {
        if (newOrganisation.isOwnedBy(owner)) {
            newOrganisation.awaitForActivation();
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rights for organisation activation");
        }
    }

    public void addMember(Organisation organisation, User owner, User newMember) throws UnauthorizedAccessException {
        if (organisation.assertMemberCanBeAddedBy(owner)) {
            organisation.addMember(newMember);
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rigths for adding new members");
        }
    }

    public void setNumOfRequiredAcknowledgments(Organisation organisation, int defaultNumOfAcknowledgments, User owner) throws UnauthorizedAccessException {
        organisation.setNumOfRequiredAcknowledgments(defaultNumOfAcknowledgments, owner);
    }

    public void activateOrganisation(Organisation organisation, User admin) throws UnauthorizedAccessException {
        organisation.activateBy(admin);
    }
    
    public void promoteMemberBy(Organisation organisation, User member, User promotor) throws UnauthorizedAccessException {
        if (canBeAPromotor(promotor)) {
            organisation.promote(member, promotor);
        } else {
            throw new UnauthorizedAccessException("User " + promotor.getName() + " has no rights to promote user");
        }
    }

    private boolean canBeAPromotor(User promotor) {
        return ((promotor.getRole() == UserRole.REPRESENTATIVE) || (promotor.getRole() == UserRole.ADMIN)) ? true : false;
    }

    public void cancelMemberRepresentativeRole(Organisation organisation, User representativeUser, User owner) throws UnauthorizedAccessException {
        organisation.cancelMembersRepresentative(representativeUser, owner);
    }

    public void addNewDocumentByUser(Organisation organisation, User organisationRepresentativeMember) throws UnauthorizedDocumentCreationException {
        if (organisation.isActive()) {
            organisation.addDocument(documentService.createDocumentByUser(organisationRepresentativeMember));
        } else {
            throw new UnauthorizedDocumentCreationException("Documents cannot be made when organisation is inactive");
        }
    }
}
