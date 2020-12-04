package show.micro.authService.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import show.micro.authService.entity.JwtToken;
import show.micro.authService.exception.TokenNotInDatabaseException;
import show.micro.authService.service.JwtTokenService;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -988314720L;

    public static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 HOUR

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final String SECRET_KEY = "microshow";

    private Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    private JwtTokenService tokenService;

    public JwtTokenUtil(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    // fetches all claims from token, using secretKey
    private Claims getAllClaimsFromToken(String token) throws Exception {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // used to get some specific standard claim from token
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) throws Exception {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) throws Exception {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getIssuerFromToken(String token) throws Exception{
        return getClaimFromToken(token, Claims::getIssuer);
    }

    public Date getIssuedTimeFromToken(String token) throws Exception {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationTimeFromToken(String token) throws Exception {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) throws Exception {
        return getClaimFromToken(token, Claims::getExpiration).after(new Date());
    }

    // Logic here need to be changed for better usage
    public boolean isValid(String token, UserDetails userDetails) throws Exception{
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // why do we check username again?
    }

    public void isTokenInDb(String token) throws Exception {
        if (tokenService.getOne(token) != null) {
            throw new TokenNotInDatabaseException();
        }
    }

    public String refreshToken() {
        return ";;";
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
     }

     /**Steps to create a token:
           1. Define claims of the token, like Issuer, Expiration, Subject, ID, etc.
           2. Sign the JWT using the HS512 algorithm and secret key.
           3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
           compaction of the JWT to a URL-safe string
      */
     private String doGenerateToken(Map<String, Object> claims, String username) {
         byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
         Key signingKey = new SecretKeySpec(secretKeyBytes, SIGNATURE_ALGORITHM.getJcaName());
         String token = Jwts.builder().
                 setClaims(claims).
                 setSubject(username).
                 setIssuer("Sanzhars auth service").
                 setIssuedAt(new Date(System.currentTimeMillis())).
                 setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)).
                 signWith(SIGNATURE_ALGORITHM, signingKey).
                 compact();
         // save token to DB
        tokenService.save(new JwtToken(token));
         return token;
     }

}
