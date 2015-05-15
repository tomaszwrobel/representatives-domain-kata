package rdk.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import rdk.model.Organization;
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

    public UserAssert isOwnerOf(Organization newOrganisation) {
        isNotNull();
        Assertions.assertThat(actual.getRole()).isEqualTo(UserRole.OWNER);
        Assertions.assertThat(actual.getName()).isEqualTo(newOrganisation.getOwner().getName());
        return this;
    }
    
    public UserAssert isInOrganisationMembers(Organization newOrganisation) {
        isNotNull();
        Assertions.assertThat(newOrganisation.getMembers()).contains(actual);
        return this;
    }
    
    public UserAssert isNotInOrganisationMembers(Organization organisation) {
        isNotNull();
        Assertions.assertThat(organisation.getMembers()).doesNotContain(actual);
        return this;
    }
    
    public UserAssert hasNumberOfAcknowledgments(int numOfAcknowledgments) {
        isNotNull();
        Assertions.assertThat(actual.getPromoters().size()).isEqualTo(numOfAcknowledgments);
        return this;
    }
    
    public UserAssert hasBeenPromotedBy(User promotor) {
        isNotNull();
        Assertions.assertThat(actual.getPromoters()).contains(promotor);
        return this;
    }

    public UserAssert isRepresentative() {
        isNotNull();
        Assertions.assertThat(actual.getRole()).isEqualTo(UserRole.REPRESENTATIVE);
        return this;
    }
}
