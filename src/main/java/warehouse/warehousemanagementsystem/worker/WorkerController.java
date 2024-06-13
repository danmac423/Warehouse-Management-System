package warehouse.warehousemanagementsystem.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        return new ResponseEntity<>(workerService.getAllWorkers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long id){
        Optional<Worker> worker = workerService.getWorkerById(id);
        if(worker.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(worker.get(), HttpStatus.OK);
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

}
