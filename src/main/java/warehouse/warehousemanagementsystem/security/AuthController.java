package warehouse.warehousemanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse.warehousemanagementsystem.worker.Worker;
import warehouse.warehousemanagementsystem.worker.WorkerDao;

@RestController
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final WorkerDao workerDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          WorkerDao workerDao,
                          PasswordEncoder passwordEncoder,
                          JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.workerDao = workerDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

        Worker worker = new Worker(
                null,
                registerDto.username(),
                passwordEncoder.encode(registerDto.password()),
                registerDto.name(),
                registerDto.lastName(),
                registerDto.role().toUpperCase()
        );

        workerDao.addWorker(worker);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.username(), loginDto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);

    }




}
