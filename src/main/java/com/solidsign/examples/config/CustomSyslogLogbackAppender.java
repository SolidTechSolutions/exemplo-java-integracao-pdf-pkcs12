package com.solidsign.examples.config;

import java.util.HashMap;
import ch.qos.logback.classic.net.SyslogAppender;

public class CustomSyslogLogbackAppender extends SyslogAppender {
    
    private final HashMap<String, String> facilities = new HashMap<>();

    private void initFacilities() {
        String[] facs = {"KERN", "USER", "MAIL", "DAEMON", "AUTH", "SYSLOG", "LPR", "NEWS", "UUCP", 
                         "CRON", "AUTHPRIV", "FTP", "NTP", "AUDIT", "ALERT", "CLOCK", "LOCAL0", 
                         "LOCAL1", "LOCAL2", "LOCAL3", "LOCAL4", "LOCAL5", "LOCAL6", "LOCAL7"};
        for (String f : facs) facilities.put(f, f);
    }

    @Override
    public void start() {
        initFacilities();
        String facility = this.getFacility();
        if (facility != null && facilities.containsKey(facility.toUpperCase())) {
            super.start();
        }
    }
}