package warehouse.warehousemanagementsystem.orderView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.order.Order;

import java.util.List;

@RestController
@RequestMapping("/api/ordersViews")
public class OrderViewController {
    private final OrderViewService orderService;

    @Autowired
    public OrderViewController(OrderViewService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderViewGeneral>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/workerUsername/{usernameSubstring}")
    public ResponseEntity<List<OrderView>> getOrdersByWorkerUsernameSubstring(@PathVariable String usernameSubstring) {
        return new ResponseEntity<>(orderService.getOrdersByWorkerUsernameSubstring(usernameSubstring), HttpStatus.OK);
    }

    @GetMapping("/customerEmail/{emailSubstring}")
    public ResponseEntity<List<OrderView>> getOrdersByCustomerEmailSubstring(@PathVariable String emailSubstring) {
        return new ResponseEntity<>(orderService.getOrdersByCustomerEmailSubstring(emailSubstring), HttpStatus.OK);
    }

    @GetMapping("/customerEmail/{emailSubstring}/WorkerUsername/{usernameSubstring}")
    public ResponseEntity<List<OrderView>> getOrdersByCustomerEmailWorkerUsernameSubstring(@PathVariable String emailSubstring, @PathVariable String usernameSubstring) {
        return new ResponseEntity<>(orderService.getOrdersByCustomerEmailWorkerUsernameSubstring(emailSubstring, usernameSubstring), HttpStatus.OK);
    }

    @GetMapping("/worker/{workerId}")
    public List<OrderView> getOrdersByWorker(@PathVariable Long workerId) {
        return orderService.getOrdersViewsByWorker(workerId);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderView> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersViewsByCustomer(customerId);
    }

    @GetMapping("/orderId/{orderId}")
    public List<OrderView> getOrdersByOrderId(@PathVariable Long orderId) {
        return orderService.getOrdersViewsByCustomer(orderId);
    }

}
