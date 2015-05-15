package rdk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static rdk.builders.OrganizationBuilder.organization;
import static rdk.assertions.OrganisationAssert.assertThat;
import static rdk.assertions.UserAssert.assertThat;
import static rdk.model.User.UserBuilder.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rdk.exception.UnauthorizedAccessException;
import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.Document;
import rdk.model.Organization;
import rdk.model.User;
import rdk.model.UserRole;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationServiceTest {

    private static final String USER_NAME = "someUser";
    
    @Mock
    DocumentService documentService;

    @InjectMocks
    OrganizationService organisationService = new OrganizationService();

    User someUser;

    @Before
    public void init() {
        someUser = user(USER_NAME).withRole(UserRole.REGULAR).build();
    }

    @Test
    public void userBecomesOwnerAfterOrganisationCreation() {
        Organization organisation = organisationService.createNewOrganisation("some organisation", someUser);

        assertThat(someUser).isOwnerOf(organisation);
    }

    @Test
    public void createsInactiveOrganization() {
        Organization organisation = organisationService.createNewOrganisation("name", someUser);

        assertThat(organisation.isActive()).isFalse();
    }

    @Test(expected = UnauthorizedAccessException.class)
    public void requestsForOrganisationActivationByRegularUser() throws UnauthorizedAccessException {
        User unauthorizedUser = user("unauthorized User").withRole(UserRole.REGULAR).build();
        Organization organisation = organization("name").ownedBy(someUser).build();

        assertThat(organisation.isActivationAwaiting()).isFalse();

        organisationService.requestForActivation(organisation, unauthorizedUser);
    }

    @Test
    public void ownerRequestsForActivation() throws UnauthorizedAccessException {
        User organisationOwner = user("user").withRole(UserRole.OWNER).build();
        Organization newOrganisation = organization("name").ownedBy(organisationOwner).build();

        organisationService.requestForActivation(newOrganisation, organisationOwner);

        assertThat(newOrganisation.isActivationAwaiting()).isTrue();
    }
    
    @Test
    public void ownerAddsNewMemberToInActiveOrganisation() throws UnauthorizedAccessException {
        User organisationOwner = user("ownerUser").withRole(UserRole.OWNER).build();
        Organization organisation = organization("name").ownedBy(organisationOwner).build();
        User newMember = user("new Member").withRole(UserRole.REGULAR).build();
        
        organisationService.addMember(organisation, organisationOwner, newMember);
        
        assertThat(newMember).isInOrganisationMembers(organisation);
        assertThat(newMember).hasRole(UserRole.REGULAR);
    }
    
    @Test(expected=UnauthorizedAccessException.class)
    public void ownerBecomesNotRepresentativeUser() throws UnauthorizedAccessException {
        User organisationOwner = user("ownerUser").withRole(UserRole.OWNER).build();
        Organization organisation = organization("name").ownedBy(organisationOwner).build();
        
        organisationService.addMember(organisation, organisationOwner, organisationOwner);
    }
    
    @Test(expected = UnauthorizedAccessException.class)
    public void addsNewMemberByRegularUser() throws UnauthorizedAccessException {
        User unauthorizedUser = user("unauthorized User").withRole(UserRole.REGULAR).build();
        User newMember = user("new Member").withRole(UserRole.REGULAR).build();
        Organization organisation = organization("name").ownedBy(someUser).build();
        
        organisationService.addMember(organisation, unauthorizedUser, newMember);
    }
    
    @Test
    public void promotesUser() throws UnauthorizedAccessException {
        User promotor = user("representative user").withRole(UserRole.REPRESENTATIVE).build();
        User newMember = user("new user").withRole(UserRole.REGULAR).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(promotor, newMember).active().build();
        
        organisationService.promoteMemberBy(organisation, newMember, promotor);

        assertThat(newMember).hasBeenPromotedBy(promotor);
        assertThat(newMember).hasNumberOfAcknowledgments(1);
    }

    @Test(expected=UnauthorizedAccessException.class)
    public void regularUserPromoteAnotherUser() throws UnauthorizedAccessException {
        User regularUser = user("someUser").withRole(UserRole.REGULAR).build();
        User newMember = user("new user").withRole(UserRole.REGULAR).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(regularUser, newMember).active().build();

        organisationService.promoteMemberBy(organisation, someUser, regularUser);
    }

    @Test(expected=UnauthorizedAccessException.class)
    public void userFromOutsideOfOrganisationPromotesNewMember() throws UnauthorizedAccessException {
        User userFromDifferentOrganisation = user("user from different organisation").withRole(UserRole.REPRESENTATIVE).build();
        User newMember = user("new user").withRole(UserRole.REGULAR).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(newMember).active().build();

        organisationService.promoteMemberBy(organisation, newMember, userFromDifferentOrganisation);
    }
    
    @Test
    public void ownerCancelsUserRepresentativeRole() throws UnauthorizedAccessException {
        User newMember = user("new user").withRole(UserRole.REPRESENTATIVE).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(newMember).active().build();
        
        organisationService.cancelMemberRepresentativeRole(organisation, newMember, someUser);
        
        assertThat(newMember).hasRole(UserRole.REGULAR);
        assertThat(newMember).isInOrganisationMembers(organisation);
    }
    
    @Test(expected=UnauthorizedAccessException.class)
    public void regularUserCancelsRepresentativeRole() throws UnauthorizedAccessException {
        User regularUser = user("some regular user").withRole(UserRole.REGULAR).build();
        User newMember = user("new user").withRole(UserRole.REPRESENTATIVE).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(newMember).active().build();
        
        organisationService.cancelMemberRepresentativeRole(organisation, newMember, regularUser);
    }
    
    @Test(expected=UnauthorizedDocumentCreationException.class)
    public void documentCannotBeCreatedWhenOrganisationIsInActive() throws UnauthorizedDocumentCreationException {
        User newMember = user("new user").withRole(UserRole.REPRESENTATIVE).build();
        Organization inActiveOrganisation = organization("name").ownedBy(someUser).inActive().withMembers(newMember).build();
        
        organisationService.addNewDocumentByUser(inActiveOrganisation, newMember);
    }
    
    @Test
    public void addsDocumentToOrganisation() throws UnauthorizedDocumentCreationException {
        User newMember = user("new user").withRole(UserRole.REPRESENTATIVE).build();
        Organization organisation = organization("name").ownedBy(someUser).withMembers(newMember).active().build();
        
        when(documentService.createDocumentByUser(newMember)).thenReturn(new Document(newMember));
        
        organisationService.addNewDocumentByUser(organisation, newMember);
        
        assertThat(organisation).hasNumOfDocuments(1);
    }
}
