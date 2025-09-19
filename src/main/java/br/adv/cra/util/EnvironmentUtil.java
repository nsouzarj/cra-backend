package br.adv.cra.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentUtil.class);
    
    /**
     * Log environment information at startup
     */
    public static void logEnvironment() {
        logger.info("=== Application Environment Check ===");
        
        // Log some basic environment information
        logger.info("Java Version: {}", System.getProperty("java.version"));
        logger.info("Java Home: {}", System.getProperty("java.home"));
        logger.info("User Directory: {}", System.getProperty("user.dir"));
        logger.info("Temporary Directory: {}", System.getProperty("java.io.tmpdir"));
        
        logger.info("=====================================");
    }
    
    /**
     * Log all environment variables and system properties
     */
    public static void logAllEnvironment() {
        logger.info("=== Environment Check ===");
        
        // Log some basic environment information
        logger.info("Java Version: {}", System.getProperty("java.version"));
        logger.info("Java Home: {}", System.getProperty("java.home"));
        logger.info("User Directory: {}", System.getProperty("user.dir"));
        logger.info("Temporary Directory: {}", System.getProperty("java.io.tmpdir"));
        
        logger.info("=====================================");
    }
}