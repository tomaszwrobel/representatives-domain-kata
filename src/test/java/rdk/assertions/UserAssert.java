package rdk.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import rdk.model.Organisation;
import rdk.model.User;
import rdk.model.UserRole;


public class UserAssert extends AbstractAssert<UserAssert, User> {

    protected UserAssert(User actual) {
        super(actual, UserAssert.class);
    }
    
    public UserAssert hasRole(UserRole role) {
        isNotNull();
        Assertions.assertThat(actual.getRole()).isEqualTo(role);
        return this;
    }
    
    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    public UserAssert isOwnerOfOrganisation(Organisation newOrganisation) {
        isNotNull();
        Assertions.assertThat(actual.getName()).isEqualTo(newOrganisation.getOwner().getName());
        return this;
    }
    
    public UserAssert isInOrganisation(Organisation newOrganisation) {
        isNotNull();
        Assertions.assertThat(newOrganisation.getMembers()).contains(actual);
        return this;
    }
}
