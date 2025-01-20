package org.example.masterdetail.service;

import org.example.masterdetail.model.Document;
import org.example.masterdetail.model.DocumentDetail;

import java.util.List;
import java.util.Optional;

public interface DocumentService {

    Document addDocument(Document doc);

    Document updateDocument(Long id, Document updatedDocument);

    void deleteDocument(Long id);

    List<Document> findAllDocumentsWithDetails();

    DocumentDetail addDetail(Long docId, DocumentDetail detail);

    void removeDetail(Long docId, Long detailId);

    DocumentDetail updateDetail(Long docId, Long detailId, DocumentDetail newDetail);

    DocumentDetail findDetail(Long docId, Long detailId);
}
