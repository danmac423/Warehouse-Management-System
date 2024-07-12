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

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken, worker));

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        if (jwtGenerator.validateToken(refreshToken)) {
            String username = jwtGenerator.getUsernameFromJwt(refreshToken);
            WorkerDto worker = workerDao.getWorkerByUsername(username).get();

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);

            String newAccessToken = jwtGenerator.generateAccessToken(authentication);

            return ResponseEntity.ok(new AuthResponseDto(newAccessToken, refreshToken, worker));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

    }




}
