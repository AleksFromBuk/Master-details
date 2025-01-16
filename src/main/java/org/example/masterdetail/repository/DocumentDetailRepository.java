package org.example.masterdetail.repository;

import org.example.masterdetail.model.DocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentDetailRepository extends JpaRepository<DocumentDetail, Long> {
}
