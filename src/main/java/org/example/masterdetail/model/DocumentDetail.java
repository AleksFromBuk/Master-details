package org.example.masterdetail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "document_detail")
public class DocumentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_detail_document_detail_id_seq")
    @SequenceGenerator(name = "document_detail_document_detail_id_seq", sequenceName = "document_detail_document_detail_id_seq", allocationSize = 1)
    @Column(name = "detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "item_name")
    @NotNull(message = "Item name cannot be null")
    private String itemName;

    @Column(name = "item_sum")
    @NotNull(message = "Item sum cannot be null")
    private BigDecimal itemSum;

}
