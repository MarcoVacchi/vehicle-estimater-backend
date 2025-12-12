package org.lessons.vehicles.java.pdf.controller;

import org.lessons.vehicles.java.pdf.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class PdfRestController {

    @Autowired
    private PdfService pdfService;

    /**
     * Endpoint per scaricare il PDF di prova.
     * Risponde su GET /api/documents/test-pdf
     * 
     * @return ResponseEntity contenente i byte del PDF.
     */
    @GetMapping("/test-pdf")
    public ResponseEntity<byte[]> downloadTestPdf() {

        // 1. Ottieni il PDF come array di byte dal Service
        byte[] pdfBytes = pdfService.generateTestPdf();
        String filename = "documento_prova_iText7.pdf";

        // 2. Imposta gli Headers HTTP
        HttpHeaders headers = new HttpHeaders();

        // Content-Type: indica al client il tipo di file (application/pdf)
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Content-Disposition: indica al client di trattare la risposta come un
        // allegato (download)
        headers.setContentDispositionFormData(filename, filename);

        // Content-Length: imposta la dimensione del file
        headers.setContentLength(pdfBytes.length);

        // 3. Ritorna la risposta con status 200 OK, gli headers e il corpo (i byte del
        // PDF)
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}