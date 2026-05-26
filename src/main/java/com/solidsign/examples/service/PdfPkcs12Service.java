package com.solidsign.examples.service;

import com.solidsign.examples.response.SignResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;
import java.util.zip.*;

/**
 * [EN]    PAdES (PDF) signing service using a PKCS#12 certificate pre-imported into the cache.
 *         Supports visual signature image and field configuration.
 *
 *         Usage flow:
 *          1. Import the certificate ONCE:
 *               POST /solidsign/dsig/certificates/pkcs12/import
 *               Body (multipart): pfxCertificate=<file.pfx>, pfxPassword=<base64-password>
 *             Response: { "id": "<uuid>", "alias": "...", "expirationDate": "..." }
 *          2. Set the returned UUID in solidsign.cert.id (application.properties).
 *          3. On every signing request, only the UUID is sent as "pfxCode".
 *
 * [PT-BR] Serviço de assinatura PAdES (PDF) utilizando certificado PKCS#12 pré-importado na cache.
 *         Suporta imagem de assinatura visual e configuração de campo.
 *
 *         Fluxo de uso:
 *          1. Importe o certificado UMA VEZ:
 *               POST /solidsign/dsig/certificates/pkcs12/import
 *               Body (multipart): pfxCertificate=<arquivo.pfx>, pfxPassword=<senha-base64>
 *             Resposta: { "id": "<uuid>", "alias": "...", "expirationDate": "..." }
 *          2. Configure o UUID retornado em solidsign.cert.id (application.properties).
 *          3. A cada requisição de assinatura, apenas o UUID é enviado como "pfxCode".
 *
 * [ES]    Servicio de firma PAdES (PDF) utilizando un certificado PKCS#12 pre-importado en la caché.
 *         Soporta imagen de firma visual y configuración de campo.
 *
 *         Flujo de uso:
 *          1. Importe el certificado UNA VEZ:
 *               POST /solidsign/dsig/certificates/pkcs12/import
 *               Body (multipart): pfxCertificate=<archivo.pfx>, pfxPassword=<contraseña-base64>
 *             Respuesta: { "id": "<uuid>", "alias": "...", "expirationDate": "..." }
 *          2. Configure el UUID devuelto en solidsign.cert.id (application.properties).
 *          3. En cada solicitud de firma, solo el UUID se envía como "pfxCode".
 */
