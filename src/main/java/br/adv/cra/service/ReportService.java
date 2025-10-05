package br.adv.cra.service;

import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Endereco;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Solicitacao;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

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
            log.debug("Carregando .jasper pré-compilado: {}", reportPath + ".jasper");
            try (InputStream reportStream = jasperResource.getInputStream()) {
                Object loaded = JRLoader.loadObject(reportStream);
                if (loaded instanceof JasperReport) {
                    return (JasperReport) loaded;
                } else {
                    throw new JRException("Arquivo .jasper inválido: não é um JasperReport");
                }
            } catch (Exception e) {
                log.error("Falha ao carregar .jasper: {}", reportPath + ".jasper", e);
                throw new JRException("Falha ao carregar .jasper para " + reportName, e);
            }
        }

        log.debug("Nenhum .jasper encontrado, compilando de .jrxml: {}", reportPath + ".jrxml");
        ClassPathResource jrxmlResource = new ClassPathResource(reportPath + ".jrxml");
        if (!jrxmlResource.exists()) {
            log.error("Template JRXML não encontrado: {}", reportPath + ".jrxml");
            throw new JRException("Template do relatório não encontrado: " + reportName);
        }

        try (InputStream jrxmlStream = jrxmlResource.getInputStream()) {
            // CORREÇÃO: Define o diretório temp para evitar erros de permissão (usa /tmp, que é gravável)
            System.setProperty("net.sf.jasperreports.compiler.temp.dir", "/tmp");
            log.debug("Diretório temp configurado para: /tmp");

            net.sf.jasperreports.engine.design.JasperDesign jasperDesign = JRXmlLoader.load(jrxmlStream);
            JasperReport compiled = JasperCompileManager.compileReport(jasperDesign);
            log.debug("JRXML compilado com sucesso");
            return compiled;
        } catch (JRException e) {
            log.error("Falha ao compilar JRXML (versão 7.x pode ser rigorosa com schema): {}", reportPath + ".jrxml", e);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao compilar JRXML: {}", reportPath + ".jrxml", e);
            throw new JRException("Falha na compilação do JRXML para " + reportName, e);
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
    public byte[] generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType)
            throws JRException {
        
        JasperReport jasperReport = null;
        JasperPrint jasperPrint = null;
        try {
            log.info("Iniciando geração de relatório: {} (formato: {})", reportName, outputType);
            
            jasperReport = loadReport(reportName);
            log.debug("Relatório carregado com sucesso: {}", jasperReport.getName());
            
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            log.debug("Datasource preparado com {} itens", data.size());
            
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            log.debug("Relatório preenchido com sucesso: {} páginas", jasperPrint.getPages().size());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            

            switch (outputType.toUpperCase()) {
                case "PDF":
                    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                    log.debug("Exportação PDF concluída: {} bytes", outputStream.size());
                    break;

                case "XLS":
                case "XLSX":
                    exportReportToXlsxStream(jasperPrint, outputStream);
                    log.debug("Exportação XLSX concluída: {} bytes", outputStream.size());
                    break;

                default:
                    throw new IllegalArgumentException("Formato de saída não suportado: " + outputType);
            }

            return outputStream.toByteArray();
        } catch (JRException e) {
            log.error("Erro JRException ao gerar relatório '{}': {}", reportName, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Erro geral ao gerar relatório '{}' (formato: {}): {}", reportName, outputType, 
                      e.getMessage() != null ? e.getMessage() : "Exceção sem mensagem (ver stack trace)", e);
            throw new JRException("Erro ao gerar relatório '" + reportName + "': " + 
                                  (e.getMessage() != null ? e.getMessage() : "Ver logs para detalhes"), e);
        }
    }

    /**
     * Exports a JasperPrint object to an XLSX stream.
     *
     * @param jasperPrint The JasperPrint object to export.
     * @param outputStream The stream to write the XLSX content to.
     * @throws JRException If there's an error during the export process.
     */
    private void exportReportToXlsxStream(JasperPrint jasperPrint, ByteArrayOutputStream outputStream) throws JRException {
        JRXlsxExporter xlsxExporter = new JRXlsxExporter();
        xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        SimpleXlsxReportConfiguration xlsxConfig = new SimpleXlsxReportConfiguration();
        xlsxConfig.setOnePagePerSheet(true);
        xlsxConfig.setDetectCellType(true);
        xlsxExporter.setConfiguration(xlsxConfig);
        xlsxExporter.exportReport();
    }

    /**
     * Generates a PDF report for a specific solicitacao.
     * 
     * @param solicitacao The solicitacao entity to generate the report for
     * @return Byte array containing the generated PDF report
     * @throws JRException If there's an error during report generation
     */
    public byte[] generateSolicitacaoReport(Solicitacao solicitacao) throws JRException {
        log.info("Gerando relatório para solicitação ID: {}", solicitacao.getId());
        
        // Prepare parameters for the report - USE HashMap para permitir modificações do Jasper
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Detalhes da Solicitação");

        // Prepare data map for the report
        Map<String, Object> data = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Helper function to safely get values and provide a default
        Function<Object, Object> safeGet = (value) -> value != null ? value : "";

        // Solicitacao details (usando Optional para consistência e evitar nulls)
        data.put("id", safeGet.apply(solicitacao.getId()));
        data.put("numero", safeGet.apply(solicitacao.getNumero()));
        data.put("dataSolicitacao", Optional.ofNullable(solicitacao.getDatasolicitacao())
                .map(d -> d.format(formatter)).orElse("N/A"));
        data.put("dataConclusao", Optional.ofNullable(solicitacao.getDataconclusao())
                .map(d -> d.format(formatter)).orElse("N/A"));
        data.put("dataAgendamento", Optional.ofNullable(solicitacao.getDataagendamento())
                .map(d -> d.format(formatter)).orElse("N/A"));
        data.put("dataPrazo", Optional.ofNullable(solicitacao.getDataprazo())
                .map(d -> d.format(formatter)).orElse("N/A"));
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
            data.put("correspondenteDataCadastro", Optional.ofNullable(correspondente.getDatacadastro())
                    .map(d -> d.format(formatter)).orElse("N/A"));
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
            data.put("processoQuantsoli", Optional.ofNullable(processo.getQuantsoli()).orElse(0));
            data.put("processoDataDistribuicao", Optional.ofNullable(processo.getDatadistribuicao())
                    .map(Object::toString).orElse("N/A"));
            data.put("processoObservacao", safeGet.apply(processo.getObservacao()));
        }

        // Log data summary for debugging
        log.debug("Dados preparados para solicitação {}: {} chaves no Map", solicitacao.getId(), data.size());

        // Generate the report using a specific template for solicitacao
        try {
            return generatePdfReport("solicitacao-report", parameters, Collections.singletonList(data));
        } catch (Exception e) {
            log.error("Erro ao gerar relatório de solicitação ID {}: {}", solicitacao.getId(), e.getMessage(), e);
            throw new JRException("Erro ao gerar relatório de solicitação: " + e.getMessage(), e);
        }
    }
}