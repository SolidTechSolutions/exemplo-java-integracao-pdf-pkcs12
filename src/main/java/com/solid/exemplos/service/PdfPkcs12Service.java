package com.solid.exemplos.service;

import com.solid.exemplos.response.SignResponse;
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

import java.io.File;
import java.io.FileOutputStream;
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

    public String processLocalFiles(List<File> pdfFiles, File p12Cert, String p12Pass, String outputDir) throws IOException {
        
        LOGGER.info("Starting batch processing for {} local files", pdfFiles.size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authorization);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 1. Adding Documents from local path
        for (int i = 0; i < pdfFiles.size(); i++) {
            body.add("document[" + i + "]", new FileSystemResource(pdfFiles.get(i)));
        }

        // 2. Adding Signature Images
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
            ResponseEntity<SignResponse> response = restTemplate.postForEntity(apiUrl, request, SignResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                byte[] zipContent = downloadAndZipFiles(response.getBody(), pdfFiles);
                
                File outDir = new File(outputDir);
                if (!outDir.exists()) outDir.mkdirs();
                
                String outputFileName = outputDir + "/signed_batch_" + System.currentTimeMillis() + ".zip";
                try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                    fos.write(zipContent);
                }
                return outputFileName;
            }
        } catch (HttpStatusCodeException e) {
            LOGGER.error("SolidSign API Error: {} | Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}", e.getMessage());
        }
        return null;
    }

    private byte[] downloadAndZipFiles(SignResponse signResponse, List<File> originalFiles) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authorization);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            for (int i = 0; i < signResponse.documents.size(); i++) {
                String downloadUrl = signResponse.documents.get(i).links.stream()
                        .filter(l -> "self".equals(l.rel)).findFirst().map(l -> l.href).orElse(null);

                if (downloadUrl != null) {
                    ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);
                    if (pdfResponse.getStatusCode() == HttpStatus.OK) {
                        zos.putNextEntry(new ZipEntry("signed_" + originalFiles.get(i).getName()));
                        zos.write(pdfResponse.getBody());
                        zos.closeEntry();
                    }
                }
            }
        }
        return baos.toByteArray();
    }
}