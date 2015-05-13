package rdk.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import rdk.model.Organisation;


public class OrganisationAssert extends AbstractAssert<OrganisationAssert, Organisation> {

    protected OrganisationAssert(Organisation actual) {
        super(actual, OrganisationAssert.class);
    }

    public static OrganisationAssert assertThat(Organisation actual) {
        return new OrganisationAssert(actual);
    }
    
    public OrganisationAssert hasNumOfDocuments(int num) {
        isNotNull();
        Assertions.assertThat(actual.getDocuments().size()).isEqualTo(num);
        return this;
    }
}
