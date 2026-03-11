package com.solid.exemplos.config;

import ch.qos.logback.core.rolling.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRollingFileAppender extends RollingFileAppender<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomRollingFileAppender.class);
    
    @Override
    protected void append(Object eventObject) {
        super.append(eventObject);  
    }
}