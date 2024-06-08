package warehouse.warehousemanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class LoginNotFoundException extends RuntimeException {

    public LoginNotFoundException(String message) {
        super(message);
    }
}
