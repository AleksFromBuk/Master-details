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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>DocumentService управляет сущностью Document (Master)
 * и связанными DocumentDetail (Detail) с инкрементным пересчётом totalSum.</p>
 *
 * <p>Особенности:</p>
 * <ul>
 *   <li>Проверка уникальности номера документа .</li>
 *   <li>Логирование ошибок в отдельной транзакции.</li>
 *   <li>Инкрементный пересчёт суммы при добавлении/удалении/обновлении деталей.</li>
 *   <li>При обновлении документа, если изменился набор деталей, разница сумм вычисляется
 *       путём сопоставления старых и новых деталей.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ErrorLogServiceImpl errorLogService;

    /**
     * Создаёт новый документ. Если номер дублируется, бросает исключение
     * и логирует ошибку в отдельной транзакции.
     *
     * @param doc документ
     * @return сохранённый документ
     */
    @Override
    @Transactional
    public Document addDocument(Document doc) {
        if (documentRepository.existsByDocNumber(doc.getDocNumber())) {
            String message = "Document with number " + doc.getDocNumber() + " already exists";
            errorLogService.logError(ErrorType.DOC_NUMBER_DUPLICATE.getMessage(), message);
            throw new RuntimeException(message);
        }
        if (doc.getDocDate() == null) {
            doc.setDocDate(LocalDateTime.now());
        }
        BigDecimal totalSum = BigDecimal.ZERO;
        if (doc.getDetails() != null) {
            for (DocumentDetail detail : doc.getDetails()) {
                detail.setDocument(doc);
                totalSum = totalSum.add(getSafeValue(detail.getItemSum()));
            }
        } else {
            doc.setDetails(new ArrayList<>());
        }
        doc.setTotalSum(totalSum);
        return documentRepository.save(doc);
    }

    /**
     * <p>Обновляет существующий документ: проверяет уникальность номера,
     * обновляет поля, а также выполняет инкрементные операции над списком деталей:
     * добавляет новые, обновляет старые, удаляет отсутствующие в updatedDoc.</p>
     *
     * <p>При этом totalSum пересчитывается путём вычисления разницы между
     * старыми и новыми деталями (добавленные + удалённые + изменённые).</p>
     *
     * @param id              идентификатор существующего документа
     * @param updatedDocument документ с новыми данными (номер, дата, note, список деталей)
     * @return документ после обновления
     */
    @Override
    @Transactional
    public Document updateDocument(Long id, Document updatedDocument) {
        Document existing = findDocOrElseThrowException(id);
        if (!existing.getDocNumber().equals(updatedDocument.getDocNumber())) {
            if (documentRepository.existsByDocNumber(updatedDocument.getDocNumber())) {
                String err = "Document with number " + updatedDocument.getDocNumber() + " already exists";
                errorLogService.logError(ErrorType.DOC_NUMBER_DUPLICATE.getMessage(), err);
                throw new RuntimeException(err);
            }
            existing.setDocNumber(updatedDocument.getDocNumber());
        }
        existing.setDocDate(updatedDocument.getDocDate() != null ? updatedDocument.getDocDate() : LocalDateTime.now());
        existing.setNotes(updatedDocument.getNotes());
        if (updatedDocument.getDetails() != null) {
            existing.getDetails().clear();
            BigDecimal sum = BigDecimal.ZERO;
            for (DocumentDetail detail : updatedDocument.getDetails()) {
                detail.setDocument(existing);
                existing.getDetails().add(detail);
                if (detail.getItemSum() != null) {
                    sum = sum.add(detail.getItemSum());
                }
            }
            existing.setTotalSum(sum);
        }
        return documentRepository.save(existing);
    }

    /**
     * <p>Удаляет документ, если он существует. В противном случае — логируется ошибка и бросается исключение.</p>
     *
     * @param id идентификатор документа
     */
    @Override
    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            String err = "Cannot delete. Document not found: " + id;
            errorLogService.logError(ErrorType.DOC_NOT_FOUND.getMessage(), err);
            throw new RuntimeException(err);
        }
        documentRepository.deleteById(id);
    }

    /**
     * <p>Добавляет одну новую деталь к документу, пересчитывая totalSum инкрементно.</p>
     *
     * @param docId  идентификатор документа
     * @param detail новая деталь
     * @return добавленная деталь
     */
    @Transactional
    public DocumentDetail addDetail(Long docId, DocumentDetail detail) {
        Document existing = findDocOrElseThrowException(docId);
        detail.setDocument(existing);
        existing.getDetails().add(detail);
        BigDecimal currentValue = getSafeValue(existing.getTotalSum());
        BigDecimal additionalValue = getSafeValue(detail.getItemSum());
        existing.setTotalSum(currentValue.add(additionalValue));
        documentRepository.save(existing);
        log.info("detail with id added: {}", detail.getId());
        return detail;
    }

    /**
     * <p>Удаляет деталь из документа по её идентификатору, пересчитывая totalSum.</p>
     *
     * @param docId    идентификатор документа
     * @param detailId идентификатор детали
     */
    @Transactional
    public void removeDetail(Long docId, Long detailId) {
        Document existing = findDocOrElseThrowException(docId);
        DocumentDetail toRemove = existing.getDetails().stream()
                .filter(d -> Objects.equals(d.getId(), detailId))
                .findFirst()
                .orElseThrow(() -> {
                    String message = "Detail not found: " + detailId;
                    errorLogService.logError(ErrorType.DETAIL_NOT_FOUND.getMessage(), message);
                    return new RuntimeException(message);
                });
        BigDecimal oldValue = getSafeValue(existing.getTotalSum());
        BigDecimal newValue = oldValue.subtract(
                getSafeValue(toRemove.getItemSum())
        );
        existing.setTotalSum(newValue);
        existing.getDetails().remove(toRemove);
        documentRepository.save(existing);
        log.info("detail with id removed: {}", detailId);
    }

    /**
     * <p>Обновляет существующую деталь, изменяя её поля и
     * корректируя totalSum документа на разницу между старой и новой суммой детали.</p>
     *
     * @param docId     идентификатор документа
     * @param detailId  идентификатор детали
     * @param newDetail данные, которые нужно применить
     * @return обновлённая деталь
     */
    @Transactional
    public DocumentDetail updateDetail(Long docId, Long detailId, DocumentDetail newDetail) {
        Document document = findDocOrElseThrowException(docId);
        DocumentDetail existingDetail = document.getDetails().stream()
                .filter(d -> Objects.equals(d.getId(), detailId))
                .findFirst()
                .orElseThrow(() -> {
                    String err = "Detail not found: " + detailId;
                    errorLogService.logError(ErrorType.DETAIL_NOT_FOUND.getMessage(), err);
                    return new RuntimeException(err);
                });
        BigDecimal oldValue = getSafeValue(existingDetail.getItemSum());
        BigDecimal updatedValue = getSafeValue(newDetail.getItemSum());
        existingDetail.setItemName(newDetail.getItemName());
        existingDetail.setItemSum(updatedValue);
        BigDecimal currentValue = getSafeValue(document.getTotalSum());
        BigDecimal diff = updatedValue.subtract(oldValue);
        document.setTotalSum(currentValue.add(diff));
        documentRepository.save(document);
        log.info("detail with id updated: {}", detailId);
        return existingDetail;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDetail findDetail(Long docId, Long detailId) {
        Document document = findDocOrElseThrowException(docId);
        return document.getDetails().stream()
                .filter(d -> Objects.equals(d.getId(), detailId))
                .findFirst().orElseThrow(() -> {
                    String err = "Detail not found: " + detailId;
                    errorLogService.logError(ErrorType.DETAIL_NOT_FOUND.getMessage(), err);
                    return new RuntimeException(err);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findAllDocuments() {
        return documentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Document findWithDetailsById(Long id) {
        return findDocOrElseThrowException(id);
    }

    private Document findDocOrElseThrowException(Long docId) {
        return documentRepository.findWithDetailsById(docId).orElseThrow(() -> {
            String err = "Document not found: " + docId;
            errorLogService.logError(ErrorType.DOC_NOT_FOUND.getMessage(), err);
            return new RuntimeException(err);
        });
    }

    private BigDecimal getSafeValue(BigDecimal itemSum) {
        return itemSum != null ? itemSum : BigDecimal.ZERO;
    }
}