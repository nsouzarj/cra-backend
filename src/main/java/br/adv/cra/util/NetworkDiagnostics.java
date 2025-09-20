package br.adv.cra.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * Network diagnostics utility to check connectivity to Google services
 */
public class NetworkDiagnostics {
    private static final Logger logger = LoggerFactory.getLogger(NetworkDiagnostics.class);
    
    private static final String[] GOOGLE_HOSTS = {
        "www.googleapis.com",
        "oauth2.googleapis.com",
        "drive.googleapis.com",
        "accounts.google.com"
    };
    
    private static final int PORT = 443; // HTTPS port
    private static final int TIMEOUT_MS = 5000; // 5 seconds
    
    /**
     * Run comprehensive network diagnostics
     */
    public static void runDiagnostics() {
        logger.info("Running network diagnostics for Google services connectivity");
        
        boolean allConnected = true;
        
        for (String host : GOOGLE_HOSTS) {
            if (!checkConnectivity(host)) {
                allConnected = false;
            }
        }
        
        if (allConnected) {
            logger.info("All Google services are reachable");
        } else {
            logger.warn("Some Google services are not reachable. Check network configuration.");
        }
        
        // Check DNS resolution
        checkDNSResolution();
    }
    
    /**
     * Check connectivity to a specific host
     */
    private static boolean checkConnectivity(String host) {
        try {
            logger.info("Checking connectivity to {}:{}", host, PORT);
            
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, PORT), TIMEOUT_MS);
            socket.close();
            
            logger.info("Successfully connected to {}:{}", host, PORT);
            return true;
        } catch (IOException e) {
            logger.error("Failed to connect to {}:{} - {}", host, PORT, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check DNS resolution for Google services
     */
    private static void checkDNSResolution() {
        logger.info("Checking DNS resolution for Google services");
        
        for (String host : GOOGLE_HOSTS) {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(host);
                logger.info("DNS resolution for {} successful: {}", host, addresses.length > 0 ? addresses[0].getHostAddress() : "No addresses");
            } catch (UnknownHostException e) {
                logger.error("DNS resolution failed for {}: {}", host, e.getMessage());
            }
        }
    }
    
    /**
     * Test if we can establish an HTTPS connection
     */
    public static boolean testHttpsConnection(String url) {
        try {
            logger.info("Testing HTTPS connection to: {}", url);
            URL testUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) testUrl.openConnection();
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            logger.info("HTTPS connection test to {} returned response code: {}", url, responseCode);
            return responseCode == 200;
        } catch (Exception e) {
            logger.error("HTTPS connection test failed for {}: {}", url, e.getMessage());
            return false;
        }
    }
}