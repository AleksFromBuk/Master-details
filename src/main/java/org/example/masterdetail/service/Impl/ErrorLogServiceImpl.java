package org.example.masterdetail.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterdetail.model.ErrorLog;
import org.example.masterdetail.repository.ErrorLogRepository;
import org.example.masterdetail.service.ErrorLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogServiceImpl implements ErrorLogService {
    private final ErrorLogRepository errorLogRepository;

    @Override
    public void logError(String message, String errorType) {
        ErrorLog record = ErrorLog.builder()
                .message(message)
                .errorType(errorType)
                .errorTime(LocalDateTime.now())
                .build();
        errorLogRepository.save(record);
        log.debug(message, errorType, LocalDateTime.now());
    }
}
