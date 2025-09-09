package com.github.ingbeck.kuduze_pdf_agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extrahiert Formularfelder aus einer PDF-Datei.
     * @param file Die PDF-Datei als MultipartFile.
     * @return Eine Map mit den extrahierten Feldern oder einer Fehlermeldung.
     */
    public Map<String, Object> extractFields(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            PDDocument pdf = PDDocument.load(file.getInputStream());
            PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
            if (form == null) {
                result.put("fields", Collections.emptyList());
            } else {
                List<Map<String, String>> fields = form.getFields().stream().map(f -> {
                    Map<String, String> fieldInfo = new HashMap<>();
                    fieldInfo.put("name", f.getFullyQualifiedName());
                    fieldInfo.put("type", f.getClass().getSimpleName());
                    fieldInfo.put("value", f.getValueAsString());
                    return fieldInfo;
                }).toList();
                result.put("fields", fields);
            }
            pdf.close();
        } catch(Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * Extrahiert die Feldnamen und deren Werte aus einer PDF-Datei.
     * @param file Die PDF-Datei als MultipartFile.
     * @return Eine Map mit den Feldnamen und deren Werten.
     */
    public Map<String, String> extractFieldValues(MultipartFile file){

            Map<String, String> fieldValues = new HashMap<>();
            try (PDDocument pdf = PDDocument.load(file.getInputStream())) {
                PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
                if (form != null) {
                    for (PDField field : form.getFields()) {
                        fieldValues.put(field.getFullyQualifiedName(), field.getValueAsString());
                    }
                }
            } catch (Exception e) {
                // Fehlerbehandlung nach Bedarf
            }
            return fieldValues;

    }

    /**
     * Füllt ein PDF-Formular mit den angegebenen Daten.
     * @param pdfFile Die PDF-Datei als MultipartFile.
     * @param jsonData Die JSON-Daten als String.
     * @return Eine ResponseEntity mit dem ausgefüllten PDF oder einer Fehlermeldung.
     */
    public ResponseEntity<byte[]> fillPdfForm(
            MultipartFile pdfFile,
            String jsonData) {
        try {
            // JSON in Map umwandeln
            Map<String, String> fieldValues = objectMapper.readValue(jsonData, Map.class);

            // PDF laden
            PDDocument pdf = PDDocument.load(pdfFile.getInputStream());
            PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();

            if (form == null) {
                pdf.close();
                return ResponseEntity.badRequest()
                        .body("PDF enthält keine ausfüllbaren Formularfelder.".getBytes());
            }

            // Felder befüllen
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                PDField field = form.getField(entry.getKey());
                if (field != null) {
                    field.setValue(entry.getValue());
                }
            }

            // PDF in Byte-Array speichern
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pdf.save(baos);
            pdf.close();

            // Antwort mit PDF-Daten und Headern
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_form.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Fehler: " + e.getMessage()).getBytes());
        }
    }

}
