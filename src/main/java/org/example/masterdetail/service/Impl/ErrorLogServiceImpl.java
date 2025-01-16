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
    public void logError(String errorType, String message) {
        log.debug(message, errorType, LocalDateTime.now());
        ErrorLog log = new ErrorLog();
        log.setErrorTime(LocalDateTime.now());
        log.setErrorType(errorType);
        log.setMessage(message);
        errorLogRepository.save(log);
    }
}
