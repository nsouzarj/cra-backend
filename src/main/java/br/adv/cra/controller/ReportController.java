package br.adv.cra.controller;

import br.adv.cra.dto.ReportRequest;
import br.adv.cra.entity.Solicitacao;
import br.adv.cra.service.ReportService;
import br.adv.cra.service.SolicitacaoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private SolicitacaoService solicitacaoService;

    /**
     * Generates a PDF report for a specific solicitacao by ID
     * 
     * @param solicitacaoId ID of the solicitacao to generate report for
     * @return ResponseEntity with the generated PDF report
     */
    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<byte[]> generateSolicitacaoReport(@PathVariable Long solicitacaoId) {
        try {
            // Fetch the solicitacao by ID
            Optional<Solicitacao> solicitacaoOpt = solicitacaoService.buscarPorId(solicitacaoId);
            
            if (!solicitacaoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("Solicitação não encontrada com ID: " + solicitacaoId).getBytes());
            }
            
            Solicitacao solicitacao = solicitacaoOpt.get();
            
            // Generate the PDF report
            byte[] report = reportService.generateSolicitacaoReport(solicitacao);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "solicitacao-" + solicitacaoId + ".pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Invalid request: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generates a PDF report
     * 
     * @param reportName Name of the report template
     * @param request    ReportRequest containing parameters and data
     * @return ResponseEntity with the generated PDF report
     */
    @PostMapping("/{reportName}/pdf")
    public ResponseEntity<byte[]> generatePdfReport(
            @PathVariable String reportName,
            @RequestBody ReportRequest request) {
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) request.getParameters();
            @SuppressWarnings("unchecked")
            Collection<Object> data = (Collection<Object>) request.getData();

            byte[] report = reportService.generatePdfReport(reportName, parameters, data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", reportName + ".pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Invalid request: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generates a report in a specific format
     * 
     * @param reportName Name of the report template
     * @param format     Output format (PDF, HTML, XLS)
     * @param request    ReportRequest containing parameters and data
     * @return ResponseEntity with the generated report
     */
    @PostMapping("/{reportName}/{format}")
    public ResponseEntity<byte[]> generateReport(
            @PathVariable String reportName,
            @PathVariable String format,
            @RequestBody ReportRequest request) {
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) request.getParameters();
            @SuppressWarnings("unchecked")
            Collection<Object> data = (Collection<Object>) request.getData();

            byte[] report = reportService.generateReport(reportName, parameters, data, format.toUpperCase());

            MediaType mediaType;
            String fileExtension;
            
            switch (format.toUpperCase()) {
                case "PDF":
                    mediaType = MediaType.APPLICATION_PDF;
                    fileExtension = ".pdf";
                    break;
                case "HTML":
                    mediaType = MediaType.TEXT_HTML;
                    fileExtension = ".html";
                    break;
                case "XLS":
                    mediaType = MediaType.valueOf("application/vnd.ms-excel");
                    fileExtension = ".xls";
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(("Unsupported format: " + format).getBytes());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData("attachment", reportName + fileExtension);

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Invalid request: " + e.getMessage()).getBytes());
        }
    }
}