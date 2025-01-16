package org.example.masterdetail.service;

import org.example.masterdetail.model.Document;

public interface DocumentService {

    Document addDocument(Document doc);

    Document update(Long id, Document updatedDocument);

    void delete(Long id);
}
