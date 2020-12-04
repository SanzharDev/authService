package show.micro.authService.filters.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import show.micro.authService.exception.AuthorizationHeaderException;
import show.micro.authService.exception.UsernameExtractionException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

@Order(1)
public class JwtPresentFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(JwtPresentFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // casting servlet Request/Response to HttpRequest/HttpResponse
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // log requesting URI
        logger.info(String.format(">>> JwtPresentFilter accepted request %s", request.getRequestURI()));
        // Extract Authorization header from request
        String authorizationHeader = request.getHeader("Authorization");
        try {
            // If there is no Authorization header or it doesn't start with 'Bearer ', response with 401
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new AuthorizationHeaderException();
            }
            // Fetch JWS (JWT Secured) from Authorization header, skipping 'Bearer ' prefix
            final String jwtToken = authorizationHeader.substring(7);
            // Extract username from Subject claim in JWS
            String username = null;
            Claims parsedJwsBody = Jwts.parser().
                    setSigningKey(DatatypeConverter.parseBase64Binary("microshow")).
                    parseClaimsJws(jwtToken).getBody();
            username = parsedJwsBody.getSubject();
            // If username is null, response with 401
            if (username == null) {
                throw new UsernameExtractionException();
            }
        } catch (Exception e) {
            // stop entire filter chain and reject request with 401 status code
            logger.error(e.getMessage());
            response.sendError(401, e.getMessage());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
