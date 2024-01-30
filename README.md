# Short URL Backend

¡Bienvenido al backend de Short URL! Esta aplicación proporciona servicios para acortar enlaces y generar códigos QR asociados.

## 🌟 Funcionalidades

1. **Acortar enlaces:** Envía una solicitud POST a `/api/links/acortar` con un enlace original, y la aplicación te devolverá un enlace acortado.
2. **Generar códigos QR:** Envía una solicitud POST a `/api/links/qrGenerate` con un enlace y la URL base para recibir un código QR asociado.
3. **Redirigir enlaces acortados:** Una solicitud GET a `/api/links/{linkAcortado}` redirige al enlace original desencriptado.

## ⚙️ Configuración

> 💡 **Tip:** Asegúrate de configurar la clave secreta para la encriptación AES y el algoritmo en tu aplicación. Puedes hacerlo en el archivo `application.properties` o mediante variables de entorno.

# application.properties
clave.secreta=TuClaveSecreta
algoritmo=AES/CBC/PKCS5Padding


## 🚀 Uso

**Clona este repositorio**:

git clone https://github.com/tu-usuario/short-url-backend.git
cd short-url-backend

 **Construye y ejecuta la aplicación**:

./mvnw spring-boot:run
O mediante un IDE como IntelliJ o Eclipse.

Realiza solicitudes HTTP a las rutas mencionadas anteriormente utilizando herramientas como cURL, Postman o desde tu aplicación frontend.

## 📦 Dependencias
Java 11
Spring Boot
Base de datos (configurada en application.properties)
Google ZXing para la generación de códigos QR

## 🤝 Contribuir
¡Siéntete libre de contribuir a este proyecto! Abre un problema o envía una solicitud de extracción con tus mejoras.


Licencia
Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.

Contacto
📧 Issues email: hgc.csn@gmail.com
📧 Creator email: carl.san.nu@gmail.com