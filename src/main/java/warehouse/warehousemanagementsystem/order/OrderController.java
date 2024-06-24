package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workerId
    ) {
        List<Order> orders = orderService.getOrders(workerUsername, customerEmail, status, workerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @PutMapping("/pack")
    public ResponseEntity<String> packOrder(@RequestBody Order order) {
        orderService.packOrder(order);
        return ResponseEntity.status(HttpStatus.OK).body("Order packed successfully and added to the history");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/assign")
    public ResponseEntity<Order> assignOrder(@RequestBody Order order) {
        Order assignedOrder = orderService.assignOrder(order);
        return ResponseEntity.status(HttpStatus.OK).body(assignedOrder);
    }

}
