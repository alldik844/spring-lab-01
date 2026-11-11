
package kz.iitu.springlab.transactions;

import jakarta.persistence.OptimisticLockException;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OptimisticLockService {

    private final OptimisticLockWorker worker;

    public OptimisticLockService(OptimisticLockWorker worker) {
        this.worker = worker;
    }

    public Map<String, Object> runDemo() {

        Long accountId = 2L;

        Account firstCopy = worker.loadDetached(accountId);
        Account secondCopy = worker.loadDetached(accountId);

        Long originalVersion = firstCopy.getVersion();
        BigDecimal originalBalance = firstCopy.getBalance();

        firstCopy.credit(new BigDecimal("10.00"));

        Long committedVersion = worker.saveDetached(firstCopy);

        secondCopy.credit(new BigDecimal("50.00"));

        String conflictType;

        try {
            worker.saveDetached(secondCopy);

            throw new IllegalStateException(
                    "Optimistic locking conflict was not detected"
            );

        } catch (OptimisticLockingFailureException
                 | OptimisticLockException exception) {

            conflictType = exception.getClass().getSimpleName();
        }

        Account finalAccount = worker.loadDetached(accountId);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("status", "CONFLICT_DETECTED");
        result.put("account", finalAccount.getOwnerName());
        result.put("originalBalance", originalBalance);
        result.put("firstUpdate", "+10.00");
        result.put("secondAttempt", "+50.00");
        result.put("finalBalance", finalAccount.getBalance());
        result.put("originalVersion", originalVersion);
        result.put("committedVersion", committedVersion);
        result.put("staleVersion", secondCopy.getVersion());
        result.put("finalVersion", finalAccount.getVersion());
        result.put("exception", conflictType);
        result.put("staleWriteRejected", true);

        return result;
    }

    public Account getCurrentAccount() {
        return worker.loadDetached(2L);
    }
}

