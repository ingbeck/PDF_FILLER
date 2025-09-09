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
    public Map<String, Object> extractFields(@RequestParam("file") MultipartFile file) {
        return pdfService.extractFields(file);
    }

    @PostMapping(value = "/extract-field-values", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> extractFieldValues(@RequestParam("file") MultipartFile file) {
        return pdfService.extractFieldValues(file);
    }

    @PostMapping(value = "/fill-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> fillPdfForm(
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam("data") String jsonData) {

        return pdfService.fillPdfForm(pdfFile, jsonData);

    }
}
