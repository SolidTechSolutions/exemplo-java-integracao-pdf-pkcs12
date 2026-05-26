package com.solidsign.examples.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.solidsign.examples.service.PdfPkcs12Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/pdf")
public class PdfPkcs12Controller {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPkcs12Controller.class);
    
    @Autowired
    private PdfPkcs12Service solidSignService;

    @Value("${solidsign.cert.id}")
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

        String resultPath = solidSignService.signPkcs12(
                pdfList, 
                certPassword, 
                outputPath
        );

        if (resultPath != null) {
            return ResponseEntity.ok("Processing completed! ZIP generated at: " + resultPath);
        } else {
            return ResponseEntity.internalServerError().body("Processing failed. Check logs.");
        }
    }

    /**
     * [EN]    Form signing endpoint for React — PAdES PKCS#12. properties ignored.
     *         signatureImages is optional; omit it if no visual signature image is needed.
     * [PT-BR] Endpoint de formulário para React — PAdES PKCS#12. properties ignorado.
     *         signatureImages é opcional; omita se não precisar de imagem de assinatura visual.
     * [ES]    Endpoint de formulario para React — PAdES PKCS#12. properties ignorado.
     *         signatureImages es opcional; omítalo si no se necesita imagen de firma visual.
     */
    @CrossOrigin
    @PostMapping("/sign/form")
    public ResponseEntity<byte[]> signForm(
            @RequestPart("document")                                              MultipartFile[]  documents,
            @RequestPart("authorization")                                         String           authorization,
            @RequestPart("baseUrl")                                               String           baseUrl,
            @RequestPart("pfxCode")                                               String           pfxCode,
            @RequestPart(value = "signatureImage",        required = false)       MultipartFile[]  signatureImages,
            @RequestPart(value = "profile",               required = false)       String           profile,
            @RequestPart(value = "hashAlgorithm",         required = false)       String           hashAlgorithm,
            @RequestPart(value = "policyVersion",         required = false)       String           policyVersion,
            @RequestPart(value = "sigFieldMeasurementUnit", required = false)     String           sigFieldMeasurementUnit,
            @RequestPart(value = "signatureFieldConfig",  required = false)       String           signatureFieldConfig,
            @RequestPart(value = "reason",                required = false)       String           reason,
            @RequestPart(value = "location",              required = false)       String           location,
            @RequestPart(value = "contact",               required = false)       String           contact,
            @RequestPart(value = "signatureFieldName",    required = false)       String           signatureFieldName,
            @RequestPart(value = "signatureTextConfig",   required = false)       String           signatureTextConfig,
            @RequestPart(value = "mdpPermissionLevel",    required = false)       String           mdpPermissionLevel,
            @RequestPart(value = "passwordsForDecryption", required = false)      String           passwordsForDecryption,
            @RequestPart(value = "documentInfoMetadata",  required = false)       String           documentInfoMetadata,
            @RequestPart(value = "signatureQrCodeConfig", required = false)       String           signatureQrCodeConfig
    ) throws IOException {
        List<File> tmpFiles = new ArrayList<>();
        List<File> tmpImgs  = new ArrayList<>();
        java.nio.file.Path tmpDir = java.nio.file.Files.createTempDirectory("solidsign-form-");
        try {
            for (MultipartFile mf : documents) {
                java.nio.file.Path p = tmpDir.resolve(mf.getOriginalFilename() != null ? mf.getOriginalFilename() : "doc.pdf");
                mf.transferTo(p);
                tmpFiles.add(p.toFile());
            }
            if (signatureImages != null) {
                for (MultipartFile img : signatureImages) {
                    java.nio.file.Path p = tmpDir.resolve(img.getOriginalFilename() != null ? img.getOriginalFilename() : "img");
                    img.transferTo(p);
                    tmpImgs.add(p.toFile());
                }
            }
            byte[] zip = solidSignService.signPkcs12Form(authorization, baseUrl, pfxCode,
                    profile, hashAlgorithm, policyVersion,
                    sigFieldMeasurementUnit, signatureFieldConfig,
                    reason, location, contact,
                    signatureFieldName, signatureTextConfig, mdpPermissionLevel,
                    passwordsForDecryption, documentInfoMetadata, signatureQrCodeConfig,
                    tmpFiles, tmpImgs);
            if (zip != null)
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType("application/zip"))
                        .header("Content-Disposition", "attachment; filename=\"signed_pdf.zip\"")
                        .body(zip);
            return ResponseEntity.internalServerError().build();
        } finally {
            tmpFiles.forEach(java.io.File::delete);
            tmpImgs.forEach(java.io.File::delete);
            tmpDir.toFile().delete();
        }
    }
}
