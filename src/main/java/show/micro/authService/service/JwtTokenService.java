package show.micro.authService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import show.micro.authService.entity.JwtToken;
import show.micro.authService.repostitory.JwtTokenRepository;

@Service
public class JwtTokenService {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    public JwtToken getOne(String token) {
        return jwtTokenRepository.getOne(token);
    }

    public void save(JwtToken token) {
        jwtTokenRepository.save(token);
    }
}
