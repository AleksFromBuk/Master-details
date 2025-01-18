package org.example.masterdetail.repository;

import jakarta.validation.constraints.NotNull;
import org.example.masterdetail.model.Document;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByDocNumber(@NotNull String docNumber);

    @EntityGraph(attributePaths = "details")
    Optional<Document> findWithDetailsById(Long id);

    boolean existsByDocNumber(@NotNull String docNumber);
}
