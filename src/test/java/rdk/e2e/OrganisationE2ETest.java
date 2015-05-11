package rdk.e2e;

import static rdk.model.User.UserBuilder.user;
import static org.assertj.core.api.Assertions.assertThat;
import static rdk.assertions.UserAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rdk.exception.UnauthorizedAccessException;
import rdk.init.ApplicationConfig;
import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganisationService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
public class OrganisationE2ETest {

    private static final String REGULAR_TEST_USER = "regular user";
    
    @Autowired
    OrganisationService organisationService;

    User owner;

    @Before
    public void init() {
        owner = user(REGULAR_TEST_USER).withRole(UserRole.REGULAR).build();
    }

    @Test
    public void createsInactiveOrganisationWithRegularUserAndRequestForActivation() throws UnauthorizedAccessException {
        Organisation newOrganisation = organisationService.createNewOrganisation("nazwa", owner);

        assertThat(newOrganisation.isActive()).isFalse();
        assertThat(owner).hasRole(UserRole.OWNER);
        assertThat(owner).isOwnerOfOrganisation(newOrganisation);

        organisationService.requestForActivation(newOrganisation, owner);

        assertThat(newOrganisation.isActivationAwaiting()).isTrue();
    }
    
    @Test
    public void createsNewInActiveOrganisationAndAddMember() {
        User newMember = user("newMember").withRole(UserRole.REGULAR).build();
        Organisation newOrganisation = organisationService.createNewOrganisation("nowa organizacja", owner);
        
        assertThat(newOrganisation.isActive()).isFalse();
        assertThat(newMember).hasRole(UserRole.REGULAR);
        
        organisationService.addMember(newOrganisation, owner, newMember);
        
        assertThat(newMember).hasRole(UserRole.REPRESENTATIVE);
        assertThat(newMember).isInOrganisation(newOrganisation);
    }
}
