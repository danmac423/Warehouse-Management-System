package warehouse.warehousemanagementsystem.security;

public record AuthResponse(String accessToken, String tokenType) {
    public AuthResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
