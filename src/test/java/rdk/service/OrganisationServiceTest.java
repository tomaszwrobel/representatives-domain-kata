package rdk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static rdk.model.User.UserBuilder.user;
import static rdk.model.Organisation.OrganisationBuilder.organisation;

import org.junit.Before;
import org.junit.Test;

import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;


public class OrganisationServiceTest {

    private static final String USER_NAME = "someUser";

    OrganisationService organisationService = new OrganisationService();

    User someUser;

    @Before
    public void init() {
        someUser = user(USER_NAME).withRole(UserRole.REGULAR).build();
    }

    @Test
    public void userBecomeOwnerAfterOrganisationCreation() {
        Organisation organisation = organisationService.createNewOrganisation("some organisation", someUser);

        assertThat(someUser.getRole()).isEqualTo(UserRole.OWNER);
        assertThat(organisation.getOwner().getName()).isEqualTo(USER_NAME);
    }

    @Test
    public void createsInactiveOrganization() {
        Organisation organisation = organisationService.createNewOrganisation("name", someUser);

        assertThat(organisation.isActive()).isFalse();
    }

    @Test
    public void requestsForOrganisationActivationByRegularUser() {
        Organisation organisation = organisationService.createNewOrganisation("name", someUser);

        assertThat(organisation.isActivationAwaiting()).isFalse();

        organisationService.requestForActivation(organisation, someUser);

        assertThat(organisation.isActivationAwaiting()).isFalse();
    }

    @Test
    public void ownerRequestsForActivation() {
        User organisationOwner = user("user").withRole(UserRole.OWNER).build();
        Organisation newOrganisation = organisation("name").ownedBy(organisationOwner).build();

        organisationService.requestForActivation(newOrganisation, organisationOwner);

        assertThat(newOrganisation.isActivationAwaiting()).isTrue();
    }
}
