
package kz.iitu.springlab.notify;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(NotificationSpyIntegrationTest.TestConfig.class)
class NotificationSpyIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import({
            NotificationService.class,
            EmailNotifier.class,
            ConsoleNotifier.class,
            Base64Notifier.class,
            NoopNotifier.class
    })
    static class TestConfig {
    }

    @Autowired
    private NotificationService service;

    @MockitoSpyBean(name = "email")
    private Notifier emailNotifier;

    @Test
    void disabledNotificationsAreNotSentButEnabledAreSent() {

        String disabledResult = service.notifyReader(
                "reader-1",
                "Important!",
                false
        );

        assertThat(disabledResult)
                .isEqualTo("notifications disabled");

        verify(emailNotifier, never()).send(anyString());

        String enabledResult = service.notifyReader(
                "reader-1",
                "Hello",
                true
        );

        assertThat(enabledResult)
                .isEqualTo("email: Hello");

        verify(emailNotifier).send("Hello");
    }
}
