package com.hgc.shortenerbackend.service;

import java.util.Optional;

import com.hgc.shortenerbackend.entity.Link;

public interface LinkService {

	String desencriptar(String texto);

	String acortarYGuardarLink(String linkOriginal);

	String buscarLinkDesencriptado(String linkAcortado);

	byte[] generateQRCode(String qrGenerate);

	void actualizarLinkConQRCode(String linkAcortado, byte[] qrCodeBytes);

	byte[] generarYActualizarQRCode(String qrCodeImage, String baseUrl);


	String encriptar(String texto);
	
	Optional<Link> existeQR(String linkAcortado);
}
