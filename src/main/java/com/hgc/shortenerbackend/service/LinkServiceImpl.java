package com.hgc.shortenerbackend.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hgc.shortenerbackend.entity.Link;
import com.hgc.shortenerbackend.repository.LinkRepository;

import jakarta.transaction.Transactional;

@Service
public class LinkServiceImpl implements LinkService {

	// Clave secreta para la encriptación AES

	private final String CLAVE_SECRETA; // 16 bytes
	private final String ALGORITHM;

	public LinkServiceImpl(@Value("${clave.secreta}") String claveSec, @Value("${algoritmo}") String alg) {
		this.CLAVE_SECRETA = claveSec;
		this.ALGORITHM = alg;
	}

	@Autowired
	private LinkRepository linkRepository;

	// Método para encriptar un texto
	@Override
	public String encriptar(String texto) {
		try {
			// Configuración del cifrado AES
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
			SecureRandom random = new SecureRandom();
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			// Inicialización del cifrado
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

			// Encriptación del texto
			byte[] textoEncriptado = cipher.doFinal(texto.getBytes());
			byte[] textoEncriptadoConIv = new byte[iv.length + textoEncriptado.length];
			System.arraycopy(iv, 0, textoEncriptadoConIv, 0, iv.length);
			System.arraycopy(textoEncriptado, 0, textoEncriptadoConIv, iv.length, textoEncriptado.length);

			// Codificación Base64 y retorno del resultado
			return Base64.getUrlEncoder().encodeToString(textoEncriptadoConIv);
		} catch (Exception e) {
			// Manejo de erroresç
			throw new RuntimeException("Error al encriptar el texto", e);
		}
	}

	// Método para desencriptar un texto
	@Override
	public String desencriptar(String textoEncriptadoConIv) {
		try {
			// Decodificación Base64
			byte[] data = Base64.getUrlDecoder().decode(textoEncriptadoConIv);
			byte[] iv = Arrays.copyOfRange(data, 0, 16);
			byte[] textoEncriptado = Arrays.copyOfRange(data, 16, data.length);

			// Configuración del cifrado AES
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			// Inicialización del cifrado
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

			// Desencriptación del texto y retorno del resultado
			byte[] textoDesencriptado = cipher.doFinal(textoEncriptado);
			return new String(textoDesencriptado);
		} catch (IllegalArgumentException e) {
			// Manejo de errores de decodificación Base64 inválida
			throw new RuntimeException("Error: Cadena Base64 inválida", e);
		} catch (Exception e) {
			// Manejo de otros errores de desencriptación
			throw new RuntimeException("Error al desencriptar el texto", e);
		}
	}

	// Método para buscar y desencriptar un enlace en la base de datos
	@Override
	public String buscarLinkDesencriptado(String linkAcortado) {

		// Buscar el enlace en la base de datos
		Link link = linkRepository.findByLinkAcortado(linkAcortado)
				.orElseThrow(() -> new RuntimeException("Enlace no encontrado"));

		// Desencriptar el enlace original y retornar el resultado
		return desencriptar(link.getLinkOriginal());
	}

	// Método para acortar y guardar un enlace en la base de datos
	@Override
	public String acortarYGuardarLink(String linkOriginal) {
		if (!esEnlaceValido(linkOriginal)) {
			throw new RuntimeException("Enlace no válido");
		}

		String linkAcortado;
		int intentos = 0;
		String linkEncriptado;
		do {
			// Encriptación del enlace original
			linkEncriptado = encriptar(linkOriginal);

			// Generación del enlace acortado
			linkAcortado = linkEncriptado.substring(0, Math.min(7, linkEncriptado.length()));

			// Verificación de duplicados en la base de datos
			Optional<Link> linkExistente = linkRepository.findByLinkAcortado(linkAcortado);
			if (linkExistente.isPresent()) {
				// Si el enlace acortado ya existe, se agrega un sufijo hexadecimal
				intentos++;
				linkAcortado = linkEncriptado.substring(0, Math.min(7, linkEncriptado.length()))
						+ Integer.toHexString(intentos);
			}
		} while (linkRepository.findByLinkAcortado(linkAcortado).isPresent());

		// HABRIA QUE AÑADIR EL LINK DE LA OTRA PAG WEB PARA AÑADÍRSELO AL LINKACORTADO
		// DE DELANTE
		// POR EJEMPLO: https://s-url.vercel.com/XXXXXXX

		// Guardar el enlace original y el enlace acortado en la base de datos
		linkRepository.save(new Link(linkEncriptado, linkAcortado));
		return linkAcortado;
	}


	public byte[] generateQRCode(String content) {
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);

			// Conversión del BitMatrix a BufferedImage
			BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			// Conversión de la imagen a un array de bytes en formato PNG
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(qrCodeImage, "png", stream);

			return stream.toByteArray();
		} catch (WriterException | IOException e) {
			// Manejo de errores
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Transactional
	public byte[] generarYActualizarQRCode(String linkToQr, String baseUrl) {
		Optional<Link> linkExists = existeQR(linkToQr);
		byte[] qrCodeBytes = null;

		if (linkExists.isPresent()) {
			String finalLink = baseUrl + linkToQr;
			System.out.println(finalLink);
			qrCodeBytes = generateQRCode(finalLink);
			actualizarLinkConQRCode(linkToQr, qrCodeBytes);
		}

		return qrCodeBytes;
	}

	@Override
	public void actualizarLinkConQRCode(String linkAcortado, byte[] qrCodeBytes) {
		Link link = linkRepository.findByLinkAcortado(linkAcortado)
				.orElseThrow(() -> new RuntimeException("Enlace no encontrado"));

		// Convertir qrCodeBytes a Base64 y actualizar
		String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
		link.setQrCodeImageBase64(qrCodeBase64);

		linkRepository.save(link);
	}

	@Override
	public Optional<Link> existeQR(String linkAcortado) {
		Optional<Link> link = Optional.ofNullable(linkRepository.findByLinkAcortado(linkAcortado)
				.orElseThrow(() -> new RuntimeException("Enlace no encontrado")));
		return link;
	}

	private boolean esEnlaceValido(String link) {
		try {
			URL url = new URL(link);
			url.toURI();

			// Lista blanca (por ejemplo, permitir sólo enlaces "https")
			if (!"https".equals(url.getProtocol())) {
				return false;
			}

			// Lista negra de dominios (ejemplo)
			String host = url.getHost();
			if (host.equals("sitioNoDeseado.com") || host.equals("otroSitioMalicioso.com")) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
