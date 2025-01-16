package org.example.masterdetail.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterdetail.enums.ErrorType;
import org.example.masterdetail.model.Document;
import org.example.masterdetail.model.DocumentDetail;
import org.example.masterdetail.repository.DocumentRepository;
import org.example.masterdetail.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ErrorLogServiceImpl errorLogService;

    @Override
    @Transactional
    public Document addDocument(Document doc) {
        if (documentRepository.existsByDocNumber(doc.getDocNumber())) {
            String err = "Document with number " + doc.getDocNumber() + " already exists";
            errorLogService.logError(ErrorType.DOC_NUMBER_DUPLICATE.getMessage(), err);
            throw new RuntimeException(err);
        }
        if (doc.getDetails() != null) {
            extractingAmounts(doc);
        }
        return documentRepository.save(doc);
    }

    @Override
    @Transactional
    public Document update(Long id, Document updatedDocument) {
        Document existing = documentRepository.findById(id).orElseThrow(() -> {
            String err = "Document not found: " + id;
            errorLogService.logError(ErrorType.DOC_NOT_FOUND.getMessage(), err);
            return new RuntimeException(err);
        });

        if (!existing.getDocNumber().equals(updatedDocument.getDocNumber())) {
            if (documentRepository.existsByDocNumber(updatedDocument.getDocNumber())) {
                String err = "Document with number " + updatedDocument.getDocNumber() + " already exists";
                errorLogService.logError(ErrorType.DOC_NUMBER_DUPLICATE.getMessage(), err);
                throw new RuntimeException(err);
            }
        }
        existing.setDocNumber(updatedDocument.getDocNumber());
        existing.setDocDate(updatedDocument.getDocDate());
        existing.setNotes(updatedDocument.getNotes());
        existing.getDetails().clear();
        if (updatedDocument.getDetails() != null) {
            BigDecimal sum = BigDecimal.ZERO;
            for (DocumentDetail documentDetail : updatedDocument.getDetails()) {
                documentDetail.setDocument(existing);
                existing.getDetails().add(documentDetail);
                if (documentDetail.getItemSum() != null) {
                    sum = sum.add(documentDetail.getItemSum());
                }
            }
            existing.setTotalSum(sum);
        }
        return documentRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!documentRepository.existsById(id)) {
            String err = "Cannot delete. Document not found: " + id;
            errorLogService.logError(ErrorType.DOC_NOT_FOUND.getMessage(), err);
            throw new RuntimeException(err);
        }
        documentRepository.deleteById(id);
    }

    private void extractingAmounts(Document doc) {
        BigDecimal sum = BigDecimal.ZERO;
        for (DocumentDetail d : doc.getDetails()) {
            d.setDocument(doc);
            if (d.getItemSum() != null) {
                sum = sum.add(d.getItemSum());
            }
        }
        doc.setTotalSum(sum);
    }
}