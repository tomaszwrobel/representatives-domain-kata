package rdk.e2e;

import static rdk.model.Organisation.OrganisationBuilder.organisation;
import static rdk.model.User.UserBuilder.user;
import static rdk.assertions.OrganisationAssert.assertThat;
import static rdk.assertions.DocumentAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rdk.IntegrationTestBase;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.DocumentStatus;
import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganisationService;


public class DocumentE2ETest extends IntegrationTestBase {
    
    @Autowired
    OrganisationService organisationService;
    
    User owner;
    User organisationRepresentativeMember;
    User regularUserInOrganisation;
    Organisation inActiveTestOrganisation;
    Organisation activeTestOrganisation;
    
    @Before
    public void init() {
        owner = user("owner User").withRole(UserRole.OWNER).build();
        organisationRepresentativeMember = user("representative member").withRole(UserRole.REPRESENTATIVE).build();
        regularUserInOrganisation = user("regular user within organisation").withRole(UserRole.REGULAR).build();
        
        inActiveTestOrganisation = organisation("test organisation").inActive().ownedBy(owner).withMembers(organisationRepresentativeMember).build();
        activeTestOrganisation = organisation("test organisation").active().ownedBy(owner).withMembers(organisationRepresentativeMember, regularUserInOrganisation).build();
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void userCannotCreateDocumentWhenOrganisationIsInActive() throws UnauthorizedDocumentCreationException {
        organisationService.addNewDocumentByUser(inActiveTestOrganisation, organisationRepresentativeMember);
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void regularUserCannotCreateDocument() throws UnauthorizedDocumentCreationException {
        organisationService.addNewDocumentByUser(activeTestOrganisation, regularUserInOrganisation);
    }
    
    @Test
    public void representativeUserAddsNewDocument() throws UnauthorizedDocumentCreationException {
        organisationService.addNewDocumentByUser(activeTestOrganisation, organisationRepresentativeMember);
        
        assertThat(activeTestOrganisation).hasNumOfDocuments(1);
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void userOutsideOfOrganisationCannotCreateNewDocument() throws UnauthorizedDocumentCreationException {
        User userNotInOrganisation = user("representative member").withRole(UserRole.REPRESENTATIVE).build();
        
        organisationService.addNewDocumentByUser(activeTestOrganisation, userNotInOrganisation);
    }
    
    @Test
    public void userCreatesDocumentWithStatusUnconfirmed() throws UnauthorizedDocumentCreationException {
        organisationService.addNewDocumentByUser(activeTestOrganisation, organisationRepresentativeMember);
        
        assertThat(activeTestOrganisation).hasNumOfDocuments(1);
        assertThat(activeTestOrganisation.getDocuments().get(0)).hasStatus(DocumentStatus.UNCONFIRMED);
    }

}
