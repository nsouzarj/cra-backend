package br.adv.cra.security;

import br.adv.cra.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private String nomeCompleto;
    private String email;
    private Integer tipo;
    private boolean ativo;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = getAuthoritiesForUserType(usuario.getTipo());
        
        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getSenha(),
                usuario.getNomecompleto(),
                usuario.getEmailprincipal(),
                usuario.getTipo(),
                usuario.isAtivo(),
                authorities
        );
    }
    
    private static List<GrantedAuthority> getAuthoritiesForUserType(Integer tipo) {
        String role = switch (tipo) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_ADVOGADO";
            case 3 -> "ROLE_CORRESPONDENTE";
            default -> "ROLE_USER";
        };
        
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}