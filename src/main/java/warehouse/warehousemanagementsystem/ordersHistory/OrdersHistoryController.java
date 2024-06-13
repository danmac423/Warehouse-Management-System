package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/ordersHistory")
public class OrdersHistoryController {
    private final OrdersHistoryService ordersHistoryService;

    @Autowired
    public OrdersHistoryController(OrdersHistoryService ordersHistoryService) {
        this.ordersHistoryService = ordersHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<OrdersHistory>> getAllOrders() {
        return new ResponseEntity<>(ordersHistoryService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/worker/{workerId}")
    public List<OrdersHistory> getOrdersByWorker(@PathVariable Long workerId) {
        return ordersHistoryService.getOrdersByWorker(workerId);
    }

    @GetMapping("/worker/{workerId}/{processedDateMin};{processedDateMax}")
    public List<OrdersHistory> getOrdersByWorkerWithDates(@PathVariable Long workerId,
                                                          @PathVariable Date processedDateMin,
                                                          @PathVariable Date processedDateMax) {
        return ordersHistoryService.getOrdersByWorker(workerId);
    }

    @GetMapping("/customer/{email}")
    public List<OrdersHistory> getOrdersByCustomer(@PathVariable String email) {
        return ordersHistoryService.getOrdersByCustomer(email);
    }
}
