package org.example.masterdetail.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_documents_id_seq")
    @SequenceGenerator(name = "documents_documents_id_seq", sequenceName = "documents_documents_id_seq", allocationSize = 1)
    @Column(name = "document_id")
    private Long id;

    @Column(name = "doc_number", nullable = false, unique = true)
    @NotNull
    private String docNumber;

    @Column(name ="doc_date", nullable = false)
    @NotNull
    private LocalDate docDate;

    @Column(name = "total_sum", nullable = false)
    private BigDecimal totalSum = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentDetail> details = new ArrayList<>();
}
