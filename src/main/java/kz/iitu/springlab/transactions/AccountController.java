
package kz.iitu.springlab.transactions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<Account> getAccounts() {
        return service.getAccounts();
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount
    ) {
        service.transfer(fromId, toId, amount);

        return Map.of(
                "status", "SUCCESS",
                "message", "Transfer completed"
        );
    }

    @PostMapping("/rollback-demo")
    public Map<String, String> rollbackDemo(
            @RequestParam Long fromId,
            @RequestParam BigDecimal amount
    ) {
        service.rollbackDemo(fromId, amount);

        return Map.of(
                "status", "SUCCESS"
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<Map<String, String>> handleError(
            RuntimeException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", "ERROR",
                        "message", exception.getMessage() == null
                                ? "Operation failed"
                                : exception.getMessage()
                ));
    }
}
