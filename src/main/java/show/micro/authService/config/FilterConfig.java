package show.micro.authService.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import show.micro.authService.filters.jwt.JwtPresentFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtPresentFilter> jwtFilter() {
        FilterRegistrationBean<JwtPresentFilter> jwtPresentFilterFilterRegistrationBean
                = new FilterRegistrationBean<>();
        // JwtPresentFilter works only if URL pattern matches '/verifyToken/*'
        jwtPresentFilterFilterRegistrationBean.setFilter(new JwtPresentFilter());
        jwtPresentFilterFilterRegistrationBean.addUrlPatterns("/verifyToken/*");

        return jwtPresentFilterFilterRegistrationBean;
    }

}
