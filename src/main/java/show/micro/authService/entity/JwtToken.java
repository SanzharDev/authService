package show.micro.authService.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class JwtToken {

    @Id
    private String token;

    public JwtToken() {
    }

    public JwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
