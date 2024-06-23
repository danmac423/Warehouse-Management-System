package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{orderId}")
    public OrdersHistory getOrderById(@PathVariable Long orderId) {
        return ordersHistoryService.getOrderById(orderId);
    }

    @GetMapping("/worker/{workerId}")
    public List<OrdersHistory> getOrdersByWorker(@PathVariable Long workerId) {
        return ordersHistoryService.getOrdersByWorker(workerId);
    }

    @GetMapping("/customer/{email}")
    public List<OrdersHistory> getOrdersByCustomer(@PathVariable String email) {
        return ordersHistoryService.getOrdersByCustomer(email);
    }

    @GetMapping("/formated")
    public List<OrdersHistoryView> getOrdersViews() {
        return ordersHistoryService.getAllOrdersViews();
    }

    @GetMapping("/formated/email/{email}")
    public List<OrdersHistoryView> getOrdersViewByCustomer(@PathVariable String email) {
        return ordersHistoryService.getOrdersViewByEmail(email);
    }

    @GetMapping("/formated/username/{username}")
    public List<OrdersHistoryView> getOrderViewByUsername(@PathVariable String username) {
        return ordersHistoryService.getOrdersViewByUsernameSubstr(username);
    }

    @GetMapping("/formated/email/{email}/username/{username}")
    public List<OrdersHistoryView> getOrderViewByUsernameAndEmail(@PathVariable String email, @PathVariable String username) {
        return ordersHistoryService.getOrdersViewByEmailAndUsername(email, username);
    }

    @GetMapping("/formated/orderId/{orderId}")
    public List<OrdersHistoryView> getOrderViewByOrderId(@PathVariable Long orderId) {
        return ordersHistoryService.getOrdersViewByOrderId(orderId);
    }
}
