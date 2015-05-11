package rdk.service;

import static rdk.model.Organisation.OrganisationBuilder.organisation;

import org.springframework.stereotype.Service;

import rdk.exception.UnauthorizedAccessException;
import rdk.model.Organisation;
import rdk.model.User;


@Service
public class OrganisationService {

    public Organisation createNewOrganisation(String name, User user) {
        user.setOwnerRole();
        return organisation(name).ownedBy(user).build();
    }

    public void requestForActivation(Organisation newOrganisation, User owner) throws UnauthorizedAccessException {
        if (newOrganisation.isOwnedBy(owner)) {
            newOrganisation.awaitForActivation();
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rights for organisation activation");
        }
    }

    public void addMember(Organisation organisation, User owner, User newMember) throws UnauthorizedAccessException {
        if (organisation.assertMemberCanBeAddedBy(owner)) {
            organisation.addMember(newMember);
        } else {
            throw new UnauthorizedAccessException("User " + owner.getName() + " has no rigths for adding new members");
        }
    }

    public void setNumOfRequiredAcknowledgments(Organisation organisation, int defaultNumOfAcknowledgments, User owner) throws UnauthorizedAccessException {
        organisation.setNumOfRequiredAcknowledgments(defaultNumOfAcknowledgments, owner);
    }
}
