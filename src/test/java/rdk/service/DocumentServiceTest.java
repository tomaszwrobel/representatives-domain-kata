package rdk.service;

import static rdk.model.User.UserBuilder.user;

import org.junit.Before;

import rdk.model.User;
import rdk.model.UserRole;


public class DocumentServiceTest {

    private static final String USER_NAME = "test user";

    DocumentService documentService = new DocumentService();
    
    User someUser;

    @Before
    public void init() {
        someUser = user(USER_NAME).withRole(UserRole.REGULAR).build();
    }
    
    
}
