package org.example.masterdetail.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterdetail.model.ErrorLog;
import org.example.masterdetail.repository.ErrorLogRepository;
import org.example.masterdetail.service.ErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogServiceImpl implements ErrorLogService {
    private final ErrorLogRepository errorLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(String message, String errorType) {
        String transactionId = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("Logging error in a separate transaction. Transaction ID: {}", transactionId);
        ErrorLog record = ErrorLog.builder()
                .message(message)
                .errorType(errorType)
                .errorTime(LocalDateTime.now())
                .build();
        errorLogRepository.save(record);
        log.debug(message, errorType, LocalDateTime.now());
    }
}
