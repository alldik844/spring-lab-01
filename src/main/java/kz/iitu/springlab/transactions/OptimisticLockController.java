
package kz.iitu.springlab.transactions;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/locking")
public class OptimisticLockController {

    private final OptimisticLockService service;

    public OptimisticLockController(
            OptimisticLockService service
    ) {
        this.service = service;
    }

    @PostMapping("/demo")
    public Map<String, Object> runDemo() {
        return service.runDemo();
    }

    @GetMapping("/current")
    public Account getCurrentAccount() {
        return service.getCurrentAccount();
    }
}
