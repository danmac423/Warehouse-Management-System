package warehouse.warehousemanagementsystem.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        return new ResponseEntity<>(workerService.getAllWorkers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addWorker(@RequestBody Worker worker) {
        workerService.addWorker(worker);
        return new ResponseEntity<>("Worker added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return new ResponseEntity<>("Worker deleted successfully", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateWorker(@RequestBody Worker worker) {
        workerService.updateWorker(worker);
        return new ResponseEntity<>("Worker updated successfully", HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<Worker>> getWorkersByUsername(@PathVariable String username) {
        return new ResponseEntity<>(workerService.getWorkersByUsername(username), HttpStatus.OK);
    }


}