@Service
public class PdfPkcs12Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPkcs12Service.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // [EN]    Base URL of the SolidSign API
    // [PT-BR] URL base da API SolidSign
    // [ES]    URL base de la API SolidSign
    @Value("${solidsign.api.base-url}")
    private String baseUrl;

    // [EN]    Authorization header value (Bearer token)
    // [PT-BR] Valor do header Authorization (token Bearer)
    // [ES]    Valor del header Authorization (token Bearer)
    @Value("${solidsign.api.authorization}")
    private String authorization;

    // [EN]    Signature profile (e.g. ADRB, ADRT, ADRC, ADRA)
    // [PT-BR] Perfil de assinatura (ex: ADRB, ADRT, ADRC, ADRA)
    // [ES]    Perfil de firma (p.ej. ADRB, ADRT, ADRC, ADRA)
    @Value("${solidsign.sig.profile}")
    private String profile;

    // [EN]    Hash algorithm (SHA256, SHA384, SHA512)
    // [PT-BR] Algoritmo de hash (SHA256, SHA384, SHA512)
    // [ES]    Algoritmo de hash (SHA256, SHA384, SHA512)
    @Value("${solidsign.sig.hashAlgorithm}")
    private String hashAlgorithm;

    // [EN]    Optional: policy version — leave blank to use the API default
    // [PT-BR] Opcional: versão da política — deixe em branco para usar o padrão da API
    // [ES]    Opcional: versión de la política — déjelo en blanco para usar el valor por defecto de la API
    @Value("${solidsign.sig.policyVersion:}")
    private String policyVersion;

    // [EN]    Measurement unit for visual signature field (PIXELS or CENTIMETERS)
    // [PT-BR] Unidade de medida para o campo de assinatura visual (PIXELS ou CENTIMETERS)
    // [ES]    Unidad de medida para el campo de firma visual (PIXELS o CENTIMETERS)
    @Value("${solidsign.sig.sigFieldMeasurementUnit}")
    private String sigFieldMeasurementUnit;

    // [EN]    JSON array describing the visual signature field(s)
    // [PT-BR] Array JSON descrevendo o(s) campo(s) de assinatura visual
    // [ES]    Array JSON que describe el/los campo(s) de firma visual
    @Value("${solidsign.sig.signatureFieldConfig}")
    private String signatureFieldConfig;

    // [EN]    Comma-separated list of signature image file paths
    // [PT-BR] Lista de caminhos de imagem de assinatura separados por vírgula
    // [ES]    Lista de rutas de imagen de firma separadas por coma
    @Value("${solidsign.sig.signatureImagePaths}")

    private List<String> signatureImagePaths;

    // [EN]    Reason for signing — displayed in the PDF signature properties
    // [PT-BR] Motivo da assinatura — exibido nas propriedades da assinatura PDF
    // [ES]    Motivo de la firma — se muestra en las propiedades de la firma PDF
    @Value("${solidsign.sig.reason}")
    private String reason;

    // [EN]    Signing location — displayed in the PDF signature properties
    // [PT-BR] Local da assinatura — exibido nas propriedades da assinatura PDF
    // [ES]    Lugar de firma — se muestra en las propiedades de la firma PDF
    @Value("${solidsign.sig.location}")
    private String location;

    // [EN]    Contact information of the signer — displayed in the PDF signature properties
    // [PT-BR] Informações de contato do assinante — exibido nas propriedades da assinatura PDF
    // [ES]    Información de contacto del firmante — se muestra en las propiedades de la firma PDF
    @Value("${solidsign.sig.contact}")
    private String contact;

    // [EN]    Optional: name of a pre-existing signature field in the PDF to target
    // [PT-BR] Opcional: nome de um campo de assinatura pré-existente no PDF a utilizar
    // [ES]    Opcional: nombre de un campo de firma preexistente en el PDF
    // @Value("${solidsign.sig.signatureFieldName:}")
    // private String signatureFieldName;

    // [EN]    Optional: text overlays as JSON array (pageNumber, coordinateX, coordinateY, text, fontSize, textColor)
    // [PT-BR] Opcional: sobreposições de texto como array JSON (pageNumber, coordinateX, coordinateY, text, fontSize, textColor)
    // [ES]    Opcional: superposiciones de texto como array JSON (pageNumber, coordinateX, coordinateY, text, fontSize, textColor)
    // @Value("${solidsign.sig.signatureTextConfig:}")
    // private String signatureTextConfig;

    // [EN]    Optional: MDP permission level for certification signature (1=no changes, 2=forms, 3=annotations)
    // [PT-BR] Opcional: nível de permissão MDP para assinatura de certificação (1=sem alterações, 2=formulários, 3=anotações)
    // [ES]    Opcional: nivel de permiso MDP para firma de certificación (1=sin cambios, 2=formularios, 3=anotaciones)
    // @Value("${solidsign.sig.mdpPermissionLevel:}")
    // private String mdpPermissionLevel;

    // [EN]    Optional: JSON array of per-document open passwords for encrypted PDFs (e.g. ["pwd1","pwd2",null])
    // [PT-BR] Opcional: array JSON de senhas por documento para PDFs criptografados (ex: ["senha1","senha2",null])
    // [ES]    Opcional: array JSON de contraseñas por documento para PDFs cifrados (p.ej. ["pwd1","pwd2",null])
    // @Value("${solidsign.sig.passwordsForDecryption:}")
    // private String passwordsForDecryption;

    // [EN]    Optional: JSON map of PDF document metadata (title, author, subject, keywords, creator)
    // [PT-BR] Opcional: mapa JSON de metadados do documento PDF (title, author, subject, keywords, creator)
    // [ES]    Opcional: mapa JSON de metadatos del documento PDF (title, author, subject, keywords, creator)
    // @Value("${solidsign.sig.documentInfoMetadata:}")
    // private String documentInfoMetadata;

    // [EN]    Optional: QR code config as JSON array — CANNOT be used together with signatureFieldConfig
    // [PT-BR] Opcional: configuração de QR code como array JSON — NÃO pode ser usado junto com signatureFieldConfig
    // [ES]    Opcional: configuración de código QR como array JSON — NO puede usarse junto con signatureFieldConfig
    // @Value("${solidsign.sig.signatureQrCodeConfig:}")
    // private String signatureQrCodeConfig;

    /**
     * [EN]    Signs the given PDF files via PAdES using the UUID of the pre-imported certificate.
     * [PT-BR] Assina os PDFs informados via PAdES usando o UUID do certificado pré-importado.
     * [ES]    Firma los archivos PDF indicados vía PAdES usando el UUID del certificado pre-importado.
     *
     * @param pdfFiles
     *   [EN]    PDF files to sign
     *   [PT-BR] arquivos PDF a assinar
     *   [ES]    archivos PDF a firmar
     * @param certId
     *   [EN]    UUID of the imported certificate (value of solidsign.cert.id)
     *   [PT-BR] UUID do certificado importado (valor de solidsign.cert.id)
     *   [ES]    UUID del certificado importado (valor de solidsign.cert.id)
     * @param outputDir
     *   [EN]    destination folder for the ZIP of signed files
     *   [PT-BR] pasta de destino do ZIP com os arquivos assinados
     *   [ES]    carpeta de destino del ZIP con los archivos firmados
     * @return
     *   [EN]    path of the generated ZIP, or null on error
     *   [PT-BR] caminho do ZIP gerado, ou null em caso de erro
     *   [ES]    ruta del ZIP generado, o null en caso de error
     */
    public String signPkcs12(List<File> pdfFiles, String certId, String outputDir) throws IOException {
        LOGGER.info("Starting PAdES PKCS12 signing for {} PDF(s) using certId={}.", pdfFiles.size(), certId);

        // [EN]    Build the full endpoint URL from the base URL
        // [PT-BR] Constrói a URL completa do endpoint a partir da URL base
        // [ES]    Construye la URL completa del endpoint a partir de la URL base
        String signUrl = baseUrl + "/solidsign/dsig/pdf/sign-pkcs12";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authorization);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // [EN]    Attach each PDF indexed as document[0], document[1], ...
        // [PT-BR] Anexa cada PDF indexado como document[0], document[1], ...
        // [ES]    Adjunta cada PDF indexado como document[0], document[1], ...
        for (int i = 0; i < pdfFiles.size(); i++) {
            body.add("document[" + i + "]", new FileSystemResource(pdfFiles.get(i)));
        }

        // [EN]    Pre-imported certificate UUID — sent as "pfxCode", not the raw PFX file
        // [PT-BR] UUID do certificado pré-importado — enviado como "pfxCode", não o arquivo PFX bruto
        // [ES]    UUID del certificado pre-importado — enviado como "pfxCode", no el archivo PFX bruto
        body.add("pfxCode", certId);

        // [EN]    Signature parameters
        // [PT-BR] Parâmetros de assinatura
        // [ES]    Parámetros de firma
        body.add("profile",                 profile);
        body.add("hashAlgorithm",           hashAlgorithm);
        // [EN]    Only send policyVersion if explicitly configured (optional parameter)
        // [PT-BR] Enviar policyVersion apenas se configurado explicitamente (parâmetro opcional)
        // [ES]    Enviar policyVersion solo si está configurado explícitamente (parámetro opcional)
        if (policyVersion != null && !policyVersion.isBlank()) body.add("policyVersion", policyVersion);
        body.add("sigFieldMeasurementUnit", sigFieldMeasurementUnit);
        body.add("signatureFieldConfig",    signatureFieldConfig);
        body.add("reason",                  reason);
        body.add("location",                location);
        body.add("contact",                 contact);

        // [EN]    Optional parameters — uncomment to use
        // [PT-BR] Parâmetros opcionais — descomente para usar
        // [ES]    Parámetros opcionales — descomente para usar
        // body.add("signatureFieldName",     signatureFieldName);    // target an existing named field
        // body.add("signatureTextConfig",    signatureTextConfig);   // text overlays
        // body.add("mdpPermissionLevel",     mdpPermissionLevel);    // certification signature permissions
        // body.add("passwordsForDecryption", passwordsForDecryption); // for encrypted PDFs
        // body.add("documentInfoMetadata",   documentInfoMetadata);  // PDF metadata key-value pairs
        // body.add("signatureQrCodeConfig",  signatureQrCodeConfig); // QR code (conflicts with signatureFieldConfig)

        // [EN]    Optional visual signature images (one per document, indexed)
        // [PT-BR] Imagens de assinatura visual opcionais (uma por documento, indexadas)
        // [ES]    Imágenes de firma visual opcionales (una por documento, indexadas)
        if (signatureImagePaths != null) {
            for (int i = 0; i < signatureImagePaths.size(); i++) {
                File imgFile = new File(signatureImagePaths.get(i).trim());
                if (imgFile.exists()) {
                    body.add("signatureImage[" + i + "]", new FileSystemResource(imgFile));
                }
            }
        }

        try {
            ResponseEntity<SignResponse> resp = restTemplate.postForEntity(
                    signUrl, new HttpEntity<>(body, headers), SignResponse.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                byte[] zip = downloadAndZip(resp.getBody(), pdfFiles);
                new File(outputDir).mkdirs();
                String out = outputDir + "/signed_pdf_pkcs12_" + System.currentTimeMillis() + ".zip";
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(zip);
                }
                LOGGER.info("PAdES PKCS12 signing complete. Output: {}", out);
                return out;
            }
        } catch (HttpStatusCodeException e) {
            LOGGER.error("SolidSign API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during PAdES PKCS12 signing: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * [EN]    Downloads each signed document from the SolidSign response links and packages them into a ZIP.
     * [PT-BR] Baixa cada documento assinado dos links da resposta SolidSign e os empacota em um ZIP.
     * [ES]    Descarga cada documento firmado de los enlaces de respuesta SolidSign y los empaqueta en un ZIP.
     */
    private byte[] downloadAndZip(SignResponse resp, List<File> originals) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authorization);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            for (int i = 0; i < resp.documents.size(); i++) {
                String downloadUrl = resp.documents.get(i).links.stream()
                        .filter(l -> "self".equals(l.rel))
                        .findFirst()
                        .map(l -> l.href)
                        .orElse(null);
                if (downloadUrl == null) continue;
                ResponseEntity<byte[]> r = restTemplate.exchange(
                        downloadUrl, HttpMethod.GET, entity, byte[].class);
                if (r.getStatusCode() == HttpStatus.OK) {
                    zos.putNextEntry(new ZipEntry("signed_" + originals.get(i).getName()));
                    zos.write(r.getBody());
                    zos.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }

    // ─── Form endpoint (all params from request, properties ignored) ──────────

    /**
     * [EN]    Signs PDF documents via PAdES PKCS#12 with all parameters supplied by the caller.
     *         signatureImages may be null or empty if no visual signature image is needed.
     * [PT-BR] Assina PDFs via PAdES PKCS#12 com todos os parâmetros fornecidos pelo chamador.
     *         signatureImages pode ser null ou vazio se não for necessária imagem de assinatura visual.
     * [ES]    Firma PDFs vía PAdES PKCS#12 con todos los parámetros suministrados por el llamador.
     *         signatureImages puede ser null o vacío si no se necesita imagen de firma visual.
     *
     * @return ZIP bytes with signed documents, or null on error
     */
    public byte[] signPkcs12Form(String auth, String apiBaseUrl, String certId,
                                  String profile, String hashAlgorithm, String policyVersion,
                                  String sigFieldMeasurementUnit, String signatureFieldConfig,
                                  String reason, String location, String contact,
                                  String signatureFieldName, String signatureTextConfig,
                                  String mdpPermissionLevel, String passwordsForDecryption,
                                  String documentInfoMetadata, String signatureQrCodeConfig,
                                  List<File> pdfFiles, List<File> signatureImages) throws IOException {
        LOGGER.info("PAdES PKCS12 form signing for {} PDF(s).", pdfFiles.size());
        String signUrl = apiBaseUrl + "/solidsign/dsig/pdf/sign-pkcs12";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", auth);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < pdfFiles.size(); i++) body.add("document[" + i + "]", new FileSystemResource(pdfFiles.get(i)));
        body.add("pfxCode", certId);
        if (profile != null && !profile.isBlank())                     body.add("profile",                 profile);
        if (hashAlgorithm != null && !hashAlgorithm.isBlank())         body.add("hashAlgorithm",           hashAlgorithm);
        if (policyVersion != null && !policyVersion.isBlank())         body.add("policyVersion",           policyVersion);
        if (sigFieldMeasurementUnit != null && !sigFieldMeasurementUnit.isBlank()) body.add("sigFieldMeasurementUnit", sigFieldMeasurementUnit);
        if (signatureFieldConfig != null && !signatureFieldConfig.isBlank()) body.add("signatureFieldConfig", signatureFieldConfig);
        if (reason != null && !reason.isBlank())                       body.add("reason",                  reason);
        if (location != null && !location.isBlank())                   body.add("location",                location);
        if (contact != null && !contact.isBlank())                     body.add("contact",                 contact);
        if (signatureFieldName != null && !signatureFieldName.isBlank()) body.add("signatureFieldName",    signatureFieldName);
        if (signatureTextConfig != null && !signatureTextConfig.isBlank()) body.add("signatureTextConfig", signatureTextConfig);
        if (mdpPermissionLevel != null && !mdpPermissionLevel.isBlank()) body.add("mdpPermissionLevel",    mdpPermissionLevel);
        if (passwordsForDecryption != null && !passwordsForDecryption.isBlank()) body.add("passwordsForDecryption", passwordsForDecryption);
        if (documentInfoMetadata != null && !documentInfoMetadata.isBlank()) body.add("documentInfoMetadata", documentInfoMetadata);
        if (signatureQrCodeConfig != null && !signatureQrCodeConfig.isBlank()) body.add("signatureQrCodeConfig", signatureQrCodeConfig);
        if (signatureImages != null) {
            for (int i = 0; i < signatureImages.size(); i++)
                body.add("signatureImage[" + i + "]", new FileSystemResource(signatureImages.get(i)));
        }
        try {
            ResponseEntity<SignResponse> resp = restTemplate.postForEntity(
                    signUrl, new HttpEntity<>(body, headers), SignResponse.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                SignResponse signResp = resp.getBody();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    HttpHeaders dh = new HttpHeaders();
                    dh.set("Authorization", auth);
                    HttpEntity<Void> de = new HttpEntity<>(dh);
                    for (int i = 0; i < signResp.documents.size(); i++) {
                        String dlUrl = signResp.documents.get(i).links.stream()
                                .filter(l -> "self".equals(l.rel)).findFirst()
                                .map(l -> l.href).orElse(null);
                        if (dlUrl == null) continue;
                        ResponseEntity<byte[]> r = restTemplate.exchange(
                                dlUrl, HttpMethod.GET, de, byte[].class);
                        if (r.getStatusCode() == HttpStatus.OK) {
                            zos.putNextEntry(new ZipEntry("signed_" + pdfFiles.get(i).getName()));
                            zos.write(r.getBody());
                            zos.closeEntry();
                        }
                    }
                }
                return baos.toByteArray();
            }
        } catch (HttpStatusCodeException e) {
            LOGGER.error("SolidSign API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Unexpected error in PAdES PKCS12 form signing: {}", e.getMessage(), e);
        }
        return null;
    }
}
