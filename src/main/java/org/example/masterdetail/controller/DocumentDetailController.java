package org.example.masterdetail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterdetail.model.DocumentDetail;
import org.example.masterdetail.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Контроллер для операций со спецификациями (Detail).
 */
@Controller
@RequestMapping("/documents/{docId}/details")
@RequiredArgsConstructor
@Slf4j
public class DocumentDetailController {

    private final DocumentService documentService;

    /**
     * Форма для добавления новой спецификации к документу.
     */
    @GetMapping("/new")
    public String newDetailForm(@PathVariable Long docId, Model model) {
        model.addAttribute("detail", new DocumentDetail());
        model.addAttribute("docId", docId);
        return "detail-form";
    }

    /**
     * Обработка submit формы добавления детали.
     */
    @PostMapping
    public String addDetail(@PathVariable Long docId,
                            @Valid @ModelAttribute("detail") DocumentDetail detail,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "detail-form";
        }
        try {
            documentService.addDetail(docId, detail);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "detail-form";
        }
        return "redirect:/documents";
    }

    /**
     * Форма редактирования одной детали.
     */
    @GetMapping("/{detailId}/edit")
    public String editDetailForm(@PathVariable Long docId,
                                 @PathVariable Long detailId,
                                 Model model) {
        DocumentDetail existing = documentService.findDetail(docId, detailId);
        model.addAttribute("detail", existing);
        model.addAttribute("docId", docId);
        return "detail-form";
    }

    /**
     * Обработка submit формы редактирования детали.
     */
    @PostMapping("/{detailId}")
    public String updateDetail(@PathVariable Long docId,
                               @PathVariable Long detailId,
                               @Valid @ModelAttribute("detail") DocumentDetail newDetail,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "detail-form";
        }
        try {
            documentService.updateDetail(docId, detailId, newDetail);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "detail-form";
        }
        return "redirect:/documents";
    }

    /**
     * Удаление одной детали.
     */
    @PostMapping("/{detailId}/delete")
    public String removeDetail(@PathVariable Long docId,
                               @PathVariable Long detailId,
                               Model model) {
        try {
            documentService.removeDetail(docId, detailId);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/documents";
    }
}

