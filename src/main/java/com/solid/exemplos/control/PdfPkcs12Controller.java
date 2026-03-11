package com.solid.exemplos.control;

import com.solid.exemplos.SolidSignPdfPkcs12Application;
import com.solid.exemplos.service.PdfPkcs12Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/api/pdf")
public class PdfPkcs12Controller {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPkcs12Controller.class);
    
    @Autowired
    private PdfPkcs12Service solidSignService;

    @Value("${solidsign.cert.path}")
    private String certPath;

    @Value("${solidsign.cert.password-base64}")
    private String certPassword;
    
    @PostMapping(value = "/sign-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> signDocuments(@RequestParam("files") MultipartFile[] files) throws IOException {
        
        LOGGER.info("Received request to sign {} files", files != null ? files.length : 0);

        if (files == null || files.length == 0) {
            LOGGER.warn("Request failed: No files provided");
            return ResponseEntity.badRequest().build();
        }

        byte[] zipResult = solidSignService.signMultiplePdfsAndBundle(
                Arrays.asList(files), 
                new File(certPath), 
                certPassword
        );

        if (zipResult == null) {
            LOGGER.error("Signature process failed. Check service logs for API error details.");
            return ResponseEntity.internalServerError().build();
        }

        LOGGER.info("Signature process completed successfully. Returning ZIP file.");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header("Content-Disposition", "attachment; filename=\"all_signed_docs.zip\"")
                .body(zipResult);
    }
}