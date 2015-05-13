package rdk.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import rdk.model.Document;
import rdk.model.DocumentStatus;


public class DocumentAssert extends AbstractAssert<DocumentAssert, Document> {

    protected DocumentAssert(Document actual) {
        super(actual, DocumentAssert.class);
    }

    public static DocumentAssert assertThat(Document actual) {
        return new DocumentAssert(actual);
    }
    
    public DocumentAssert hasStatus(DocumentStatus status) {
        isNotNull();
        Assertions.assertThat(actual.getStatus()).isEqualTo(status);
        return this;
    }
}
