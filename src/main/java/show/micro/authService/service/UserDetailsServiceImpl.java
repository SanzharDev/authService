package show.micro.authService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import show.micro.authService.dto.Request;
import show.micro.authService.entity.User;
import show.micro.authService.repostitory.UserRepository;

import java.util.Arrays;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info(String.format(">>> %s loads user %s", UserDetailsServiceImpl.class.getName(), username));
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("User %s doesn't exist in database", username)));
    }

    public void save(Request request) {
        userRepository.save(
                new User(
                        null,
                        request.getUsername(),
                        request.getEmail(),
                        passwordEncoder.encode(request.getPassword()),
                        Arrays.asList(roleService.findByName("USER"), roleService.findByName("ADMIN"))
                )
        );
    }
}
