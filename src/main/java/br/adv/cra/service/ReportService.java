package br.adv.cra.service;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ReportService {

    /**
     * Generates a PDF report from a Jasper report template and data.
     * 
     * @param reportName Name of the Jasper report template file (without .jrxml extension)
     * @param parameters Map of parameters to pass to the report
     * @param data       Collection of data to fill the report
     * @return Byte array containing the generated PDF report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generatePdfReport(String reportName, Map<String, Object> parameters, Collection<?> data) throws JRException {
        return generateReport(reportName, parameters, data, "PDF");
    }

    /**
     * Loads a JasperReport, compiling it from .jrxml if the .jasper file is not found.
     *
     * @param reportName The name of the report.
     * @return A compiled JasperReport object.
     * @throws JRException if the report cannot be loaded or compiled.
     * @throws IOException if there is an issue reading the report file.
     */
    private JasperReport loadReport(String reportName) throws JRException, IOException {
        String reportPath = "reports/" + reportName;
        ClassPathResource jasperResource = new ClassPathResource(reportPath + ".jasper");

        if (jasperResource.exists()) {
            try (InputStream reportStream = jasperResource.getInputStream()) {
                return (JasperReport) JRLoader.loadObject(reportStream);
            }
        }

        ClassPathResource jrxmlResource = new ClassPathResource(reportPath + ".jrxml");
        if (!jrxmlResource.exists()) {
            throw new JRException("Report template not found: " + reportName);
        }

        try (InputStream jrxmlStream = jrxmlResource.getInputStream()) {
            JasperDesign jasperDesign = JRXmlLoader.load(jrxmlStream);
            return JasperCompileManager.compileReport(jasperDesign);
        }
    }

    /**
     * Generates a report in a specific format.
     * 
     * @param reportName Name of the Jasper report template file (without .jrxml extension)
     * @param parameters Map of parameters to pass to the report
     * @param data       Collection of data to fill the report
     * @param outputType Output format (PDF, HTML, XLS, etc.)
     * @return Byte array containing the generated report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType) throws JRException {
        try {
            JasperReport jasperReport = loadReport(reportName);
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRExporter exporter;

            switch (outputType.toUpperCase()) {
                case "PDF":
                    exporter = new JRPdfExporter();
                    break;
                case "XLS":
                    // To use XLS, you would need the 'jasperreports-poi' dependency
                    // exporter = new JRXlsExporter();
                    throw new JRException("XLS export not implemented or dependency missing.");
                default:
                    throw new IllegalArgumentException("Unsupported output type: " + outputType);
            }

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.exportReport();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new JRException("Error generating report: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a PDF report for a specific solicitacao.
     * 
     * @param solicitacao The solicitacao entity to generate the report for
     * @return Byte array containing the generated PDF report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generateSolicitacaoReport(Solicitacao solicitacao) throws JRException {
        // Prepare parameters for the report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Detalhes da Solicitação");

        // Prepare data map for the report
        Map<String, Object> data = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Helper function to safely get values and provide a default
        Function<Object, Object> safeGet = (value) -> value != null ? value : "";

        // Solicitacao details
        data.put("id", safeGet.apply(solicitacao.getId()));
        data.put("numero", safeGet.apply(solicitacao.getNumero()));
        data.put("dataSolicitacao", solicitacao.getDatasolicitacao() != null ? solicitacao.getDatasolicitacao().format(formatter) : "");
        data.put("dataConclusao", solicitacao.getDataconclusao() != null ? solicitacao.getDataconclusao().format(formatter) : "");
        data.put("dataAgendamento", solicitacao.getDataagendamento() != null ? solicitacao.getDataagendamento().format(formatter) : "");
        data.put("dataPrazo", solicitacao.getDataprazo() != null ? solicitacao.getDataprazo().format(formatter) : "");
        data.put("vara", safeGet.apply(solicitacao.getVara()));
        data.put("uf", safeGet.apply(solicitacao.getUf()));
        data.put("requerente", safeGet.apply(solicitacao.getRequerente()));
        data.put("requerido", safeGet.apply(solicitacao.getRequerido()));
        data.put("observacao", safeGet.apply(solicitacao.getObservacao()));
        data.put("instrucoes", safeGet.apply(solicitacao.getInstrucoes()));
        data.put("complemento", safeGet.apply(solicitacao.getComplemento()));
        data.put("justificativa", safeGet.apply(solicitacao.getJustificativa()));
        data.put("tratposaudiencia", safeGet.apply(solicitacao.getTratposaudiencia()));
        data.put("numcontrole", safeGet.apply(solicitacao.getNumcontrole()));
        data.put("tempreposto", solicitacao.isTempreposto() ? "Sim" : "Não");
        data.put("convolada", solicitacao.isConvolada() ? "Sim" : "Não");
        data.put("horaudiencia", safeGet.apply(solicitacao.getHoraudiencia()));
        data.put("statusexterno", safeGet.apply(solicitacao.getStatusexterno()));
        data.put("valor", safeGet.apply(solicitacao.getValor()));
        data.put("valordaalcada", safeGet.apply(solicitacao.getValordaalcada()));
        data.put("emailenvio", safeGet.apply(solicitacao.getEmailenvio()));
        data.put("pago", safeGet.apply(solicitacao.getPago()));
        data.put("grupo", Optional.ofNullable(solicitacao.getGrupo()).map(Object::toString).orElse(""));
        data.put("propostaacordo", solicitacao.isPropostaacordo() ? "Sim" : "Não");
        data.put("audinterna", solicitacao.isAudinterna() ? "Sim" : "Não");
        data.put("lide", safeGet.apply(solicitacao.getLide()));
        data.put("avaliacaonota", Optional.ofNullable(solicitacao.getAvaliacaonota()).map(Object::toString).orElse(""));
        data.put("textoavaliacao", safeGet.apply(solicitacao.getTextoavaliacao()));

        // Related entities
        data.put("comarca", Optional.ofNullable(solicitacao.getComarca()).map(c -> safeGet.apply(c.getNome())).orElse(""));
        data.put("processo", Optional.ofNullable(solicitacao.getProcesso()).map(p -> safeGet.apply(p.getNumeroprocesso())).orElse(""));
        data.put("status", Optional.ofNullable(solicitacao.getStatusSolicitacao()).map(s -> safeGet.apply(s.getStatus())).orElse(""));
        data.put("tipoSolicitacao", Optional.ofNullable(solicitacao.getTipoSolicitacao()).map(t -> safeGet.apply(t.getEspecie())).orElse(""));
        data.put("correspondente", Optional.ofNullable(solicitacao.getCorrespondente()).map(c -> safeGet.apply(c.getNome())).orElse(""));
        data.put("usuario", Optional.ofNullable(solicitacao.getUsuario()).map(u -> safeGet.apply(u.getNomecompleto())).orElse(""));

        // Complete Correspondente data
        Correspondente correspondente = solicitacao.getCorrespondente();
        if (correspondente != null) {
            data.put("correspondenteId", safeGet.apply(correspondente.getId()));
            data.put("correspondenteNome", safeGet.apply(correspondente.getNome()));
            data.put("correspondenteResponsavel", safeGet.apply(correspondente.getResponsavel()));
            data.put("correspondenteCpfcnpj", safeGet.apply(correspondente.getCpfcnpj()));
            data.put("correspondenteOab", safeGet.apply(correspondente.getOab()));
            data.put("correspondenteTipo", safeGet.apply(correspondente.getTipocorrepondente()));
            data.put("correspondenteTelefonePrimario", safeGet.apply(correspondente.getTelefoneprimario()));
            data.put("correspondenteTelefoneSecundario", safeGet.apply(correspondente.getTelefonesecundario()));
            data.put("correspondenteTelefoneCelularPrimario", safeGet.apply(correspondente.getTelefonecelularprimario()));
            data.put("correspondenteTelefoneCelularSecundario", safeGet.apply(correspondente.getTelefonecelularsecundario()));
            data.put("correspondenteEmailPrimario", safeGet.apply(correspondente.getEmailprimario()));
            data.put("correspondenteEmailSecundario", safeGet.apply(correspondente.getEmailsecundario()));
            data.put("correspondenteDataCadastro", correspondente.getDatacadastro() != null ? correspondente.getDatacadastro().format(formatter) : "");
            data.put("correspondenteAtivo", correspondente.isAtivo() ? "Sim" : "Não");
            data.put("correspondenteObservacao", safeGet.apply(correspondente.getObservacao()));

            Endereco endereco = correspondente.getEnderecos();
            if (endereco != null) {
                data.put("correspondenteEnderecoLogradouro", safeGet.apply(endereco.getLogradouro()));
                data.put("correspondenteEnderecoNumero", safeGet.apply(endereco.getNumero()));
                data.put("correspondenteEnderecoComplemento", safeGet.apply(endereco.getComplemento()));
                data.put("correspondenteEnderecoBairro", safeGet.apply(endereco.getBairro()));
                data.put("correspondenteEnderecoCidade", safeGet.apply(endereco.getCidade()));
                data.put("correspondenteEnderecoUf", Optional.ofNullable(endereco.getUf()).map(u -> safeGet.apply(u.getSigla())).orElse(""));
                data.put("correspondenteEnderecoCep", safeGet.apply(endereco.getCep()));
            }
        }

        // Complete Processo data
        Processo processo = solicitacao.getProcesso();
        if (processo != null) {
            data.put("processoId", safeGet.apply(processo.getId()));
            data.put("processoNumero", safeGet.apply(processo.getNumeroprocesso()));
            data.put("processoNumeroPesq", safeGet.apply(processo.getNumeroprocessopesq()));
            data.put("processoParte", safeGet.apply(processo.getParte()));
            data.put("processoAdverso", safeGet.apply(processo.getAdverso()));
            data.put("processoPosicao", safeGet.apply(processo.getPosicao()));
            data.put("processoStatus", safeGet.apply(processo.getStatus()));
            data.put("processoCartorio", safeGet.apply(processo.getCartorio()));
            data.put("processoAssunto", safeGet.apply(processo.getAssunto()));
            data.put("processoLocalizacao", safeGet.apply(processo.getLocalizacao()));
            data.put("processoNumeroIntegracao", safeGet.apply(processo.getNumerointegracao()));
            data.put("processoComarca", Optional.ofNullable(processo.getComarca()).map(c -> safeGet.apply(c.getNome())).orElse(""));
            data.put("processoOrgao", Optional.ofNullable(processo.getOrgao()).map(o -> safeGet.apply(o.getDescricao())).orElse(""));
            data.put("processoNumOrgao", Optional.ofNullable(processo.getNumorgao()).map(Object::toString).orElse(""));
            data.put("processoProcEletronico", safeGet.apply(processo.getProceletronico()));
            data.put("processoQuantsoli", processo.getQuantsoli() != null ? processo.getQuantsoli() : 0);
            data.put("processoDataDistribuicao", Optional.ofNullable(processo.getDatadistribuicao()).map(Object::toString).orElse(""));
            data.put("processoObservacao", safeGet.apply(processo.getObservacao()));
        }

        // Generate the report using a specific template for solicitacao
        try {
            return generatePdfReport("solicitacao-report", parameters, Collections.singletonList(data));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JRException("Error generating solicitacao report: " + e.getMessage(), e);
        }
    }
}