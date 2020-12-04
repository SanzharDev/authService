package show.micro.authService.filters.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(2)
public class TokenInDbFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(TokenInDbFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getRequestURI().equals("/authenticate")) {
            logger.info(String.format(">>> CheckTokenInDbFilter %s", request.getAuthType()));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
