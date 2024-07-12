package warehouse.warehousemanagementsystem.security;

//public record AuthResponseDto(String accessToken, String tokenType, String role, Long workerId) {
//    public AuthResponseDto(String accessToken, String role, Long workerId) {
//        this(accessToken, "Bearer", role, workerId);
//    }
//}

import warehouse.warehousemanagementsystem.worker.WorkerDto;

public record AuthResponseDto(String tokenType, String accessToken, String refreshToken, WorkerDto workerDto) {
    public AuthResponseDto(String accessToken, String refreshToken, WorkerDto workerDto) {
        this("Bearer", accessToken, refreshToken, workerDto);
    }
}