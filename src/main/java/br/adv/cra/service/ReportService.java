package br.adv.cra.service;

import br.adv.cra.entity.Solicitacao;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Generates a PDF report for a specific solicitacao
     * 
     * @param solicitacao The solicitacao entity to generate the report for
     * @return Byte array containing the generated PDF report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generateSolicitacaoReport(Solicitacao solicitacao) throws JRException {
        // Prepare parameters for the report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Detalhes da Solicitação");
        
        // Prepare data for the report
        List<Map<String, Object>> reportData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        
        // Add solicitacao details to the data map
        data.put("id", solicitacao.getId());
        data.put("numero", solicitacao.getNumero() != null ? solicitacao.getNumero() : "");
        data.put("dataSolicitacao", solicitacao.getDatasolicitacao() != null ? 
                 solicitacao.getDatasolicitacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        data.put("dataConclusao", solicitacao.getDataconclusao() != null ? 
                 solicitacao.getDataconclusao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        data.put("dataAgendamento", solicitacao.getDataagendamento() != null ? 
                 solicitacao.getDataagendamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        data.put("dataPrazo", solicitacao.getDataprazo() != null ? 
                 solicitacao.getDataprazo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        data.put("vara", solicitacao.getVara() != null ? solicitacao.getVara() : "");
        data.put("uf", solicitacao.getUf() != null ? solicitacao.getUf() : "");
        data.put("requerente", solicitacao.getRequerente() != null ? solicitacao.getRequerente() : "");
        data.put("requerido", solicitacao.getRequerido() != null ? solicitacao.getRequerido() : "");
        data.put("observacao", solicitacao.getObservacao() != null ? solicitacao.getObservacao() : "");
        data.put("instrucoes", solicitacao.getInstrucoes() != null ? solicitacao.getInstrucoes() : "");
        data.put("complemento", solicitacao.getComplemento() != null ? solicitacao.getComplemento() : "");
        data.put("justificativa", solicitacao.getJustificativa() != null ? solicitacao.getJustificativa() : "");
        data.put("tratposaudiencia", solicitacao.getTratposaudiencia() != null ? solicitacao.getTratposaudiencia() : "");
        data.put("numcontrole", solicitacao.getNumcontrole() != null ? solicitacao.getNumcontrole() : "");
        data.put("tempreposto", solicitacao.isTempreposto() ? "Sim" : "Não");
        data.put("convolada", solicitacao.isConvolada() ? "Sim" : "Não");
        data.put("horaudiencia", solicitacao.getHoraudiencia() != null ? solicitacao.getHoraudiencia() : "");
        data.put("statusexterno", solicitacao.getStatusexterno() != null ? solicitacao.getStatusexterno() : "");
        data.put("valor", solicitacao.getValor());
        data.put("valordaalcada", solicitacao.getValordaalcada());
        data.put("emailenvio", solicitacao.getEmailenvio() != null ? solicitacao.getEmailenvio() : "");
        data.put("pago", solicitacao.getPago() != null ? solicitacao.getPago() : "");
        data.put("grupo", solicitacao.getGrupo() != null ? solicitacao.getGrupo().toString() : "");
        data.put("propostaacordo", solicitacao.isPropostaacordo() ? "Sim" : "Não");
        data.put("audinterna", solicitacao.isAudinterna() ? "Sim" : "Não");
        data.put("lide", solicitacao.getLide() != null ? solicitacao.getLide() : "");
        data.put("avaliacaonota", solicitacao.getAvaliacaonota() != null ? solicitacao.getAvaliacaonota().toString() : "");
        data.put("textoavaliacao", solicitacao.getTextoavaliacao() != null ? solicitacao.getTextoavaliacao() : "");
        
        // Add related entities information
        if (solicitacao.getComarca() != null) {
            data.put("comarca", solicitacao.getComarca().getNome() != null ? solicitacao.getComarca().getNome() : "");
        } else {
            data.put("comarca", "");
        }
        
        if (solicitacao.getProcesso() != null) {
            data.put("processo", solicitacao.getProcesso().getNumeroprocesso() != null ? solicitacao.getProcesso().getNumeroprocesso() : "");
        } else {
            data.put("processo", "");
        }
        
        if (solicitacao.getStatusSolicitacao() != null) {
            data.put("status", solicitacao.getStatusSolicitacao().getStatus() != null ? solicitacao.getStatusSolicitacao().getStatus() : "");
        } else {
            data.put("status", "");
        }
        
        if (solicitacao.getTipoSolicitacao() != null) {
            data.put("tipoSolicitacao", solicitacao.getTipoSolicitacao().getEspecie() != null ? solicitacao.getTipoSolicitacao().getEspecie() : "");
        } else {
            data.put("tipoSolicitacao", "");
        }
        
        if (solicitacao.getCorrespondente() != null) {
            data.put("correspondente", solicitacao.getCorrespondente().getNome() != null ? solicitacao.getCorrespondente().getNome() : "");
        } else {
            data.put("correspondente", "");
        }
        
        if (solicitacao.getUsuario() != null) {
            data.put("usuario", solicitacao.getUsuario().getNomecompleto() != null ? solicitacao.getUsuario().getNomecompleto() : "");
        } else {
            data.put("usuario", "");
        }
        
        reportData.add(data);
        
        // Generate the report using a specific template for solicitacao
        return generatePdfReport("solicitacao-report", parameters, reportData);
    }
}