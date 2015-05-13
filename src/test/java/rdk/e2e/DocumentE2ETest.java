package rdk.e2e;

import static rdk.model.Organisation.OrganisationBuilder.organisation;
import static rdk.model.User.UserBuilder.user;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rdk.IntegrationTestBase;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganisationService;


public class DocumentE2ETest extends IntegrationTestBase {
    
    @Autowired
    OrganisationService organisationService;
    
    User owner;
    User organisationRepresentativeMember;
    Organisation inActiveTestOrganisation;
    Organisation activeTestOrganisation;
    
    @Before
    public void init() {
        owner = user("owner User").withRole(UserRole.OWNER).build();
        organisationRepresentativeMember = user("representative member").withRole(UserRole.REPRESENTATIVE).build();
        
        inActiveTestOrganisation = organisation("test organisation").inActive().ownedBy(owner).withMembers(organisationRepresentativeMember).build();
        activeTestOrganisation = organisation("test organisation").active().ownedBy(owner).withMembers(organisationRepresentativeMember).build();
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void userCannotCreateDocumentWhenOrganisationIsInActive() {
        organisationService.addNewDocumentByUser(organisationRepresentativeMember);
    }

}
