package warehouse.warehousemanagementsystem.ordersHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse.warehousemanagementsystem.order.Order;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders-history")
public class OrdersHistoryController {
    private final OrdersHistoryService ordersHistoryService;

    @Autowired
    public OrdersHistoryController(OrdersHistoryService ordersHistoryService) {
        this.ordersHistoryService = ordersHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam (required = false) String customerEmail,
            @RequestParam (required = false) String workerUsername
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ordersHistoryService.getOrders(customerEmail, workerUsername));
    }


//    @GetMapping
//    public ResponseEntity<List<OrdersHistory>> getAllOrders() {
//        return new ResponseEntity<>(ordersHistoryService.getAllOrders(), HttpStatus.OK);
//    }

//    @GetMapping("/dates/{processedDateMin}/{processedDateMax}")
//    public List<OrdersHistory> getAll0rdersWithDates(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable Date processedDateMin,
//                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable Date processedDateMax){
//        return ordersHistoryService.getAllOrdersWithDate(processedDateMin, processedDateMax);
//
//    }


    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Long orderId) {
        return ordersHistoryService.getOrderById(orderId);
    }

    @GetMapping("/worker/{workerId}")
    public List<OrdersHistory> getOrdersByWorker(@PathVariable Long workerId) {
        return ordersHistoryService.getOrdersByWorker(workerId);
    }

    @GetMapping("/worker/{workerId}/{processedDateMin}/{processedDateMax}")
    public List<OrdersHistory> getOrdersByWorkerWithDates(@PathVariable Long workerId,
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable Date processedDateMin,
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable Date processedDateMax) {
        return ordersHistoryService.getOrderByWorkerWithDates(workerId, processedDateMin, processedDateMax);
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
