package rdk.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import rdk.model.Organisation;
import rdk.model.User;

public class OrganisationServiceTest {

    OrganisationService organisationService = new OrganisationService();
    
    @Test
    public void createsInactiveOrganization() {
        Organisation organisation = organisationService.createNewOrganisation("name", new User());
        
        assertThat(organisation.isActive()).isFalse();
    }
}
