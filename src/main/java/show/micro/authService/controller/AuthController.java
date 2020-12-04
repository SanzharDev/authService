package show.micro.authService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import show.micro.authService.dto.Request;
import show.micro.authService.utils.JwtTokenUtil;
import show.micro.authService.service.UserDetailsServiceImpl;

import java.util.Objects;

@RestController
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final AuthenticationManager authenticationManager;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService, AuthenticationManager authenticationManager) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsServiceImpl = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody Request request, @RequestHeader HttpHeaders requestHeaders) {
        logger.info(String.format(">>> Accepted request in /authenticate. Request headers: %s", requestHeaders.toString()));
        try {
            doAuthentication(request.getUsername(), request.getPassword());
        } catch (Exception e) {
            logger.error(String.format(">>>Error authenticating user %s", request.getUsername()));
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        String token = jwtTokenUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping(
            value = "/verifyToken",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.ALL_VALUE})
    public ResponseEntity<?> verifyToken(@RequestBody Request request, @RequestHeader HttpHeaders requestHeaders)  throws Exception{
        logger.info(String.format(">>> Accepted request in /authenticate. Request body: %s", request));
        logger.info(String.format(">>> Request headers: %s", requestHeaders));
       return ResponseEntity.ok("Token is valid");
    }

    @PostMapping(
            value = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> register(@RequestBody Request request) {
        // save a user in a db, if needed there can be generated token and send back to the user
        userDetailsServiceImpl.save(request);
        return ResponseEntity.ok("Added user");
    }

    private void doAuthentication(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception(String.format("User %s DISABLED", username), e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
