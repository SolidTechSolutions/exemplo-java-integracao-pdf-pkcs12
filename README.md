# 🇧🇷 SolidSign API - Exemplo de Assinatura PDF PKCS12 (Batch Mode)

Este projeto demonstra a integração com a **SolidSign API** para realizar a assinatura digital de múltiplos documentos PDF em uma única operação, utilizando certificados no formato PFX/PKCS12 e processamento de imagens (carimbos).



## Estrutura do Projeto

* **Controller:** Atua como gatilho para escanear a pasta de entrada local e gerenciar o processo.
* **Service:** Orquestra a chamada para a API, trata erros 400/500 e realiza o download dos arquivos assinados para o armazenamento local.

## Configuração (application.properties)

| Atributo | Descrição | Exemplo / Valor |
| :--- | :--- | :--- |
| `solidsign.api.url` | Endpoint da SolidSign API para assinatura PKCS12. | `https://solidsign.com.br/solidsign/dsig/pdf/sign-pkcs12` |
| `solidsign.api.token` | Token JWT de autorização (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Caminho absoluto do arquivo `.pfx` ou `.p12`. | `C:/certificados/meu_cert.pfx` |
| `solidsign.cert.password-base64` | Senha do certificado em Base64. | `MTIzNDU2....` |
| `solidsign.batch.input-path` | Pasta local contendo os PDFs originais para assinatura. | `C:/Users/User/Desktop/input_pdfs` |
| `solidsign.batch.output-path` | Pasta onde o arquivo .zip com os PDFs assinados será salvo. | `C:/Users/User/Desktop/signed_results` |
| `solidsign.sig.image-paths` | Lista de caminhos para as imagens de carimbo. | `C:/img/stamp1.png, C:/img/stamp2.png` |
| `solidsign.sig.field-config` | JSON com coordenadas e dimensões da assinatura. | `[{ "coordinateX": 30, "coordinateY": 30, "page": "FIRST_PAGE", "width": 60, "height": 150 }]` |
| `solidsign.sig.profile` | Perfil/Padrão da assinatura (Adobe/ICP-Brasil/PAdES ETSI). | `PDF_BASIC`, `ADRT`, `PADES_B`, etc. |
| `solidsign.sig.unit` | Unidade de medida para os campos. | `PIXELS` ou `MILLIMETERS` |

## Stack
1. Java 17
2. SpringBoot 3.4.x+
3. Maven 3.x.x+
4. Logback (para logging dos erros)

## Como Executar

1. **Compilar:** `mvn clean install`
2. **Configurar:** Defina suas credenciais, certificado e caminhos de pastas locais no arquivo `src/main/resources/application.properties`.
3. **Iniciar:** `mvn spring-boot:run`
4. **Testar:** Envie um POST para `http://localhost:8080/api/pdf/process-local-folder`. O sistema processará automaticamente todos os PDFs encontrados na pasta de entrada.

## Tratamento de Erros
O sistema intercepta erros **400 Bad Request** e loga o JSON detalhado da SolidSign para facilitar o debug de certificados ou parâmetros inválidos.

---

# 🇬🇧 SolidSign API - PDF PKCS12 Signature Example (Batch Mode)

This project demonstrates the integration with the **SolidSign API** to perform digital signatures on multiple PDF documents in a single operation, using PFX/PKCS12 certificates and image stamp processing.

## Project Structure

* **Controller:** Acts as a trigger to scan the local input folder and manage the process.
* **Service:** Orchestrates the API calls, handles 400/500 errors, and downloads signed files to the local storage.

## Configuration (application.properties)

| Attribute | Description | Example / Value |
| :--- | :--- | :--- |
| `solidsign.api.url` | SolidSign API endpoint for PKCS12 signing. | `https://solidsign.com.br/.../sign-pkcs12` |
| `solidsign.api.token` | Authorization JWT Token (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Absolute path to the `.pfx` or `.p12` file. | `C:/certificates/my_cert.pfx` |
| `solidsign.cert.password-base64` | Certificate password in Base64 format. | `MTIzNDU2....` |
| `solidsign.batch.input-path` | Local folder where original PDFs are stored for signing. | `C:/Users/User/Desktop/input_pdfs` |
| `solidsign.batch.output-path` | Local folder where the resulting ZIP file will be saved. | `C:/Users/User/Desktop/signed_results` |
| `solidsign.sig.image-paths` | Comma-separated list of image stamp paths. | `C:/img/stamp1.png, C:/img/stamp2.png` |
| `solidsign.sig.field-config` | JSON with signature coordinates and dimensions. | `[{ "coordinateX": 30, "coordinateY": 30, "page": "FIRST_PAGE", "width": 60, "height": 150 }]` |
| `solidsign.sig.profile` | Signature Profile/Standard (Adobe/ICP-Brasil/PAdES ETSI). | `PDF_BASIC`, `ADRT`, `PADES_B`, etc. |
| `solidsign.sig.unit` | Measurement unit for signature fields. | `PIXELS` or `MILLIMETERS` |

## Stack
1. Java 17
2. SpringBoot 3.4.x+
3. Maven 3.x.x+
4. Logback (for error logging)

## How to Run

1. **Build:** `mvn clean install`
2. **Configure:** Set your credentials, certificate, and local folder paths in the `src/main/resources/application.properties` file.
3. **Start:** `mvn spring-boot:run`
4. **Test:** Send a POST request to `http://localhost:8080/api/pdf/process-local-folder`. The application will scan your input folder and process all PDFs automatically.

## Error Handling
The system intercepts **400 Bad Request** errors and logs the detailed JSON response from SolidSign to assist in debugging invalid certificates or parameters.

---

# 🇪🇸 SolidSign API - Ejemplo de Firma PDF PKCS12 (Modo Batch)

Este proyecto demuestra la integración con la **SolidSign API** para realizar la firma digital de múltiples documentos PDF en una sola operación, utilizando certificados en formato PFX/PKCS12 y procesamiento de imágenes (sellos).

## Estructura del Proyecto

* **Controller:** Actúa como disparador para escanear la carpeta local de entrada y gestionar el proceso.
* **Service:** Orquestra las llamadas a la API, gestiona errores 400/500 y descarga los archivos firmados en el almacenamiento local.

## Configuración (application.properties)

| Atributo | Descripción | Ejemplo / Valor |
| :--- | :--- | :--- |
| `solidsign.api.url` | Endpoint de la SolidSign API para firma PKCS12. | `https://solidsign.com.br/.../sign-pkcs12` |
| `solidsign.api.token` | Token JWT de autorización (Bearer). | `Bearer eyJhbGciOiJIUzI1...` |
| `solidsign.cert.path` | Ruta absoluta del archivo `.pfx` o `.p12`. | `C:/certificados/mi_cert.pfx` |
| `solidsign.cert.password-base64` | Contraseña del certificado en formato Base64. | `MTIzNDU2....` |
| `solidsign.batch.input-path` | Carpeta local que contiene los PDF originales para firmar. | `C:/Users/User/Desktop/input_pdfs` |
| `solidsign.batch.output-path` | Carpeta local donde se guardará el archivo ZIP resultante. | `C:/Users/User/Desktop/signed_results` |
| `solidsign.sig.image-paths` | Lista de rutas para las imágenes del sello. | `C:/img/sello1.png, C:/img/sello2.png` |
| `solidsign.sig.field-config` | JSON con coordenadas y dimensiones de la firma. | `[{ "coordinateX": 30, "coordinateY": 30, "page": "FIRST_PAGE", "width": 60, "height": 150 }]` |
| `solidsign.sig.profile` | Perfil/Estándar de firma (Adobe/ICP-Brasil/PAdES ETSI). | `PDF_BASIC`, `ADRT`, `PADES_B`, etc. |
| `solidsign.sig.unit` | Unidad de medida para los campos de firma. | `PIXELS` o `MILLIMETERS` |

## Stack
1. Java 17
2. SpringBoot 3.4.x+
3. Maven 3.x.x+
4. Logback (para el registro de errores)

## Cómo Ejecutar

1. **Compilar:** `mvn clean install`
2. **Configurar:** Defina sus credenciales, certificado y rutas de carpetas locales en el archivo `src/main/resources/application.properties`.
3. **Iniciar:** `mvn spring-boot:run`
4. **Probar:** Envíe una solicitud POST a `http://localhost:8080/api/pdf/process-local-folder`. La aplicación escaneará su carpeta de entrada y procesará todos los PDF automáticamente.

## Gestión de Errores
El sistema intercepta errores **400 Bad Request** y registra el JSON detallado de SolidSign para facilitar la depuración de certificados o parámetros inválidos.