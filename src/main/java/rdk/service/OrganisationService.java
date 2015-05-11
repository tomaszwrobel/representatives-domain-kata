package rdk.service;

import static rdk.model.Organisation.OrganisationBuilder.organisation;

import org.springframework.stereotype.Service;

import rdk.exception.UnauthorizedAccessException;
import rdk.model.Organisation;
import rdk.model.User;


@Service
public class OrganisationService {

    public Organisation createNewOrganisation(String name, User owner) {
        owner.setOwnerRole();
        return organisation(name).ownedBy(owner).build();
    }

    public void requestForActivation(Organisation newOrganisation, User user) throws UnauthorizedAccessException {
        if (newOrganisation.isOwnedBy(user)) {
            newOrganisation.awaitForActivation();
        } else {
            throw new UnauthorizedAccessException("User " + user.getName() + " has no rights for organisation activation");
        }
    }

    public void addMember(Organisation organisation, User owner, User newMember) throws UnauthorizedAccessException {
        if (organisation.assertMemberCanBeAddedBy(owner)) {
            organisation.addMember(newMember);
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rigths for adding new members");
        }
    }
}
