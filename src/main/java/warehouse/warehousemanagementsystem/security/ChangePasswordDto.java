package warehouse.warehousemanagementsystem.security;

public record ChangePasswordDto(Long workerId, String newPassword) {
}
