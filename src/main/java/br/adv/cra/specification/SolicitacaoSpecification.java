package br.adv.cra.specification;

import br.adv.cra.entity.Solicitacao;
import br.adv.cra.entity.Comarca;
import br.adv.cra.entity.Correspondente;
import br.adv.cra.entity.Processo;
import br.adv.cra.entity.Usuario;
import br.adv.cra.entity.StatusSolicitacao;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SolicitacaoSpecification {
    
    public static Specification<Solicitacao> comarcaIdEquals(Long comarcaId) {
        return (root, query, criteriaBuilder) -> {
            if (comarcaId == null) {
                return criteriaBuilder.conjunction();
            }
            
            // Join with comarca directly
            Join<Solicitacao, Comarca> comarcaJoin = root.join("comarca", JoinType.LEFT);
            
            // Join with processo to get comarca from processo
            Join<Solicitacao, Processo> processoJoin = root.join("processo", JoinType.LEFT);
            Join<Processo, Comarca> processoComarcaJoin = processoJoin.join("comarca", JoinType.LEFT);
            
            return criteriaBuilder.or(
                criteriaBuilder.equal(comarcaJoin.get("id"), comarcaId),
                criteriaBuilder.equal(processoComarcaJoin.get("id"), comarcaId)
            );
        };
    }
    
    public static Specification<Solicitacao> correspondenteIdEquals(Long correspondenteId) {
        return (root, query, criteriaBuilder) -> {
            if (correspondenteId == null) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Solicitacao, Correspondente> correspondenteJoin = root.join("correspondente", JoinType.LEFT);
            return criteriaBuilder.equal(correspondenteJoin.get("id"), correspondenteId);
        };
    }
    
    public static Specification<Solicitacao> processoIdEquals(Long processoId) {
        return (root, query, criteriaBuilder) -> {
            if (processoId == null) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Solicitacao, Processo> processoJoin = root.join("processo", JoinType.LEFT);
            return criteriaBuilder.equal(processoJoin.get("id"), processoId);
        };
    }
    
    public static Specification<Solicitacao> usuarioIdEquals(Long usuarioId) {
        return (root, query, criteriaBuilder) -> {
            if (usuarioId == null) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Solicitacao, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.equal(usuarioJoin.get("id"), usuarioId);
        };
    }
    
    public static Specification<Solicitacao> statusIdEquals(Long statusId) {
        return (root, query, criteriaBuilder) -> {
            if (statusId == null) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Solicitacao, StatusSolicitacao> statusJoin = root.join("statusSolicitacao", JoinType.LEFT);
            return criteriaBuilder.equal(statusJoin.get("idstatus"), statusId);
        };
    }
    
    public static Specification<Solicitacao> grupoEquals(Long grupo) {
        return (root, query, criteriaBuilder) -> {
            if (grupo == null) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.equal(root.get("grupo"), grupo.intValue());
        };
    }
    
    public static Specification<Solicitacao> statusExternoEquals(String statusExterno) {
        return (root, query, criteriaBuilder) -> {
            if (statusExterno == null || statusExterno.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.equal(root.get("statusexterno"), statusExterno);
        };
    }
    
    public static Specification<Solicitacao> statusEquals(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Solicitacao, StatusSolicitacao> statusJoin = root.join("statusSolicitacao", JoinType.LEFT);
            return criteriaBuilder.equal(statusJoin.get("status"), status);
        };
    }

    public static Specification<Solicitacao> textoContains(String texto) {
        return (root, query, criteriaBuilder) -> {
            if (texto == null || texto.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("observacao")), "%" + texto.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("instrucoes")), "%" + texto.toLowerCase() + "%")
            );
        };
    }
    
    public static Specification<Solicitacao> dataBetween(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, criteriaBuilder) -> {
            if (inicio == null && fim == null) {
                return criteriaBuilder.conjunction();
            }
            
            Path<LocalDateTime> dataPath = root.get("datasolicitacao");
            
            if (inicio != null && fim != null) {
                return criteriaBuilder.between(dataPath, inicio, fim);
            } else if (inicio != null) {
                return criteriaBuilder.greaterThanOrEqualTo(dataPath, inicio);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(dataPath, fim);
            }
        };
    }
    
    public static Specification<Solicitacao> pagoEquals(Boolean pago) {
        return (root, query, criteriaBuilder) -> {
            if (pago == null) {
                return criteriaBuilder.conjunction();
            }
            
            String pagoValue = pago ? "true" : "false";
            return criteriaBuilder.equal(root.get("pago"), pagoValue);
        };
    }
    
    public static Specification<Solicitacao> concluidaEquals(Boolean concluida) {
        return (root, query, criteriaBuilder) -> {
            if (concluida == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (concluida) {
                return criteriaBuilder.isNotNull(root.get("dataconclusao"));
            } else {
                return criteriaBuilder.isNull(root.get("dataconclusao"));
            }
        };
    }
    
    public static Specification<Solicitacao> atrasadaEquals(Boolean atrasada) {
        return (root, query, criteriaBuilder) -> {
            if (atrasada == null || !atrasada) {
                return criteriaBuilder.conjunction();
            }
            
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.and(
                criteriaBuilder.isNull(root.get("dataconclusao")),
                criteriaBuilder.lessThan(root.get("dataagendamento"), now)
            );
        };
    }
    
    public static Specification<Solicitacao> numeroContains(String numero) {
        return (root, query, criteriaBuilder) -> {
            if (numero == null || numero.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("numero")), "%" + numero.toLowerCase() + "%");
        };
    }
    
    public static Specification<Solicitacao> varaContains(String vara) {
        return (root, query, criteriaBuilder) -> {
            if (vara == null || vara.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("vara")), "%" + vara.toLowerCase() + "%");
        };
    }
    
    public static Specification<Solicitacao> requerenteContains(String requerente) {
        return (root, query, criteriaBuilder) -> {
            if (requerente == null || requerente.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("requerente")), "%" + requerente.toLowerCase() + "%");
        };
    }
    
    public static Specification<Solicitacao> requeridoContains(String requerido) {
        return (root, query, criteriaBuilder) -> {
            if (requerido == null || requerido.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("requerido")), "%" + requerido.toLowerCase() + "%");
        };
    }
    
    public static Specification<Solicitacao> ufEquals(String uf) {
        return (root, query, criteriaBuilder) -> {
            if (uf == null || uf.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.equal(root.get("uf"), uf);
        };
    }
}