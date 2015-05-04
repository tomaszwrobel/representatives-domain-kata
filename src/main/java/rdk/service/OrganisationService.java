package rdk.service;

import org.springframework.stereotype.Service;

import rdk.model.Organisation;
import rdk.model.User;

@Service
public class OrganisationService {

    public Organisation createNewOrganisation(String string, User user) {
        return new Organisation();
    }

    public void requestForActivation(Organisation newOrganisation, User user) {
    }

}
