package com.solid.exemplos.service;

import com.solid.exemplos.response.SignResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfPkcs12Service {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPkcs12Service.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${solidsign.api.token}")
    private String authorization;

    @Value("${solidsign.api.url}")
    private String apiUrl;

    @Value("${solidsign.sig.hash-algorithm}")
    private String hashAlgorithm;

    @Value("${solidsign.sig.profile}")
    private String profile;

    @Value("${solidsign.sig.unit}")
    private String measurementUnit;

    @Value("${solidsign.sig.image-paths}")
    private List<String> signatureImagePaths;

    @Value("${solidsign.sig.field-config}")
    private String signatureFieldConfig;

    public byte[] signMultiplePdfsAndBundle(List<MultipartFile> originalFiles, File p12Cert, String p12Pass) throws IOException {
        
        LOGGER.info("Starting signature process for {} file(s)", originalFiles.size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authorization);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 1. Adding Documents
        for (int i = 0; i < originalFiles.size(); i++) {
            final int index = i;
            body.add("document[" + i + "]", new ByteArrayResource(originalFiles.get(i).getBytes()) {
                @Override public String getFilename() { return originalFiles.get(index).getOriginalFilename(); }
            });
        }

        // 2. Adding Images
        if (signatureImagePaths != null) {
            for (int i = 0; i < signatureImagePaths.size(); i++) {
                File imgFile = new File(signatureImagePaths.get(i).trim());
                if (imgFile.exists()) {
                    body.add("signatureImage[" + i + "]", new FileSystemResource(imgFile));
                }
            }
        }

        body.add("pfxCertificate", new FileSystemResource(p12Cert));
        body.add("pfxCode", p12Pass);
        body.add("hashAlgorithm", hashAlgorithm);
        body.add("profile", profile);
        body.add("sigFieldMeasurementUnit", measurementUnit);
        body.add("signatureFieldConfig", signatureFieldConfig);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            LOGGER.info("Sending request to SolidSign API: {}", apiUrl);
            ResponseEntity<SignResponse> response = restTemplate.postForEntity(apiUrl, request, SignResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                LOGGER.info("Signature request successful. Identifier: {}", response.getBody().identifier);
                return downloadAndZipFiles(response.getBody(), originalFiles);
            }
        } catch (HttpStatusCodeException e) {
            // LOGGING THE ERROR JSON (400, 401, 500, etc.)
            LOGGER.error("API Error - Status Code: {} | Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during signature request: {}", e.getMessage());
        }
        
        return null;
    }

    private byte[] downloadAndZipFiles(SignResponse signResponse, List<MultipartFile> originalFiles) throws IOException {
        LOGGER.info("Downloading signed documents and bundling into ZIP");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authorization);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            for (int i = 0; i < signResponse.documents.size(); i++) {
                String downloadUrl = signResponse.documents.get(i).links.stream()
                        .filter(l -> "self".equals(l.rel))
                        .findFirst()
                        .map(l -> l.href)
                        .orElse(null);

                if (downloadUrl != null) {
                    try {
                        ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);
                        if (pdfResponse.getStatusCode() == HttpStatus.OK) {
                            String fileName = "signed_" + originalFiles.get(i).getOriginalFilename();
                            zos.putNextEntry(new ZipEntry(fileName));
                            zos.write(pdfResponse.getBody());
                            zos.closeEntry();
                            LOGGER.debug("File added to ZIP: {}", fileName);
                        }
                    } catch (HttpStatusCodeException e) {
                        LOGGER.error("Error downloading signed file from {}: {}", downloadUrl, e.getResponseBodyAsString());
                    }
                }
            }
        }
        LOGGER.info("ZIP bundle created successfully");
        return baos.toByteArray();
    }
}