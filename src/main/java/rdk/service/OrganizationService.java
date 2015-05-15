package rdk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rdk.exception.UnauthorizedAccessException;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.Organization;
import rdk.model.User;
import rdk.model.UserRole;


@Service
public class OrganizationService {

    @Autowired
    private DocumentService documentService;

    public Organization createNewOrganisation(String name, User user) {
        return new Organization(name, user, false, false, null);
    }

    public void requestForActivation(Organization newOrganization, User owner) throws UnauthorizedAccessException {
        newOrganization.awaitForActivation(owner);
    }

    public void addMember(Organization organization, User owner, User newMember) throws UnauthorizedAccessException {
        organization.addMemberBy(newMember, owner);
    }

    public void setNumOfRequiredAcknowledgments(Organization organization, int numOfAcknowledgments, User owner)
            throws UnauthorizedAccessException {
        organization.setNumOfRequiredAcknowledgments(numOfAcknowledgments, owner);
    }

    public void activateOrganisation(Organization organization, User admin) throws UnauthorizedAccessException {
        organization.activateBy(admin);
    }

    public void promoteMemberBy(Organization organization, User member, User promotor) throws UnauthorizedAccessException {
        organization.promote(member, promotor);
    }

    public void cancelMemberRepresentativeRole(Organization organization, User representativeUser, User owner)
            throws UnauthorizedAccessException {
        organization.cancelMembersRepresentative(representativeUser, owner);
    }

    public void addNewDocumentByUser(Organization organization, User organizationRepresentativeMember)
            throws UnauthorizedDocumentCreationException {
            organization.addDocumentByUser(documentService.createDocumentByUser(organizationRepresentativeMember), organizationRepresentativeMember);
    }

    public void setNumOfRequiredDocumentConfirmations(Organization organization, int numOfDocumentConfirmations, User owner) {
        organization.setNumOfRequiredDocumentConfirmation(numOfDocumentConfirmations, owner);
    }
}
