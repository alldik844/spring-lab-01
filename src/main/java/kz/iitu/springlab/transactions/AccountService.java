
package kz.iitu.springlab.transactions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Account> getAccounts() {
        return repository.findAll(Sort.by("id"));
    }

    @Transactional
    public void transfer(
            Long fromId,
            Long toId,
            BigDecimal amount
    ) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Accounts must be different"
            );
        }

        Account from = repository.findById(fromId)
                .orElseThrow();

        Account to = repository.findById(toId)
                .orElseThrow();

        from.debit(amount);
        to.credit(amount);

        repository.save(from);
        repository.save(to);
    }

    @Transactional
    public void rollbackDemo(
            Long fromId,
            BigDecimal amount
    ) {
        Account from = repository.findById(fromId)
                .orElseThrow();

        from.debit(amount);

        repository.saveAndFlush(from);

        throw new IllegalStateException(
                "Simulated failure: transaction rollback"
        );
    }
}
