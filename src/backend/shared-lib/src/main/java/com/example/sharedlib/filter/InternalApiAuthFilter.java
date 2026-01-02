package com.example.sharedlib.filter;

import com.example.sharedlib.jwt.JwtValidator;
import io.jsonwebtoken.Claims;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter for authenticating internal API calls using JWT
 */
public class InternalApiAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    public InternalApiAuthFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            
            if (token != null) {
                Claims claims = jwtValidator.validateToken(token);
                
                // Extract subject (user ID)
                String subject = claims.getSubject();
                
                // Extract roles from claims
                List<String> roles = extractRoles(claims);
                
                // Create authorities from roles
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                
                // Set authentication in security context
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Failed to authenticate internal API call", e);
            // Continue filter chain even if authentication fails
            // The downstream service should handle unauthorized access
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     *
     * @param request HTTP request
     * @return JWT token string or null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Extract roles from JWT claims
     *
     * @param claims JWT claims
     * @return List of role names
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Claims claims) {
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        return new ArrayList<>();
    }
}
