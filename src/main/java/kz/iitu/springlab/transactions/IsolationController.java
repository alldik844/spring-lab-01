
package kz.iitu.springlab.transactions;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/isolation")
public class IsolationController {

    private final IsolationDemoService service;

    public IsolationController(IsolationDemoService service) {
        this.service = service;
    }

    @PostMapping("/read-committed")
    public Map<String, Object> readCommitted() {
        return service.testReadCommitted();
    }

    @PostMapping("/repeatable-read")
    public Map<String, Object> repeatableRead() {
        return service.testRepeatableRead();
    }
}

