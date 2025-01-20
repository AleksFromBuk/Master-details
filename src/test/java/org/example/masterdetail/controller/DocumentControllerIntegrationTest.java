package org.example.masterdetail.controller;

import org.example.masterdetail.model.Document;
import org.example.masterdetail.model.DocumentDetail;
import org.example.masterdetail.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
    }

    @Test
    void testListDocuments() throws Exception {
        Document doc = Document.builder()
                .docNumber("DOC-LIST")
                .docDate(LocalDateTime.now())
                .totalSum(BigDecimal.valueOf(0))
                .build();
        documentRepository.save(doc);

        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("document-list"))
                .andExpect(model().attributeExists("documents"));
    }

    @Test
    void testCreateDocument() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("docNumber", "DOC-CTRL")
                        .param("docDate", "2023-10-05T10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));
    }

    @Test
    public void testAddDocument() throws Exception {
        mockMvc.perform(post("/documents")
                        .param("docNumber", "DOC-123")
                        .param("docDate", "2025-01-01T12:00")
                        .param("notes", "Test Document")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        assertEquals(1, documentRepository.count());
        Document savedDocument = documentRepository.findAll().get(0);
        assertEquals("DOC-123", savedDocument.getDocNumber());
        assertEquals("Test Document", savedDocument.getNotes());
    }

    @Test
    public void testDeleteDocument() throws Exception {
        Document document = Document.builder()
                .docNumber("DOC-DELETE")
                .docDate(LocalDateTime.now())
                .notes("To be deleted")
                .totalSum(BigDecimal.ZERO)
                .build();
        documentRepository.save(document);
        mockMvc.perform(post("/documents/" + document.getId() + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        assertEquals(0, documentRepository.count());
    }

    @Test
    public void testEditDocument() throws Exception {
        Document document = Document.builder()
                .docNumber("DOC-DELETE")
                .docDate(LocalDateTime.now())
                .notes("To be edit")
                .totalSum(BigDecimal.ZERO)
                .build();
        Document documentEdited = documentRepository.save(document);

        assertEquals(document.getId(), documentEdited.getId());

        mockMvc.perform(post("/documents/" + document.getId())
                        .param("docNumber", "DOC-EDITED")
                        .param("docDate", "2025-02-01T12:00")
                        .param("notes", "Edited Document")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        Document updatedDocument = documentRepository.findWithDetailsById(document.getId()).orElseThrow();
        assertEquals("DOC-EDITED", updatedDocument.getDocNumber());
        assertEquals("Edited Document", updatedDocument.getNotes());
    }

    @Test
    public void testAddDetail() throws Exception {
        Document document = Document.builder()
                .docNumber("DOC-WITH-DETAIL")
                .docDate(LocalDateTime.now())
                .notes("With details")
                .totalSum(BigDecimal.ZERO)
                .build();
        documentRepository.save(document);

        mockMvc.perform(post("/documents/" + document.getId() + "/details")
                        .param("itemName", "Test Item")
                        .param("itemSum", "100.00")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        Document updatedDocument = documentRepository.findWithDetailsById(document.getId()).orElseThrow();

        assertEquals(1, updatedDocument.getDetails().size());
        DocumentDetail detail = updatedDocument.getDetails().get(0);
        assertEquals("Test Item", detail.getItemName());
        assertEquals(new BigDecimal("100.00"), detail.getItemSum());
    }

    @Test
    public void testDeleteDetail() throws Exception {
        Document document = Document.builder()
                        .docNumber("DOC-WITH-DETAIL-DELETE")
                .docDate(LocalDateTime.now())
                .notes("With details to delete")
                .totalSum(BigDecimal.ZERO)
                .details(new ArrayList<>())
                .build();

        DocumentDetail detail = new DocumentDetail();
        detail.setItemName("Detail to delete");
        detail.setItemSum(new BigDecimal("50.00"));
        detail.setDocument(document);

        document.getDetails().add(detail);
        documentRepository.save(document);

        mockMvc.perform(post("/documents/" + document.getId() + "/details/" + detail.getId() + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"));

        Document updatedDocument = documentRepository.findWithDetailsById(document.getId()).orElseThrow();
        assertEquals(0, updatedDocument.getDetails().size());
    }
}