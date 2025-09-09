package com.github.ingbeck.kuduze_pdf_agent.controller;

import com.github.ingbeck.kuduze_pdf_agent.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @PostMapping(value = "/extract-fields", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> extractFields(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "PDF-Datei fehlt oder ist leer."));
        }
        return ResponseEntity.ok(pdfService.extractFields(file));
    }

    @PostMapping(value = "/extract-field-values", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> extractFieldValues(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "PDF-Datei fehlt oder ist leer."));
        }
        return ResponseEntity.ok(pdfService.extractFieldValues(file));
    }

    @PostMapping(value = "/fill-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> fillPdfForm(
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam("data") String jsonData) {

        if (pdfFile == null || pdfFile.isEmpty() || pdfFile.getSize() == 0) {
            return ResponseEntity.badRequest().body("PDF-Datei fehlt oder ist leer.".getBytes());
        }
        if (jsonData == null || jsonData.isBlank()) {
            return ResponseEntity.badRequest().body("JSON-Daten fehlen.".getBytes());
        }
        return pdfService.fillPdfForm(pdfFile, jsonData);
    }
}
