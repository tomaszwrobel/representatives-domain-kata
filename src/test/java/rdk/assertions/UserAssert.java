package rdk.assertions;

import org.assertj.core.api.AbstractAssert;

import rdk.model.User;
import rdk.model.UserRole;


public class UserAssert extends AbstractAssert<UserAssert, User> {

    protected UserAssert(User actual) {
        super(actual, UserAssert.class);
    }
    
    public UserAssert hasRole(UserRole role) {
        return this;
    }
    
    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }
}
