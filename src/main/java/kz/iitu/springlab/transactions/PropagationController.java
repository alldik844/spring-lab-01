
package kz.iitu.springlab.transactions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class PropagationController {

    private final PropagationDemoService demoService;
    private final AuditRepository auditRepository;

    public PropagationController(
            PropagationDemoService demoService,
            AuditRepository auditRepository
    ) {
        this.demoService = demoService;
        this.auditRepository = auditRepository;
    }

    @PostMapping("/required")
    public Map<String, String> testRequired() {
        demoService.testRequired();

        return Map.of("status", "SUCCESS");
    }

    @PostMapping("/requires-new")
    public Map<String, String> testRequiresNew() {
        demoService.testRequiresNew();

        return Map.of("status", "SUCCESS");
    }

    @GetMapping("/audits")
    public List<AuditRecord> getAudits() {
        return auditRepository.findAll();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleError(
            IllegalStateException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", "ROLLBACK",
                        "message", exception.getMessage()
                ));
    }
}
