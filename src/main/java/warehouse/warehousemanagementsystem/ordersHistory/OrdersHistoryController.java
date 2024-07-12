package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.order.OrderDto;

import java.util.List;

@RestController
@RequestMapping("/api/orders-history")
public class OrdersHistoryController {
    private final OrdersHistoryService ordersHistoryService;

    @Autowired
    public OrdersHistoryController(OrdersHistoryService ordersHistoryService) {
        this.ordersHistoryService = ordersHistoryService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam (required = false) String customerEmail,
            @RequestParam (required = false) String workerUsername
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ordersHistoryService.getOrders(customerEmail, workerUsername));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return ordersHistoryService.getOrderById(orderId);
    }
}
