package br.adv.cra.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@Service
public class ReportService {

    /**
     * Generates a PDF report from a Jasper report template and data
     * 
     * @param reportName Name of the Jasper report template file (without .jrxml extension)
     * @param parameters Map of parameters to pass to the report
     * @param data       Collection of data to fill the report
     * @return Byte array containing the generated PDF report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generatePdfReport(String reportName, Map<String, Object> parameters, Collection<?> data) 
            throws JRException {
        
        // Load the compiled Jasper report (.jasper file)
        ClassPathResource resource = new ClassPathResource("reports/" + reportName + ".jasper");
        
        try (InputStream reportStream = resource.getInputStream()) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            
            // Create data source
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Export to PDF
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            
            return pdfOutputStream.toByteArray();
        } catch (Exception e) {
            throw new JRException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a report in a specific format
     * 
     * @param reportName Name of the Jasper report template file (without .jrxml extension)
     * @param parameters Map of parameters to pass to the report
     * @param data       Collection of data to fill the report
     * @param outputType Output format (PDF, HTML, XLS, etc.)
     * @return Byte array containing the generated report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType) 
            throws JRException {
        
        // Load the compiled Jasper report (.jasper file)
        ClassPathResource resource = new ClassPathResource("reports/" + reportName + ".jasper");
        
        try (InputStream reportStream = resource.getInputStream()) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            
            // Create data source
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Export based on output type
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            switch (outputType.toUpperCase()) {
                case "PDF":
                    JRPdfExporter pdfExporter = new JRPdfExporter();
                    pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                    pdfExporter.setConfiguration(new SimplePdfExporterConfiguration());
                    pdfExporter.exportReport();
                    break;
                case "HTML":
                    // HTML export implementation would go here
                    break;
                case "XLS":
                    // Excel export implementation would go here
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported output type: " + outputType);
            }
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new JRException("Error generating report: " + e.getMessage(), e);
        }
    }
}