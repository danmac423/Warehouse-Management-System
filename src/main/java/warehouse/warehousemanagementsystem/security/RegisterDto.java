package warehouse.warehousemanagementsystem.security;

public record RegisterDto (String username, String password, String name, String lastName, String role) {
}