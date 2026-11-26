
package kz.iitu.springlab.integration;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Testcontainers
class PostgresIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void connectsToRealPostgreSQL() {

        String version = jdbcTemplate.queryForObject(
                "SELECT version()",
                String.class
        );

        assertThat(version)
                .contains("PostgreSQL");
    }

    @Test
    void flywayMigrationsAreApplied() {

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = true
                """,
                Integer.class
        );

        assertThat(count)
                .isNotNull()
                .isGreaterThanOrEqualTo(6);
    }

    @Test
    void accountCanBeCreatedAndReadBack() {

        jdbcTemplate.update(
                """
                INSERT INTO accounts
                (owner_name, balance, version, status)
                VALUES (?, ?, 0, 'ACTIVE')
                """,
                "Lab12 Test",
                new BigDecimal("123.45")
        );

        BigDecimal balance = jdbcTemplate.queryForObject(
                """
                SELECT balance
                FROM accounts
                WHERE owner_name = ?
                """,
                BigDecimal.class,
                "Lab12 Test"
        );

        assertThat(balance)
                .isEqualByComparingTo("123.45");
    }

    @Test
    void adminCreatesThenReadsBackBook() throws Exception {

        String json = """
                {
                    "title": "Lab12 HTTP Test Book",
                    "author": "Lab12",
                    "year": 2026
                }
                """;

        var result = mockMvc.perform(
                        post("/api/books")
                                .with(user("admin").roles("ADMIN", "USER"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Lab12 HTTP Test Book"))
                .andReturn();

        String location = result
                .getResponse()
                .getHeader("Location");

        assertThat(location).isNotBlank();

        mockMvc.perform(
                        get(location)
                                .with(user("admin").roles("ADMIN", "USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Lab12 HTTP Test Book"));
    }
}
