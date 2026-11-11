
package kz.iitu.springlab.transactions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class OptimisticLockWorker {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Account loadDetached(Long accountId) {

        Account account = entityManager.find(
                Account.class,
                accountId
        );

        if (account == null) {
            throw new NoSuchElementException(
                    "Account not found: " + accountId
            );
        }

        entityManager.detach(account);

        return account;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long saveDetached(Account account) {

        Account managed = entityManager.merge(account);

        entityManager.flush();

        return managed.getVersion();
    }
}

