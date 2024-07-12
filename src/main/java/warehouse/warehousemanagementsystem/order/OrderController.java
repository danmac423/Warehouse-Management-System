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
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(required = false) String workerUsername,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workerId
    ) {
        List<OrderDto> orders = orderService.getOrders(workerUsername, customerEmail, status, workerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @PutMapping("/pack")
    public ResponseEntity<String> packOrder(@RequestBody OrderDto order) {
        orderService.packOrder(order);
        return ResponseEntity.status(HttpStatus.OK).body("Order packed successfully and added to the history");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/assign")
    public ResponseEntity<OrderDto> assignOrder(@RequestBody OrderDto order) {
        OrderDto assignedOrder = orderService.assignOrder(order);
        return ResponseEntity.status(HttpStatus.OK).body(assignedOrder);
    }

}
