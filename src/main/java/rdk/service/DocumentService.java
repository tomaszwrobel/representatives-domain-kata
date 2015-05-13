package rdk.service;

import org.springframework.stereotype.Service;

import rdk.exception.UnauthorizedDocumentCreationException;
import rdk.model.Document;
import rdk.model.User;
import rdk.model.UserRole;

@Service
public class DocumentService {

    public Document createDocumentByUser(User organisationRepresentativeMember) throws UnauthorizedDocumentCreationException {
        if (organisationRepresentativeMember.getRole() != UserRole.REPRESENTATIVE) {
            throw new UnauthorizedDocumentCreationException("Documents can be created only by representative users");
        }
        return new Document(organisationRepresentativeMember);
    }

}
