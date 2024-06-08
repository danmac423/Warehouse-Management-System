package warehouse.warehousemanagementsystem.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        orderService.addOrder(order);
        return new ResponseEntity<>("Order added successfully", HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
        return new ResponseEntity<>("Order updated successfully", HttpStatus.OK);
    }

//    @GetMapping("/worker/{workerId}")
//    public List<Order> getOrdersByWorker(@PathVariable Long workerId) {
//        return orderService.getOrdersByWorker(workerId);
//    }

}
