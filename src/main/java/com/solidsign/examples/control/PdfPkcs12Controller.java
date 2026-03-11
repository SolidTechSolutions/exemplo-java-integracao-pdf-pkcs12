package com.solidsign.examples.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solidsign.examples.service.PdfPkcs12Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    @Value("${solidsign.batch.input-path}")
    private String inputPath;

    @Value("${solidsign.batch.output-path}")
    private String outputPath;

    @PostMapping("/sign-pkcs12")
    public ResponseEntity<String> signPkcs12Folder() throws IOException {
        
        File folder = new File(inputPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.badRequest().body("Invalid input path: " + inputPath);
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        
        if (files == null || files.length == 0) {
            return ResponseEntity.ok("No PDF files found in " + inputPath);
        }

        List<File> pdfList = Arrays.asList(files);
        LOGGER.info("Found {} files for local processing.", pdfList.size());

        String resultPath = solidSignService.signPkcs12WithApi(
                pdfList, 
                new File(certPath), 
                certPassword, 
                outputPath
        );

        if (resultPath != null) {
            return ResponseEntity.ok("Processing completed! ZIP generated at: " + resultPath);
        } else {
            return ResponseEntity.internalServerError().body("Processing failed. Check logs.");
        }
    }
}