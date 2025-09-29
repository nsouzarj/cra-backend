package br.adv.cra.dto;

import lombok.Data;
import java.util.Collection;
import java.util.Map;

@Data
public class ReportRequest {
    private Map<String, Object> parameters;
    private Collection<Object> data;
}