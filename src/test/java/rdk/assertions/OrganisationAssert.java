package rdk.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import rdk.model.Organization;


public class OrganisationAssert extends AbstractAssert<OrganisationAssert, Organization> {

    protected OrganisationAssert(Organization actual) {
        super(actual, OrganisationAssert.class);
    }

    public static OrganisationAssert assertThat(Organization actual) {
        return new OrganisationAssert(actual);
    }
    
    public OrganisationAssert hasNumOfDocuments(int num) {
        isNotNull();
        Assertions.assertThat(actual.getDocuments().size()).isEqualTo(num);
        return this;
    }
}
