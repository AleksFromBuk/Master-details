package org.example.masterdetail.service;

import org.example.masterdetail.model.Document;
import org.example.masterdetail.model.DocumentDetail;
import org.example.masterdetail.repository.DocumentRepository;
import org.example.masterdetail.service.Impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DocumentServiceIntegrationTest {

    @Autowired
    private DocumentServiceImpl documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
    }

    @Test
    void testAddDocument() {
        Document doc = new Document();
        doc.setDocNumber("DOC-100");
        doc.setDocDate(LocalDateTime.now());

        Document saved = documentService.addDocument(doc);
        assertThat(saved.getId()).isNotNull();
        assertThat(documentRepository.count()).isEqualTo(1);
    }

    @Test
    void testUpdateDocument() {
        Document doc = new Document();
        doc.setDocNumber("DOC-101");
        doc.setDocDate(LocalDateTime.now());
        doc = documentService.addDocument(doc);

        doc.setDocNumber("DOC-UPDATED");
        Document updated = documentService.updateDocument(doc.getId(), doc);

        assertThat(updated.getDocNumber()).isEqualTo("DOC-UPDATED");
    }

    @Test
    void testDeleteDocument() {
        Document doc = new Document();
        doc.setDocNumber("DOC-102");
        doc.setDocDate(LocalDateTime.now());
        doc = documentService.addDocument(doc);

        documentService.deleteDocument(doc.getId());
        assertThat(documentRepository.count()).isEqualTo(0);
    }

    @Test
    void testAddDetail() {
        Document doc = new Document();
        doc.setDocNumber("DOC-200");
        doc.setDocDate(LocalDateTime.now());
        doc = documentService.addDocument(doc);

        DocumentDetail detail = new DocumentDetail();
        detail.setItemName("Item A");
        detail.setItemSum(BigDecimal.valueOf(100));

        documentService.addDetail(doc.getId(), detail);

        Document found = documentRepository.findWithDetailsById(doc.getId()).get();
        assertThat(found.getDetails()).hasSize(1);
        assertThat(found.getTotalSum()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testRemoveDetail() {
        Document doc = Document.builder()
                .docNumber("DOC-201")
                .docDate(LocalDateTime.now())
                .totalSum(BigDecimal.ZERO)
                .build();
        DocumentDetail detail = new DocumentDetail();
        detail.setItemName("Item B");
        detail.setItemSum(BigDecimal.valueOf(200));
        List<DocumentDetail> details = new ArrayList<>();
        details.add(detail);
        doc.setDetails(details);

        doc = documentService.addDocument(doc);
        DocumentDetail savedDetail = doc.getDetails().get(0);

        documentService.removeDetail(doc.getId(), savedDetail.getId());
        Document found = documentRepository.findById(doc.getId()).get();
        assertThat(found.getDetails()).isEmpty();
        assertThat(found.getTotalSum()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testUpdateDetail() {
        Document doc = Document.builder()
                .docNumber("DOC-201")
                .docDate(LocalDateTime.now())
                .totalSum(BigDecimal.ZERO)
                .build();
        DocumentDetail detail = new DocumentDetail();
        detail.setItemName("Item C");
        detail.setItemSum(BigDecimal.valueOf(300));
        List<DocumentDetail> details = new ArrayList<>();
        details.add(detail);
        doc.setDetails(details);

        doc = documentService.addDocument(doc);
        DocumentDetail existing = doc.getDetails().get(0);

        DocumentDetail newDet = new DocumentDetail();
        newDet.setItemName("Item C Updated");
        newDet.setItemSum(BigDecimal.valueOf(500));

        documentService.updateDetail(doc.getId(), existing.getId(), newDet);

        Document found = documentRepository.findById(doc.getId()).get();
        assertThat(found.getDetails()).hasSize(1);
        assertThat(found.getTotalSum()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(found.getDetails().get(0).getItemName()).isEqualTo("Item C Updated");
    }

    @Test
    void testNoNPlusOne() {
        // создаём документ с несколькими деталями
        Document doc = new Document();
        doc.setDocNumber("DOC-300");
        doc.setDocDate(LocalDateTime.now());
        doc.setDetails(List.of(
                new DocumentDetail(null, doc, "Item1", BigDecimal.valueOf(100)),
                new DocumentDetail(null, doc, "Item2", BigDecimal.valueOf(200))
        ));
        doc = documentService.addDocument(doc);

        Document loaded = documentRepository.findById(doc.getId()).get();
        assertThat(loaded.getDetails()).hasSize(2);
    }
}
