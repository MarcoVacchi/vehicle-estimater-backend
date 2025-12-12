package org.lessons.vehicles.java.pdf.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documenti")
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeFile;
    private String mimeType;

    private long dimensioneByte;

    @Column(name = "data_creazione", columnDefinition = "DATETIME")
    private LocalDateTime dataCreazione;

    public Pdf() {
    }

    public Pdf(String nomeFile, String mimeType, long dimensioneByte) {
        this.nomeFile = nomeFile;
        this.mimeType = mimeType;
        this.dimensioneByte = dimensioneByte;
        this.dataCreazione = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFile() {
        return this.nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getDimensioneByte() {
        return this.dimensioneByte;
    }

    public void setDimensioneByte(long dimensioneByte) {
        this.dimensioneByte = dimensioneByte;
    }

    public LocalDateTime getDataCreazione() {
        return this.dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

}