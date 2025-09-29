package br.adv.cra.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmailRequest {
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String text;
    private boolean isHtml = false;
}