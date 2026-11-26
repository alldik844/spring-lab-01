
package kz.iitu.springlab.security;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AppUserRepository repository;

    public AdminService(AppUserRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<AppUser> getAllUsers() {
        return repository.findAll(
                Sort.by(Sort.Direction.ASC, "username")
        );
    }
}
