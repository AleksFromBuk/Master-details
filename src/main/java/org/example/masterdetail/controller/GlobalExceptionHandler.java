package org.example.masterdetail.controller;

import org.example.masterdetail.enums.ErrorType;
import org.example.masterdetail.errors.CustomValidationException;
import org.example.masterdetail.service.ErrorLogService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final ErrorLogService errorLogService;
    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception ex, Model model) {

        String errorMsg = "Внутренняя ошибка сервера. Обратитесь к администратору.";
        model.addAttribute("errorMessage", errorMsg);
        errorLogService.logError(ErrorType.GENERAL_ERROR.getMessage(), ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolation(ConstraintViolationException ex, Model model) {
        String errorMsg = "Ошибка при сохранении данных. Пожалуйста, заполните все обязательные поля.";
        model.addAttribute("errorMessage", errorMsg);
        errorLogService.logError(ErrorType.VALIDATION_ERROR.getMessage(), ex.getMessage());
        return "error";
    }

    @ExceptionHandler(CustomValidationException.class)
    public String handleCustomValidationException(CustomValidationException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        errorLogService.logError(ErrorType.VALIDATION_ERROR.getMessage(), "Ошибка валидации: " + ex.getMessage());
        return "error";
    }

}
