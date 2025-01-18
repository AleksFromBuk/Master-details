package org.example.masterdetail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.masterdetail.model.Document;
import org.example.masterdetail.service.Impl.DocumentServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.*;

import java.util.ArrayList;

/**
 * Контроллер для операций над Document (Master).
 */
@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentServiceImpl documentService;

    /**
     * Отображает список документов в виде таблицы.
     */
    @GetMapping
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentService.findAllDocuments());
        return "document-list";
    }

    /**
     * Форма создания нового документа (пустой объект).
     */
    @GetMapping("/new")
    public String newDocumentForm(Model model) {
        Document newDocument = new Document();
        newDocument.setDetails(new ArrayList<>()); // Инициализация пустого списка спецификаций
        model.addAttribute("document", newDocument);
        return "document-form";
    }

    /**
     * Обработка submit формы создания документа.
     */
    @PostMapping
    public String addDocument(@Valid @ModelAttribute("document") Document doc,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "document-form";
        }
        try {
            documentService.addDocument(doc);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "document-form";
        }
        return "redirect:/documents";
    }

    /**
     * Форма редактирования существующего документа.
     */
    @GetMapping("/{id}/edit")
    public String editDocumentForm(@PathVariable Long id, Model model) {
        Document existing = documentService.findWithDetailsById(id);
        model.addAttribute("document", existing);
        return "document-form";
    }

    /**
     * Обработка submit формы редактирования документа.
     */
    @PostMapping("/{id}")
    public String updateDocument(@PathVariable Long id,
                                 @Valid @ModelAttribute("document") Document doc,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "document-form";
        }
        try {
            documentService.updateDocument(id, doc);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "document-form";
        }
        return "redirect:/documents";
    }

    /**
     * Удаление документа.
     */
    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long id, Model model) {
        try {
            documentService.deleteDocument(id);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/documents";
    }
}
