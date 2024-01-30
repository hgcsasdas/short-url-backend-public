// LinkController.java
package com.hgc.shortenerbackend.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hgc.shortenerbackend.entity.dto.AcortarRequest;
import com.hgc.shortenerbackend.entity.dto.QrGenerateRequest;
import com.hgc.shortenerbackend.service.LinkService;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/links")
public class LinkController {

	@Autowired
	private LinkService linkService;

	@PostMapping("/acortar")
	public ResponseEntity<String> acortarLink(@RequestBody AcortarRequest acortarRequest) {
		// Acorta y guarda el enlace, y devuelve el enlace acortado y su QR code
		String linkAcortado = linkService.acortarYGuardarLink(acortarRequest.getLinkOriginal());

		String jsonResponse = "{\"linkAcortado\":\"" + linkAcortado + "\"}";

		return ResponseEntity.ok(jsonResponse);
	}

	@PostMapping("/qrGenerate")
	public ResponseEntity<String> generarQR(@RequestBody QrGenerateRequest qrGenerateRequest) {
		String linkToQr= qrGenerateRequest.getLinkToQr();
		String baseUrl= qrGenerateRequest.getBaseUrl();
				
		byte[] qrCodeBytes = linkService.generarYActualizarQRCode(linkToQr, baseUrl);
		if (qrCodeBytes == null) {
			// Manejar el caso en que no se genera el código QR
			return ResponseEntity.badRequest().body("Error al generar el código QR.");
		}
		// Convierte el byte array del QR code a Base64
		String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
		// Construye la respuesta JSON
		String jsonResponse = "{\"qrCode\":\"" + qrCodeBase64 + "\"}";
		return ResponseEntity.ok(jsonResponse);
	}

	@GetMapping("/{linkAcortado}")
	public ResponseEntity<String> redirigirLink(@PathVariable String linkAcortado) {
		// Busca el enlace desencriptado correspondiente en la base de datos
		String linkDesencriptado = linkService.buscarLinkDesencriptado(linkAcortado);
		
		String jsonResponse = "{\"redirectToLink\":\"" + linkDesencriptado + "\"}";

		// Devuelve el enlace desencriptado
		return ResponseEntity.ok(jsonResponse);
	}
}
