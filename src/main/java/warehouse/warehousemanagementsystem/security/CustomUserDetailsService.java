package warehouse.warehousemanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.worker.WorkerDto;
import warehouse.warehousemanagementsystem.worker.WorkerDao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final WorkerDao workerDao;

    @Autowired
    public CustomUserDetailsService(WorkerDao workerDao) {
        this.workerDao = workerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WorkerDto worker = workerDao.getWorkerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new User(
                worker.username(),
                worker.password(),
                mapRolesToAuthorities(List.of(worker.role()))
        );
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


}
