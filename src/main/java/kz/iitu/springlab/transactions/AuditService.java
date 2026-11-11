
package kz.iitu.springlab.transactions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository repository;

    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRequiredAudit() {
        AuditRecord record = new AuditRecord(
                "REQUIRED audit - outer transaction rollback"
        );

        repository.saveAndFlush(record);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequiresNewAudit() {
        AuditRecord record = new AuditRecord(
                "REQUIRES_NEW audit - independent transaction"
        );

        repository.saveAndFlush(record);
    }
}
