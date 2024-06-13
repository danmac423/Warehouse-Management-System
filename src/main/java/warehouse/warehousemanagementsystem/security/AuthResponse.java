package warehouse.warehousemanagementsystem.security;

public record AuthResponse(String accessToken, String tokenType, String role, Long workerId) {
    public AuthResponse(String accessToken, String role, Long workerId) {
        this(accessToken, "Bearer", role, workerId);
    }
}
