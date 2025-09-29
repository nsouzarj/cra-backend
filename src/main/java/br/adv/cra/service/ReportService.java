package br.adv.cra.service;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Processo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
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
        
        try {
            // First try to load the compiled .jasper file
            ClassPathResource jasperResource = new ClassPathResource("reports/" + reportName + ".jasper");
            
            JasperReport jasperReport;
            if (jasperResource.exists()) {
                // Load the compiled Jasper report (.jasper file)
                try (InputStream reportStream = jasperResource.getInputStream()) {
                    jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
                }
            } else {
                // If .jasper file doesn't exist, try to compile .jrxml file
                ClassPathResource jrxmlResource = new ClassPathResource("reports/" + reportName + ".jrxml");
                if (!jrxmlResource.exists()) {
                    throw new JRException("Report template not found: " + reportName);
                }
                
                // Compile the .jrxml file to JasperReport
                try (InputStream jrxmlStream = jrxmlResource.getInputStream()) {
                    JasperDesign jasperDesign = JRXmlLoader.load(jrxmlStream);
                    jasperReport = JasperCompileManager.compileReport(jasperDesign);
                }
            }
            
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
        
        try {
            // First try to load the compiled .jasper file
            ClassPathResource jasperResource = new ClassPathResource("reports/" + reportName + ".jasper");
            
            JasperReport jasperReport;
            if (jasperResource.exists()) {
                // Load the compiled Jasper report (.jasper file)
                try (InputStream reportStream = jasperResource.getInputStream()) {
                    jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
                }
            } else {
                // If .jasper file doesn't exist, try to compile .jrxml file
                ClassPathResource jrxmlResource = new ClassPathResource("reports/" + reportName + ".jrxml");
                if (!jrxmlResource.exists()) {
                    throw new JRException("Report template not found: " + reportName);
                }
                
                // Compile the .jrxml file to JasperReport
                try (InputStream jrxmlStream = jrxmlResource.getInputStream()) {
                    JasperDesign jasperDesign = JRXmlLoader.load(jrxmlStream);
                    jasperReport = JasperCompileManager.compileReport(jasperDesign);
                }
            }
            
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
        
        // Add complete correspondente data
        if (solicitacao.getCorrespondente() != null) {
            Correspondente correspondente = solicitacao.getCorrespondente();
            data.put("correspondenteId", correspondente.getId() != null ? correspondente.getId() : "");
            data.put("correspondenteNome", correspondente.getNome() != null ? correspondente.getNome() : "");
            data.put("correspondenteResponsavel", correspondente.getResponsavel() != null ? correspondente.getResponsavel() : "");
            data.put("correspondenteCpfcnpj", correspondente.getCpfcnpj() != null ? correspondente.getCpfcnpj() : "");
            data.put("correspondenteOab", correspondente.getOab() != null ? correspondente.getOab() : "");
            data.put("correspondenteTipo", correspondente.getTipocorrepondente() != null ? correspondente.getTipocorrepondente() : "");
            data.put("correspondenteTelefonePrimario", correspondente.getTelefoneprimario() != null ? correspondente.getTelefoneprimario() : "");
            data.put("correspondenteTelefoneSecundario", correspondente.getTelefonesecundario() != null ? correspondente.getTelefonesecundario() : "");
            data.put("correspondenteTelefoneCelularPrimario", correspondente.getTelefonecelularprimario() != null ? correspondente.getTelefonecelularprimario() : "");
            data.put("correspondenteTelefoneCelularSecundario", correspondente.getTelefonecelularsecundario() != null ? correspondente.getTelefonecelularsecundario() : "");
            data.put("correspondenteEmailPrimario", correspondente.getEmailprimario() != null ? correspondente.getEmailprimario() : "");
            data.put("correspondenteEmailSecundario", correspondente.getEmailsecundario() != null ? correspondente.getEmailsecundario() : "");
            data.put("correspondenteDataCadastro", correspondente.getDatacadastro() != null ? 
                     correspondente.getDatacadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
            data.put("correspondenteAtivo", correspondente.isAtivo() ? "Sim" : "Não");
            data.put("correspondenteObservacao", correspondente.getObservacao() != null ? correspondente.getObservacao() : "");
            
            // Add correspondente endereco data
            if (correspondente.getEnderecos() != null) {
                Endereco endereco = correspondente.getEnderecos();
                data.put("correspondenteEnderecoLogradouro", endereco.getLogradouro() != null ? endereco.getLogradouro() : "");
                data.put("correspondenteEnderecoNumero", endereco.getNumero() != null ? endereco.getNumero() : "");
                data.put("correspondenteEnderecoComplemento", endereco.getComplemento() != null ? endereco.getComplemento() : "");
                data.put("correspondenteEnderecoBairro", endereco.getBairro() != null ? endereco.getBairro() : "");
                data.put("correspondenteEnderecoCidade", endereco.getCidade() != null ? endereco.getCidade() : "");
                data.put("correspondenteEnderecoUf", endereco.getUf() != null ? endereco.getUf().getSigla() : "");
                data.put("correspondenteEnderecoCep", endereco.getCep() != null ? endereco.getCep() : "");
            } else {
                data.put("correspondenteEnderecoLogradouro", "");
                data.put("correspondenteEnderecoNumero", "");
                data.put("correspondenteEnderecoComplemento", "");
                data.put("correspondenteEnderecoBairro", "");
                data.put("correspondenteEnderecoCidade", "");
                data.put("correspondenteEnderecoUf", "");
                data.put("correspondenteEnderecoCep", "");
            }
        } else {
            // Set empty values for all correspondente fields
            data.put("correspondenteId", "");
            data.put("correspondenteNome", "");
            data.put("correspondenteResponsavel", "");
            data.put("correspondenteCpfcnpj", "");
            data.put("correspondenteOab", "");
            data.put("correspondenteTipo", "");
            data.put("correspondenteTelefonePrimario", "");
            data.put("correspondenteTelefoneSecundario", "");
            data.put("correspondenteTelefoneCelularPrimario", "");
            data.put("correspondenteTelefoneCelularSecundario", "");
            data.put("correspondenteEmailPrimario", "");
            data.put("correspondenteEmailSecundario", "");
            data.put("correspondenteDataCadastro", "");
            data.put("correspondenteAtivo", "");
            data.put("correspondenteObservacao", "");
            
            // Set empty values for correspondente endereco fields
            data.put("correspondenteEnderecoLogradouro", "");
            data.put("correspondenteEnderecoNumero", "");
            data.put("correspondenteEnderecoComplemento", "");
            data.put("correspondenteEnderecoBairro", "");
            data.put("correspondenteEnderecoCidade", "");
            data.put("correspondenteEnderecoUf", "");
            data.put("correspondenteEnderecoCep", "");
        }
        
        // Add complete processo data
        if (solicitacao.getProcesso() != null) {
            Processo processo = solicitacao.getProcesso();
            data.put("processoId", processo.getId() != null ? processo.getId() : "");
            data.put("processoNumero", processo.getNumeroprocesso() != null ? processo.getNumeroprocesso() : "");
            data.put("processoNumeroPesq", processo.getNumeroprocessopesq() != null ? processo.getNumeroprocessopesq() : "");
            data.put("processoParte", processo.getParte() != null ? processo.getParte() : "");
            data.put("processoAdverso", processo.getAdverso() != null ? processo.getAdverso() : "");
            data.put("processoPosicao", processo.getPosicao() != null ? processo.getPosicao() : "");
            data.put("processoStatus", processo.getStatus() != null ? processo.getStatus() : "");
            data.put("processoCartorio", processo.getCartorio() != null ? processo.getCartorio() : "");
            data.put("processoAssunto", processo.getAssunto() != null ? processo.getAssunto() : "");
            data.put("processoLocalizacao", processo.getLocalizacao() != null ? processo.getLocalizacao() : "");
            data.put("processoNumeroIntegracao", processo.getNumerointegracao() != null ? processo.getNumerointegracao() : "");
            data.put("processoComarca", processo.getComarca() != null && processo.getComarca().getNome() != null ? processo.getComarca().getNome() : "");
            data.put("processoOrgao", processo.getOrgao() != null && processo.getOrgao().getDescricao() != null ? processo.getOrgao().getDescricao() : "");
            data.put("processoNumOrgao", processo.getNumorgao() != null ? processo.getNumorgao().toString() : "");
            data.put("processoProcEletronico", processo.getProceletronico() != null ? processo.getProceletronico() : "");
            data.put("processoQuantsoli", processo.getQuantsoli() != null ? processo.getQuantsoli() : 0);
            data.put("processoDataDistribuicao", processo.getDatadistribuicao() != null ? 
                     processo.getDatadistribuicao().toString() : "");
            data.put("processoObservacao", processo.getObservacao() != null ? processo.getObservacao() : "");
        } else {
            // Set empty values for all processo fields
            data.put("processoId", "");
            data.put("processoNumero", "");
            data.put("processoNumeroPesq", "");
            data.put("processoParte", "");
            data.put("processoAdverso", "");
            data.put("processoPosicao", "");
            data.put("processoStatus", "");
            data.put("processoCartorio", "");
            data.put("processoAssunto", "");
            data.put("processoLocalizacao", "");
            data.put("processoNumeroIntegracao", "");
            data.put("processoComarca", "");
            data.put("processoOrgao", "");
            data.put("processoNumOrgao", "");
            data.put("processoProcEletronico", "");
            data.put("processoQuantsoli", 0);
            data.put("processoDataDistribuicao", "");
            data.put("processoObservacao", "");
        }
        
        reportData.add(data);
        
        // Generate the report using a specific template for solicitacao
        return generatePdfReport("solicitacao-report", parameters, reportData);
    }
}