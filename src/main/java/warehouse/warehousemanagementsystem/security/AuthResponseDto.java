package warehouse.warehousemanagementsystem.security;

import warehouse.warehousemanagementsystem.worker.WorkerDto;

public record AuthResponseDto(String tokenType, String accessToken, String refreshToken , WorkerDto workerDto) {
    public AuthResponseDto(String accessToken, String refreshToken, WorkerDto workerDto) {
        this("Bearer", accessToken, refreshToken, workerDto);
    }
}