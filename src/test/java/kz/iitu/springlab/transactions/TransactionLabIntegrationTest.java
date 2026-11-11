
package kz.iitu.springlab.transactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:txlabtests;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
class TransactionLabIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private PropagationDemoService propagationService;

    @Autowired
    private IsolationDemoService isolationService;

    @Autowired
    private OptimisticLockService optimisticLockService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {

        jdbcTemplate.update(
                "UPDATE accounts SET balance = 1000.00, version = 0 WHERE id = 1"
        );

        jdbcTemplate.update(
                "UPDATE accounts SET balance = 500.00, version = 0 WHERE id = 2"
        );

        jdbcTemplate.update(
                "DELETE FROM audit_records"
        );

        jdbcTemplate.update(
                "UPDATE isolation_demo SET amount = 100 WHERE id = 1"
        );
    }

    private void assertBalance(Long id, String expected) {
        Account account = accountRepository.findById(id)
                .orElseThrow();

        assertEquals(
                0,
                new BigDecimal(expected)
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void testSuccessfulTransfer() {

        accountService.transfer(
                1L,
                2L,
                new BigDecimal("100.00")
        );

        assertBalance(1L, "900.00");
        assertBalance(2L, "600.00");
    }

    @Test
    void testInsufficientFunds() {

        assertThrows(
                IllegalStateException.class,
                () -> accountService.transfer(
                        1L,
                        2L,
                        new BigDecimal("5000.00")
                )
        );

        assertBalance(1L, "1000.00");
        assertBalance(2L, "500.00");
    }

    @Test
    void testTransactionRollback() {

        assertThrows(
                IllegalStateException.class,
                () -> accountService.rollbackDemo(
                        1L,
                        new BigDecimal("200.00")
                )
        );

        assertBalance(1L, "1000.00");
        assertBalance(2L, "500.00");
    }

    @Test
    void testRequiredPropagation() {

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.testRequired()
        );

        assertEquals(0, auditRepository.count());

        assertBalance(1L, "1000.00");
    }

    @Test
    void testRequiresNewPropagation() {

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.testRequiresNew()
        );

        assertEquals(1, auditRepository.count());

        assertEquals(
                "REQUIRES_NEW audit - independent transaction",
                auditRepository.findAll().get(0).getMessage()
        );

        assertBalance(1L, "1000.00");
    }

    @Test
    void testReadCommitted() {

        Map<String, Object> result =
                isolationService.testReadCommitted();

        assertEquals("READ_COMMITTED",
                result.get("isolationLevel"));

        assertEquals(100,
                ((Number) result.get("firstRead")).intValue());

        assertEquals(200,
                ((Number) result.get("secondRead")).intValue());

        assertEquals(true, result.get("valueChanged"));
    }

    @Test
    void testRepeatableRead() {

        Map<String, Object> result =
                isolationService.testRepeatableRead();

        assertEquals("REPEATABLE_READ",
                result.get("isolationLevel"));

        assertEquals(100,
                ((Number) result.get("firstRead")).intValue());

        assertEquals(100,
                ((Number) result.get("secondRead")).intValue());

        assertEquals(false, result.get("valueChanged"));
    }

    @Test
    void testOptimisticLocking() {

        Map<String, Object> result =
                optimisticLockService.runDemo();

        assertEquals(
                "CONFLICT_DETECTED",
                result.get("status")
        );

        assertEquals(
                true,
                result.get("staleWriteRejected")
        );

        assertBalance(2L, "510.00");

        Account account = accountRepository.findById(2L)
                .orElseThrow();

        assertEquals(1L, account.getVersion());
    }
}
