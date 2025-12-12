package org.lessons.vehicles.java.pdf.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.lessons.vehicles.java.quoted.dto.QuotedDTO; // Import necessario
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PdfService {

    // METODO ESISTENTE (per il test)
    public byte[] generateTestPdf() {
        // ... (il tuo codice di prova rimane qui)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            document.add(new Paragraph("PDF di Prova Generato con Spring Boot 3 + iText 7")
                    .setFontSize(18).setBold());
            document.add(
                    new Paragraph("Questo documento dimostra la generazione di file PDF in tempo reale dal backend."));
            document.add(new Paragraph("Versione iText: 7"));

        } catch (IOException e) {
            System.err.println("Errore durante la generazione del PDF: " + e.getMessage());
            throw new RuntimeException("Impossibile generare il PDF richiesto.", e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    // NUOVO METODO: Genera il PDF con i dati del preventivo
    public byte[] generateQuotedPdf(QuotedDTO quotedDTO) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            // TITOLO
            document.add(new Paragraph("Preventivo Veicolo")
                    .setFontSize(24).setBold().setUnderline());
            document.add(new Paragraph("\n"));

            // 1. SEZIONE DATI UTENTE
            document.add(new Paragraph("Dati Cliente").setFontSize(16).setBold());
            document.add(new Paragraph("Nome: " + quotedDTO.userName() + " " + quotedDTO.userSurname()));
            document.add(new Paragraph("Email: " + quotedDTO.userMail()));
            document.add(new Paragraph("\n"));

            // 2. SEZIONE DETTAGLI VEICOLO (assumendo sempre 1 veicolo)
            if (quotedDTO.vehicleDTOToQuoted() != null && !quotedDTO.vehicleDTOToQuoted().isEmpty()) {
                document.add(new Paragraph("Dettagli Veicolo").setFontSize(16).setBold());

                // Prende il primo veicolo
                var vehicle = quotedDTO.vehicleDTOToQuoted().get(0);
                document.add(new Paragraph("Marca: " + vehicle.brand()));
                document.add(new Paragraph("Modello: " + vehicle.model()));
                document.add(new Paragraph("Prezzo Base: " + formatCurrency(vehicle.basePrice())));

                document.add(new Paragraph("\n"));
            }

            // 3. SEZIONE OPTIONAL
            document.add(new Paragraph("Optional Selezionati").setFontSize(16).setBold());
            if (quotedDTO.optionalDTOtoQuoted() != null && !quotedDTO.optionalDTOtoQuoted().isEmpty()) {
                Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
                table.addHeaderCell(new Paragraph("ID Optional").setBold());
                table.addHeaderCell(new Paragraph("Prezzo").setBold());

                quotedDTO.optionalDTOtoQuoted().forEach(optional -> {
                    table.addCell(String.valueOf(optional.id()));
                    table.addCell(formatCurrency(optional.price()));
                });
                document.add(table);
            } else {
                document.add(new Paragraph("Nessun optional selezionato."));
            }
            document.add(new Paragraph("\n"));

            // 4. PREZZO FINALE
            document.add(new Paragraph("Prezzo Totale Calcolato:").setFontSize(18).setBold());
            document.add(
                    new Paragraph(formatCurrency(quotedDTO.finalPrice())).setFontSize(22).setBold().setUnderline());

        } catch (IOException e) {
            System.err.println("Errore durante la generazione del PDF del preventivo: " + e.getMessage());
            throw new RuntimeException("Impossibile generare il PDF del preventivo.", e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    // Metodo helper per formattare la valuta
    private String formatCurrency(BigDecimal price) {
        return price != null ? "€ " + price.setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "N/A";
    }
}