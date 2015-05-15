package rdk.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static rdk.assertions.UserAssert.assertThat;
import static rdk.model.User.UserBuilder.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rdk.IntegrationTestBase;
import rdk.exception.UnauthorizedAccessException;
import rdk.model.Organization;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganizationService;



public class OrganisationE2ETest extends IntegrationTestBase {

    private static final String REGULAR_TEST_USER = "regular user";
    private static final int DEFAULT_NUM_OF_ACKNOWLEDGMENTS = 3;
    private static final String ADMIN_USER = "Admin user";
    private static final int DEFAULT_NUM_OF_DOCUMENT_CONFIRMATIONS = 3;
    
    @Autowired
    OrganizationService organizationService;
    
    User owner;
    User admin;
    Organization testOrganization;

    @Before
    public void init() {
        owner = user(REGULAR_TEST_USER).withRole(UserRole.REGULAR).build();
        admin = user(ADMIN_USER).withRole(UserRole.ADMIN).build();
        
        testOrganization = organizationService.createNewOrganisation("nazwa", owner);
    }
    
    @Test
    public void newOrganisationIsInActive() {
        assertThat(testOrganization.isActive()).isFalse();
    }
    
    @Test
    public void userBecomeOwnerOfNewCreatedOrganisation() {
        assertThat(owner).hasRole(UserRole.OWNER);
        assertThat(owner).isOwnerOf(testOrganization);
    }

    @Test
    public void ownerRequestsForActivation() throws UnauthorizedAccessException {

        organizationService.requestForActivation(testOrganization, owner);

        assertThat(testOrganization.isActivationAwaiting()).isTrue();
    }
    
    @Test
    public void addsMemberToNewInActiveOrganisation() throws UnauthorizedAccessException {
        User newMember = user("newMember").withRole(UserRole.REGULAR).build();
        
        assertThat(testOrganization.isActive()).isFalse();
        
        organizationService.addMember(testOrganization, owner, newMember);
        
        assertThat(newMember).isInOrganisationMembers(testOrganization);
    }
    
    @Test
    public void ownerPromoteNewMemberToBeRepresentative() throws UnauthorizedAccessException {
        User newMember = user("newMember").withRole(UserRole.REGULAR).build();
        
        assertThat(testOrganization.isActive()).isFalse();
        
        organizationService.addMember(testOrganization, owner, newMember);
        
        assertThat(newMember).isInOrganisationMembers(testOrganization);
        
        organizationService.promoteMemberBy(testOrganization, newMember, owner);
        
        assertThat(newMember).isRepresentative();
    }
    
    @Test
    public void ownerSetsRequiredNumberOfAcknowledgmentsForRepresentativeUser() throws UnauthorizedAccessException {
        
        organizationService.setNumOfRequiredAcknowledgments(testOrganization, DEFAULT_NUM_OF_ACKNOWLEDGMENTS, owner);
        
//        assertThat(testOrganisation.getNumOfAcknowledgments()).isEqualTo(DEFAULT_NUM_OF_ACKNOWLEDGMENTS);
    }
    
    @Test
    public void organisationIsActivatedByAdmin() throws UnauthorizedAccessException {
        organizationService.activateOrganisation(testOrganization, admin);
        
        assertThat(testOrganization.isActive()).isTrue();
    }
    
    @Test
    public void userGetsProperNumOfAcknowledgmentsAndBecomeRepresentative() throws UnauthorizedAccessException {
        List<User> someRepresentativeMembers = prepareRepresentativeMembers(3);
        
        User newUser = user("new User").withRole(UserRole.REGULAR).build();
        
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(0));
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(1));
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(2));
        organizationService.activateOrganisation(testOrganization, admin);
        organizationService.setNumOfRequiredAcknowledgments(testOrganization, DEFAULT_NUM_OF_ACKNOWLEDGMENTS, owner);
        
        organizationService.addMember(testOrganization, owner, newUser);
        
        organizationService.promoteMemberBy(testOrganization, newUser, someRepresentativeMembers.get(0));
        organizationService.promoteMemberBy(testOrganization, newUser, someRepresentativeMembers.get(1));
        organizationService.promoteMemberBy(testOrganization, newUser, someRepresentativeMembers.get(2));
        
        assertThat(newUser).isInOrganisationMembers(testOrganization);
        assertThat(newUser).hasRole(UserRole.REPRESENTATIVE);
        assertThat(newUser).hasNumberOfAcknowledgments(DEFAULT_NUM_OF_ACKNOWLEDGMENTS);
    }
    
    @Test
    public void userLessNumOfAcknowledgmentsAndNotBecomesRepresentative() throws UnauthorizedAccessException {
        List<User> someRepresentativeMembers = prepareRepresentativeMembers(3);
        
        User newUser = user("new User").withRole(UserRole.REGULAR).build();
        
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(0));
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(1));
        organizationService.addMember(testOrganization, owner, someRepresentativeMembers.get(2));
        organizationService.activateOrganisation(testOrganization, admin);
        organizationService.setNumOfRequiredAcknowledgments(testOrganization, DEFAULT_NUM_OF_ACKNOWLEDGMENTS, owner);
        
        organizationService.addMember(testOrganization, owner, newUser);
        
        organizationService.promoteMemberBy(testOrganization, newUser, someRepresentativeMembers.get(0));
        organizationService.promoteMemberBy(testOrganization, newUser, someRepresentativeMembers.get(1));
        
        assertThat(newUser).isInOrganisationMembers(testOrganization);
        assertThat(newUser).hasRole(UserRole.REGULAR);
        assertThat(newUser).hasNumberOfAcknowledgments(DEFAULT_NUM_OF_ACKNOWLEDGMENTS - 1);
    }
    
    @Test
    public void ownerCancelsMemberRepresentativeRole() throws UnauthorizedAccessException {
        User representativeUser = user("some representative user").withRole(UserRole.REPRESENTATIVE).build();
        
        organizationService.addMember(testOrganization, owner, representativeUser);
        
        organizationService.cancelMemberRepresentativeRole(testOrganization, representativeUser, owner);
        
        assertThat(representativeUser).isInOrganisationMembers(testOrganization);
        assertThat(representativeUser).hasRole(UserRole.REGULAR);
    }
    
    private List<User> prepareRepresentativeMembers(int num) {
        
        List<User> users = new ArrayList<User>();
        
        for (int i = 0; i < num; i++) {
            users.add(user("user " + (i + 1)).withRole(UserRole.REPRESENTATIVE).build());
        }
        
        return users;
        
    }
}
