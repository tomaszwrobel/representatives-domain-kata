package rdk.model;

import java.time.LocalDateTime;


public class Document {
    
    private User creator;
    
    private LocalDateTime created;
    
    private DocumentStatus status = DocumentStatus.UNCONFIRMED;

    public Document(User creator) {
        this.creator = creator;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public LocalDateTime getCreated() {
        return created;
    }
    
    public DocumentStatus getStatus() {
        return status;
    }
}
