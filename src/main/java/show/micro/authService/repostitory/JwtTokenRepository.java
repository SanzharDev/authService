package show.micro.authService.repostitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import show.micro.authService.entity.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {
}
