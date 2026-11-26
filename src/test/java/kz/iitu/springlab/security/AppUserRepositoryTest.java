
package kz.iitu.springlab.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AppUser createUser(String username) {

        AppUser user = new AppUser();

        ReflectionTestUtils.setField(
                user, "username", username
        );

        ReflectionTestUtils.setField(
                user, "passwordHash", "test-hash"
        );

        ReflectionTestUtils.setField(
                user, "enabled", true
        );

        user.getRoles().add("USER");

        return user;
    }

    @Test
    void findsUserSavedInDatabase() {

        AppUser user = createUser("lab12_alice");

        entityManager.persistAndFlush(user);
        entityManager.clear();

        var result = repository.findByUsername(
                "lab12_alice"
        );

        assertThat(result).isPresent();

        assertThat(result.orElseThrow().getUsername())
                .isEqualTo("lab12_alice");

        assertThat(result.orElseThrow().getRoles())
                .contains("USER");
    }

    @Test
    void queryExcludesOtherUsers() {

        AppUser alice = createUser("lab12_alice");
        AppUser bob = createUser("lab12_bob");

        entityManager.persist(alice);
        entityManager.persist(bob);

        entityManager.flush();
        entityManager.clear();

        var result = repository.findByUsername(
                "lab12_alice"
        );

        assertThat(result).isPresent();

        assertThat(result.orElseThrow().getUsername())
                .isEqualTo("lab12_alice");

        assertThat(result.orElseThrow().getUsername())
                .isNotEqualTo("lab12_bob");

        assertThat(
                repository.findByUsername("lab12_missing")
        ).isEmpty();
    }
}
