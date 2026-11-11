
package kz.iitu.springlab.transactions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PropagationDemoService {

    private final AccountRepository accountRepository;
    private final AuditService auditService;

    public PropagationDemoService(
            AccountRepository accountRepository,
            AuditService auditService
    ) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void testRequired() {

        Account account = accountRepository.findById(1L)
                .orElseThrow();

        account.debit(new BigDecimal("10.00"));

        accountRepository.saveAndFlush(account);

        auditService.saveRequiredAudit();

        throw new IllegalStateException(
                "Outer transaction failed: REQUIRED rollback"
        );
    }

    @Transactional
    public void testRequiresNew() {

        Account account = accountRepository.findById(1L)
                .orElseThrow();

        account.debit(new BigDecimal("10.00"));

        accountRepository.saveAndFlush(account);

        auditService.saveRequiresNewAudit();

        throw new IllegalStateException(
                "Outer transaction failed: REQUIRES_NEW audit committed"
        );
    }
}
