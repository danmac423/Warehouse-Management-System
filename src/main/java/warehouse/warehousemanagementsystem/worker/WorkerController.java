package warehouse.warehousemanagementsystem.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<WorkerDto>> getWorkers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(workerService.getWorkers(username, role));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WorkerDto> getWorkerById(@PathVariable Long id){
        Optional<WorkerDto> worker = workerService.getWorkerById(id);
        return worker.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return new ResponseEntity<>("Worker deleted successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<String> updateWorker(@RequestBody WorkerDto worker) {
        workerService.updateWorker(worker);
        return new ResponseEntity<>("Worker updated successfully", HttpStatus.OK);
    }


}
