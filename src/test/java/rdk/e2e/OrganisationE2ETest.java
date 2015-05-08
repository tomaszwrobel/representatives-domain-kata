package rdk.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static rdk.assertions.UserAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rdk.init.ApplicationConfig;
import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;
import rdk.service.OrganisationService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
public class OrganisationE2ETest {

    @Autowired
    OrganisationService organisationService;

    User user;

    @Before
    public void init() {
        user = new User();
    }

    @Test
    public void createsInactiveOrganisationWithRegularUserAndRequestForActivation() {
        Organisation newOrganisation = organisationService.createNewOrganisation("nazwa", user);

        assertThat(newOrganisation.isActive()).isFalse();
        assertThat(user).hasRole(UserRole.OWNER);
        assertThat(user).isInOrganisation(newOrganisation);

        organisationService.requestForActivation(newOrganisation, user);

        assertThat(newOrganisation.isActivationAwaiting()).isTrue();
    }
}
