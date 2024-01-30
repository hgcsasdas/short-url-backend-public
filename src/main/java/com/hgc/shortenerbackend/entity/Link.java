package com.hgc.shortenerbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "link_original",  columnDefinition = "TEXT", nullable = false)
    private String linkOriginal;

    @Column(name = "link_acortado", columnDefinition = "TEXT", nullable = false)
    private String linkAcortado;

    @Column(name = "qr_code_image_base64", columnDefinition = "TEXT")
    private String qrCodeImageBase64;

    // Constructores

    public Link() {
    }

    public Link(String linkOriginal, String linkAcortado) {
        this.linkOriginal = linkOriginal;
        this.linkAcortado = linkAcortado;
    }

    public Link(String linkOriginal, String linkAcortado, String qrCodeImageBase64) {
        this.linkOriginal = linkOriginal;
        this.linkAcortado = linkAcortado;
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkOriginal() {
        return linkOriginal;
    }

    public void setLinkOriginal(String linkOriginal) {
        this.linkOriginal = linkOriginal;
    }

    public String getLinkAcortado() {
        return linkAcortado;
    }

    public void setLinkAcortado(String linkAcortado) {
        this.linkAcortado = linkAcortado;
    }

    public String getQrCodeImageBase64() {
        return qrCodeImageBase64;
    }

    public void setQrCodeImageBase64(String qrCodeImageBase64) {
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }
}
