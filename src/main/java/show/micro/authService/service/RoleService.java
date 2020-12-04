package show.micro.authService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import show.micro.authService.entity.Role;
import show.micro.authService.repostitory.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void save(Role role) {
        roleRepository.save(role);
    }

    public Role findByName(String role) {
        return roleRepository.findByName(role);
    }
}
