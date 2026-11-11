
CREATE TABLE isolation_demo (
                                id BIGINT PRIMARY KEY,
                                amount INT NOT NULL
);

INSERT INTO isolation_demo (id, amount)
VALUES (1, 100);
