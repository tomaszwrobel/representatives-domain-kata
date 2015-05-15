package rdk.e2e;

import static rdk.builders.OrganizationBuilder.organization;
import static rdk.model.User.UserBuilder.user;
import static rdk.assertions.OrganisationAssert.assertThat;
import static rdk.assertions.DocumentAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rdk.IntegrationTestBase;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.DocumentStatus;
import rdk.model.Organization;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganizationService;


public class DocumentE2ETest extends IntegrationTestBase {
    
    @Autowired
    OrganizationService organizationService;
    
    User owner;
    User organizationRepresentativeMember;
    User regularUserInOrganization;
    Organization inActiveTestOrganization;
    Organization activeTestOrganization;
    
    @Before
    public void init() {
        owner = user("owner User").withRole(UserRole.OWNER).build();
        organizationRepresentativeMember = user("representative member").withRole(UserRole.REPRESENTATIVE).build();
        regularUserInOrganization = user("regular user within organization").withRole(UserRole.REGULAR).build();
        
        inActiveTestOrganization = organization("test organization").inActive().ownedBy(owner).withMembers(organizationRepresentativeMember).build();
        activeTestOrganization = organization("test organization").active().ownedBy(owner).withMembers(organizationRepresentativeMember, regularUserInOrganization).build();
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void userCannotCreateDocumentWhenOrganisationIsInActive() throws UnauthorizedDocumentCreationException {
        organizationService.addNewDocumentByUser(inActiveTestOrganization, organizationRepresentativeMember);
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void regularUserCannotCreateDocument() throws UnauthorizedDocumentCreationException {
        organizationService.addNewDocumentByUser(activeTestOrganization, regularUserInOrganization);
    }
    
    @Test
    public void representativeUserAddsNewDocument() throws UnauthorizedDocumentCreationException {
        organizationService.addNewDocumentByUser(activeTestOrganization, organizationRepresentativeMember);
        
        assertThat(activeTestOrganization).hasNumOfDocuments(1);
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void userOutsideOfOrganisationCannotCreateNewDocument() throws UnauthorizedDocumentCreationException {
        User userNotInOrganisation = user("representative member").withRole(UserRole.REPRESENTATIVE).build();
        
        organizationService.addNewDocumentByUser(activeTestOrganization, userNotInOrganisation);
    }
    
    @Test
    public void userCreatesDocumentWithStatusUnconfirmed() throws UnauthorizedDocumentCreationException {
        organizationService.addNewDocumentByUser(activeTestOrganization, organizationRepresentativeMember);
        
        assertThat(activeTestOrganization).hasNumOfDocuments(1);
        assertThat(activeTestOrganization.getDocuments().get(0)).hasStatus(DocumentStatus.UNCONFIRMED);
    }

}
