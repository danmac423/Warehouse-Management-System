package warehouse.warehousemanagementsystem.worker;

import org.springframework.stereotype.Service;
import warehouse.warehousemanagementsystem.exception.BadRequestException;
import warehouse.warehousemanagementsystem.exception.ConflictException;
import warehouse.warehousemanagementsystem.exception.DatabaseException;
import warehouse.warehousemanagementsystem.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {

    private final WorkerDao workerDao;

    public WorkerService(WorkerDao workerDao) {
        this.workerDao = workerDao;
    }

    private void validData(Worker worker) {
        if (worker.name().isEmpty()
                || worker.lastName().isEmpty()
                || worker.username().isEmpty()
                || worker.password().isEmpty()
                || worker.role().isEmpty()) {
            throw new BadRequestException("All fields are required");
        }

        if (!worker.role().equals("ADMIN") && !worker.role().equals("WORKER")) {
            throw new BadRequestException("Role must be either ADMIN or WORKER");
        }
    }

    public List<Worker> getWorkers(String username, String role) {
        return workerDao.getWorkers(username, role);
    }

    public void addWorker(Worker worker) {
        validData(worker);

        if (workerDao.getWorkerByUsername(worker.username()).isPresent()) {
            throw new ConflictException("Worker with this username already exists");
        }

        if (workerDao.addWorker(worker) != 1) {
            throw new DatabaseException("Failed to add worker");
        }
    }

    public void updateWorker(Worker worker) {
        Worker currentWorker = workerDao.getWorkerByUsername(worker.username()).orElseThrow(() -> new BadRequestException("Worker not found"));

        validData(worker);

        if (workerDao.getWorkerByUsername(worker.username()).isPresent() && !currentWorker.username().equals(worker.username())) {
            throw new ConflictException("Worker with this username already exists");
        }

        if (workerDao.updateWorker(worker) != 1) {
            throw new DatabaseException("Failed to update worker");
        }
    }

    public void deleteWorker(Long id) {
        Optional<Worker> worker = workerDao.getWorkerById(id);
        int result;

        if (worker.isEmpty()) {
            throw new NotFoundException("Worker not found");
        }
        try {
            result = workerDao.deleteWorker(id);
        } catch (Exception e) {
            throw new BadRequestException("This worker is still in use");
        }
        if (result != 1) {
            throw new DatabaseException("Failed to delete worker");
        }
    }

}
