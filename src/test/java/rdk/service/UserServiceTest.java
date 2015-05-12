package rdk.service;

import static rdk.assertions.UserAssert.assertThat;
import static rdk.model.Organisation.OrganisationBuilder.organisation;
import static rdk.model.User.UserBuilder.user;

import org.junit.Before;
import org.junit.Test;

import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;


public class UserServiceTest {

    UserService userService = new UserService();

    User testUser;
    Organisation someOrganisation;

    @Before
    public void init() {
        testUser = user("test user").withRole(UserRole.REGULAR).build();
        someOrganisation = organisation("some organisation").withMembers(testUser).build();
    }

    @Test
    public void promotesUser() {
        User promotor = user("representative user").withRole(UserRole.REPRESENTATIVE).build();

        userService.promoteUserBy(testUser, promotor);

        assertThat(testUser).hasBeenPromotedBy(promotor);
        assertThat(testUser).hasNumberOfAcknowledgments(1);
    }

    @Test
    public void regularUserCannotPromoteAnotherUser() {
        User regularUser = user("someUser").withRole(UserRole.REGULAR).build();

        userService.promoteUserBy(testUser, regularUser);

        assertThat(testUser).hasNumberOfAcknowledgments(0);
    }

    @Test
    public void userCannotPromoteUserFromAnotherOrganisation() {
        User userFromDifferentOrganisation = user("user from different organisation").withRole(UserRole.REPRESENTATIVE)
                .inOrganisation(organisation("another organisation").build()).build();

        userService.promoteUserBy(testUser, userFromDifferentOrganisation);

        assertThat(testUser).hasNumberOfAcknowledgments(0);
    }

}
