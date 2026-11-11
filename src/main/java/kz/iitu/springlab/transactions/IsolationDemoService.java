
package kz.iitu.springlab.transactions;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class IsolationDemoService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    public IsolationDemoService(
            JdbcTemplate jdbcTemplate,
            PlatformTransactionManager transactionManager
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = transactionManager;
    }

    public Map<String, Object> testReadCommitted() {
        return runExperiment(
                TransactionDefinition.ISOLATION_READ_COMMITTED,
                "READ_COMMITTED"
        );
    }

    public Map<String, Object> testRepeatableRead() {
        return runExperiment(
                TransactionDefinition.ISOLATION_REPEATABLE_READ,
                "REPEATABLE_READ"
        );
    }

    private Map<String, Object> runExperiment(
            int isolationLevel,
            String isolationName
    ) {

        TransactionTemplate setup =
                new TransactionTemplate(transactionManager);

        setup.executeWithoutResult(status -> {
            jdbcTemplate.update(
                    "UPDATE isolation_demo SET amount = 100 WHERE id = 1"
            );
        });

        TransactionTemplate reader =
                new TransactionTemplate(transactionManager);

        reader.setIsolationLevel(isolationLevel);

        TransactionTemplate writer =
                new TransactionTemplate(transactionManager);

        writer.setIsolationLevel(
                TransactionDefinition.ISOLATION_READ_COMMITTED
        );

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        try {
            return reader.execute(status -> {

                Integer firstRead =
                        jdbcTemplate.queryForObject(
                                "SELECT amount FROM isolation_demo WHERE id = 1",
                                Integer.class
                        );

                Future<?> writerFuture = executor.submit(() -> {
                    writer.executeWithoutResult(writerStatus -> {
                        jdbcTemplate.update(
                                "UPDATE isolation_demo SET amount = 200 WHERE id = 1"
                        );
                    });
                });

                try {
                    writerFuture.get(15, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "Experiment interrupted", e
                    );
                } catch (ExecutionException | TimeoutException e) {
                    throw new IllegalStateException(
                            "Writer transaction failed", e
                    );
                }

                Integer secondRead =
                        jdbcTemplate.queryForObject(
                                "SELECT amount FROM isolation_demo WHERE id = 1",
                                Integer.class
                        );

                return Map.<String, Object>of(
                        "database", "H2",
                        "isolationLevel", isolationName,
                        "firstRead", firstRead,
                        "writerCommitted", true,
                        "secondRead", secondRead,
                        "valueChanged",
                        !firstRead.equals(secondRead)
                );
            });
        } finally {
            executor.shutdownNow();
        }
    }
}
