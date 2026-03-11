# 🇧🇷 SolidSign API - Exemplo de Assinatura PDF PKCS12 (Batch Mode)

Este projeto demonstra a integração com a **SolidSign API** para realizar a assinatura digital de múltiplos documentos PDF em uma única operação, utilizando certificados no formato PFX/PKCS12 e processamento de imagens (carimbos).

## ## Estrutura do Projeto

* **Controller:** Recebe os arquivos via Multipart e gerencia a resposta ZIP.
* **Service:** Orquestra a chamada para a API, trata erros 400/500 e realiza o download dos arquivos assinados.

## ## Configuração (application.properties)

| Atributo | Descrição | Exemplo / Valor |
| :--- | :--- | :--- |
| `solidsign.api.url` | Endpoint da API para assinatura PKCS12. | `https://solidsign.com.br/.../sign-pkcs12` |
| `solidsign.api.token` | Token JWT de autorização (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Caminho absoluto do arquivo `.pfx` ou `.p12`. | `C:/certificados/meu_cert.pfx` |
| `solidsign.cert.password-base64` | Senha do certificado em texto simples ou Base64. | `MTIzNDU2....` |
| `solidsign.sig.image-paths` | Lista de caminhos para as imagens de carimbo. | `C:/img/stamp1.png, C:/img/stamp2.png` |
| `solidsign.sig.field-config` | JSON com coordenadas e dimensões da assinatura. | `solidsign.sig.field-config=[{ "coordinateX": 30, "coordinateY": 30, "page": "FIRST_PAGE", "width": 60, "height": 150 }]` |
| `solidsign.sig.profile` | Perfil/Padrão da assinatura (Adobe/PAdES ETSI/ PBAD ICP-Brasil). | `ADRB`, `PADES_B`, `PDF_BASIC`, `ADRT`, etc. |
| `solidsign.sig.unit` | Unidade de medida para os campos. | `PIXELS` ou `MILLIMETERS` |

## ## Como Executar

1. **Compilar:** `mvn clean install`
2. **Configurar:** Defina suas credenciais, certificado e caminhos de imagem no arquivo `src/main/resources/application.properties`.
3. **Iniciar:** `mvn spring-boot:run`
4. **Testar:** Envie um POST para `http://localhost:8080/api/pdf/sign-multiple` com o campo `files`.

## ## Tratamento de Erros
O sistema intercepta erros **400 Bad Request** e loga o JSON detalhado da SolidSign para facilitar o debug de certificados ou parâmetros inválidos.

====================================================================================================================================================

# 🇬🇧 SolidSign API - PDF PKCS12 Signature Example (Batch Mode)

This project demonstrates the integration with the **SolidSign API** to perform digital signatures on multiple PDF documents in a single operation, using PFX/PKCS12 certificates and image stamp processing.

## ## Project Structure

* **Controller:** Handles Multipart file uploads and manages the ZIP response.
* **Service:** Orchestrates the API calls, handles 400/500 errors, and downloads signed files.
* **Config:** Custom Logback Appenders (Syslog and RollingFile).

## ## Configuration (application.properties)

| Attribute | Description | Example / Value |
| :--- | :--- | :--- |
| `solidsign.api.url` | SolidSign API endpoint for PKCS12 signing. | `https://solidsign.com.br/.../sign-pkcs12` |
| `solidsign.api.token` | Authorization JWT Token (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Absolute path to the `.pfx` or `.p12` file. | `/path/to/certificate.pfx` |
| `solidsign.cert.password-base64` | Certificate password (Plain or Base64). | `MTIzNDU2` |
| `solidsign.sig.image-paths` | Comma-separated list of image stamp paths. | `/img/stamp1.png, /img/stamp2.png` |
| `solidsign.sig.field-config` | JSON with signature coordinates and dimensions. | `[{"coordinateX": 30, "page": "FIRST_PAGE"}]` |
| `solidsign.sig.profile` | Signature Profile/Standard (PAdES). | `ADRT`, `PADES_B`, `PDF_COMPLETED` |
| `solidsign.sig.unit` | Measurement unit for signature fields. | `PIXELS` or `MILLIMETERS` |

## ## How to Run

1. **Build:** `mvn clean install`
2. **Configure:** Set your signature, certificate, and image settings in `src/main/resources/application.properties` file.
3. **Start:** `mvn spring-boot:run`
4. **Test:** Send a POST request to `http://localhost:8080/api/pdf/sign-multiple` using the `files` field.

## ## Error Handling
The system intercepts **400 Bad Request** errors and logs the detailed JSON response from SolidSign to assist in debugging invalid certificates or parameters.

====================================================================================================================================================

# 🇪🇸 SolidSign API - Ejemplo de Firma PDF PKCS12 (Modo Batch)

Este proyecto demuestra la integración con la **SolidSign API** para realizar la firma digital de múltiples documentos PDF en una sola operación, utilizando certificados en formato PFX/PKCS12 y procesamiento de imágenes (sellos).

## ## Estructura del Proyecto

* **Controller:** Recibe los archivos vía Multipart y gestiona la respuesta en formato ZIP.
* **Service:** Orquestra la llamada a la API, gestiona errores 400/500 y descarga los archivos firmados.
* **Config:** Appenders personalizados para Logback (Syslog y RollingFile).

## ## Configuración (application.properties)

| Atributo | Descripción | Ejemplo / Valor |
| :--- | :--- | :--- |
| `solidsign.api.url` | Endpoint de la API para firma PKCS12. | `https://solidsign.com.br/.../sign-pkcs12` |
| `solidsign.api.token` | Token JWT de autorización (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Ruta absoluta del archivo `.pfx` o `.p12`. | `/ruta/al/certificado.pfx` |
| `solidsign.cert.password-base64` | Contraseña del certificado (Plano o Base64). | `MTIzNDU2` |
| `solidsign.sig.image-paths` | Lista de rutas para las imágenes del sello. | `/img/sello1.png, /img/sello2.png` |
| `solidsign.sig.field-config` | JSON con coordenadas y dimensiones de la firma. | `[{"coordinateX": 30, "page": "FIRST_PAGE"}]` |
| `solidsign.sig.profile` | Perfil/Estándar de firma (PAdES). | `ADRT`, `PADES_B`, `PDF_COMPLETED` |
| `solidsign.sig.unit` | Unidad de medida para los campos de firma. | `PIXELS` o `MILLIMETERS` |

## ## Cómo Ejecutar

1. **Compilar:** `mvn clean install`
2. **Configurar:** Defina sus credenciales, certificado y rutas de imagen en el archivo `src/main/resources/application.properties`.
3. **Iniciar:** `mvn spring-boot:run`
4. **Probar:** Envíe un POST a `http://localhost:8080/api/pdf/sign-multiple` con el campo `files`.

## ## Gestión de Errores
El sistema intercepta errores **400 Bad Request** y registra el JSON detallado de SolidSign para facilitar la depuración de certificados o parámetros inválidos.