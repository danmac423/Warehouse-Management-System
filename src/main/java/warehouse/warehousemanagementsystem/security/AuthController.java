package warehouse.warehousemanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.worker.WorkerDao;
import warehouse.warehousemanagementsystem.worker.WorkerDto;

@RestController
//@CrossOrigin
@RequestMapping(path = "api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final WorkerDao workerDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final RefreshTokenDao refreshTokenDao;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          WorkerDao workerDao,
                          PasswordEncoder passwordEncoder,
                          JwtGenerator jwtGenerator,
                          RefreshTokenDao refreshTokenDao) {
        this.authenticationManager = authenticationManager;
        this.workerDao = workerDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenDao = refreshTokenDao;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

        WorkerDto worker = new WorkerDto(
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        WorkerDto worker = workerDao.getWorkerById(changePasswordDto.workerId()).orElseThrow(() -> new RuntimeException("Worker not found"));
        workerDao.changePassword(worker.id(), passwordEncoder.encode(changePasswordDto.newPassword()));
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.username(), loginDto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtGenerator.generateAccessToken(authentication);
        String refreshToken = jwtGenerator.generateRefreshToken(authentication);

        WorkerDto worker = workerDao.getWorkerByUsername(loginDto.username()).get();
        WorkerDto workerWithoutPassword = new WorkerDto(worker.id(), worker.username(), null, worker.name(), worker.lastName(), worker.role());



        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken, workerWithoutPassword));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto>  refresh(@RequestBody RefreshDto refreshDto) {
        String storedToken = refreshTokenDao.getRefreshToken(refreshDto.username());
        if (storedToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (storedToken.equals(refreshDto.refreshToken())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(refreshDto.username(), null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String newAccessToken = jwtGenerator.generateAccessToken(authentication);

            return ResponseEntity.ok(new AuthResponseDto(newAccessToken, refreshDto.refreshToken(), null));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


}
