package org.example.masterdetail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "documents")
@Builder
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
    private LocalDateTime docDate;

    @Column(name = "total_sum", nullable = false)
    private BigDecimal totalSum = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentDetail> details = new ArrayList<>();
}
