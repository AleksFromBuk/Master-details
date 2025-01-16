package org.example.masterdetail.service;

import org.example.masterdetail.model.Document;
import org.example.masterdetail.model.DocumentDetail;

public interface DocumentService {

    Document addDocument(Document doc);

    Document update(Long id, Document updatedDocument);

    void delete(Long id);

    DocumentDetail addDetail(Long docId, DocumentDetail detail);

    void removeDetail(Long docId, Long detailId);

    DocumentDetail updateDetail(Long docId, Long detailId, DocumentDetail newDetail);
}
