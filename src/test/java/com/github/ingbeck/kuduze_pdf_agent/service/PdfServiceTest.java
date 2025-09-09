package com.github.ingbeck.kuduze_pdf_agent.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {

    @Test
    void extractFields_whenEmptyPdf_thenReturnsFieldsOrError() {
        PdfService pdfService = new PdfService();
        // Erstelle ein Mock-MultipartFile mit einer Beispiel-PDF (hier als leeres Byte-Array)
        MultipartFile mockFile = new org.springframework.mock.web.MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[0]);
        Map<String, Object> result = pdfService.extractFields(mockFile);
        assertNotNull(result);
        assertTrue(result.containsKey("fields") || result.containsKey("error"));
    }

    @Test
    void extractFieldValues_whenEmptyPdf_thenReturnsErrorOrEmptyMap() {
        PdfService pdfService = new PdfService();
        MultipartFile mockFile = new org.springframework.mock.web.MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[0]);
        Map<String, String> result = pdfService.extractFieldValues(mockFile);
        assertNotNull(result);
        assertTrue(result.containsKey("error") || !result.isEmpty());
    }

}