package com.solidsign.examples;

import java.util.Locale;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SolidSignPdfPkcs12Application {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SolidSignPdfPkcs12Application.class);
	
	@Value("${app.version}")
	private String version;
	
	@Value("${app.locale}")
	private String locale;

	public static void main(String[] args) {
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);

		//DSSAPI-11 - TODO Fix DSS_I18n so it doesn't require specific suffix for bundle name
		Locale.setDefault(new Locale("en", "US"));
		
		SpringApplication.run(SolidSignPdfPkcs12Application.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void afterReady() {
		LOGGER.info("SolidSign API Back-End Application (PDF-FileCertificatePKCS12) version {} has been successfully initialized in language: {}.", version, locale);
	}
	
}
